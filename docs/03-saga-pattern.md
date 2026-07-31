# Documentación — Saga Pattern Orquestado

© 2026 Abel Gomez. Todos los derechos reservados.

---

## 1. Introducción

El patrón Saga es un mecanismo para gestionar transacciones distribuidas
que abarcan múltiples microservicios. En lugar de usar una transacción
monolítica (2PC), cada paso de la saga se ejecuta de forma independiente.
Si un paso falla, se ejecutan **compensating transactions** en orden inverso
para deshacer los cambios ya realizados.

FinCore implementa un **Saga Orquestado**: una clase central
(`TransferenciaSagaOrchestrator`) coordina los 12 pasos, decide qué
compensaciones ejecutar y gestiona los timeouts y reintentos.

---

## 2. Los 12 Pasos de la Saga

### Paso 1: VALIDAR_DATOS

**Responsabilidad:** Verificar la integridad de los datos de la transferencia.

**Validaciones:**
- Formato de cuenta destino válido (20 dígitos, prefijo 2026)
- Cuenta origen existe y está activa
- Cuenta destino existe y está activa
- Monto > 0
- Monto ≤ límite por transacción
- Moneda coincide entre cuentas
- Cuenta origen ≠ cuenta destino

**Si falla:** Estado → `RECHAZADA` (no hay compensación necesaria)

**Servicio responsable:** `transfer-service` (validaciones locales)

---

### Paso 2: VERIFICAR_KYC

**Responsabilidad:** Verificar que el cliente cumple con requisitos KYC/AML.

**Validaciones:**
- Cliente existe y está en estado `ACTIVO`
- Documento de identidad vigente (no expirado)
- KYC aprobado (`estado_kyc = APROBADO`)
- No está en lista de sanciones AML (consulta a `fraud-service`)

**Si falla:** Estado → `RECHAZADA`

**Servicio responsable:** `customer-service` (consulta a `fraud-service` vía gRPC)

---

### Paso 3: VALIDAR_LIMITES

**Responsabilidad:** Verificar que la transferencia respeta los límites.

**Validaciones:**
- Límite diario acumulado no excedido
- Límite por transacción no excedido
- Límite mensual acumulado no excedido
- Contador de transacciones del día actualizado

**Si falla:** Estado → `RECHAZADA`

**Servicio responsable:** `account-service` (consulta `limites_transaccion`)

---

### Paso 4: EVALUAR_FRAUDE

**Responsabilidad:** Evaluar el riesgo de la transferencia.

**Proceso:**
1. Transfer-Service llama a `fraud-service` vía gRPC
2. Fraud-Service aplica las 10 reglas de fraude
3. Calcula score total (suma de puntos de reglas activadas)
4. Retorna decisión:

| Score | Decisión |
|-------|----------|
| 0-29  | APROBADO automáticamente |
| 30-69 | EN_REVISION (esperar aprobación manual) |
| 70+   | RECHAZADO automáticamente |

**Si falla:** Estado → `RECHAZADA` o `EN_REVISION`

**Servicio responsable:** `fraud-service`

**Compensación:** No aplica (es una consulta)

---

### Paso 5: RESERVAR_FONDOS

**Responsabilidad:** Reservar los fondos en la cuenta origen.

**Acciones:**
- Incrementar `saldo_retenido` en cuenta origen
- Decrementar `saldo_disponible` en cuenta origen
- Crear asiento contable de retención:
  ```
  DÉBITO  2002 - Dep. ahorros Abel    $200.00
  CRÉDITO 2100 - Fondos en tránsito   $200.00
  ```

**Si falla:** Estado → `RECHAZADA`
**Compensación:** No aplica (no se reservó nada)

**Servicio responsable:** `account-service` (gRPC → `transfer-service`)
**Servicio secundario:** `ledger-service` (asiento de retención)

---

### Paso 6: CREAR_EVENTO_KAFKA

**Responsabilidad:** Publicar evento de inicio de transferencia.

**Evento:** `transferencia.iniciada`
- Contiene: ID de transferencia, cuentas, monto, traceId
- Topics: `transferencia.iniciada`

**Si falla:** Estado → `RECHAZADA`
**Compensación:** `LiberarReservaCompensation` (liberar fondos reservados)

**Servicio responsable:** `transfer-service`

---

### Paso 7: EJECUTAR_DEBITO

**Responsabilidad:** Debitar la cuenta origen en el ledger.

**Acciones:**
- Crear asiento contable de débito:
  ```
  DÉBITO  2002 - Dep. ahorros Abel    $200.00
  CRÉDITO 2100 - Fondos en tránsito   $200.00
  ```
- Decrementar `saldo_contable` en cuenta origen
- Decrementar `saldo_retenido` en cuenta origen

**Si falla:** Estado → `ERROR`
**Compensación:** 
1. `LiberarReservaCompensation` (liberar retención)
2. `RevertirEventoKafkaCompensation` (publicar evento de reversión)

**Servicio responsable:** `ledger-service` (gRPC → `transfer-service`)
**Servicio secundario:** `account-service` (actualizar saldos)

---

### Paso 8: EJECUTAR_CREDITO

**Responsabilidad:** Acreditar la cuenta destino en el ledger.

**Acciones:**
- Crear asiento contable de crédito:
  ```
  DÉBITO  2100 - Fondos en tránsito   $200.00
  CRÉDITO 2002 - Dep. ahorros María   $200.00
  ```
- Incrementar `saldo_contable` en cuenta destino
- Incrementar `saldo_disponible` en cuenta destino

**Si falla:** Estado → `ERROR`
**Compensación:**
1. `RevertirDebitoCompensation` (revertir débito)
2. `LiberarReservaCompensation` (liberar retención)
3. `RevertirEventoKafkaCompensation` (publicar evento de reversión)

**Servicio responsable:** `ledger-service` (gRPC → `transfer-service`)
**Servicio secundario:** `account-service` (actualizar saldos)

---

### Paso 9: LIBERAR_RETENCION

**Responsabilidad:** Limpiar la retención de fondos.

**Acciones:**
- Decrementar `saldo_retenido` en cuenta origen
- Limpiar fondos en tránsito

**Si falla:** Estado → `ERROR`
**Compensación:** Proceso manual (inconsistencia contable — requiere intervención)

**Servicio responsable:** `account-service` (gRPC → `transfer-service`)

---

### Paso 10: REGISTRAR_AUDITORIA

**Responsabilidad:** Persistir el registro completo en audit-service.

**Datos registrados:**
- TraceId, SpanId
- Usuario, IP, dispositivo
- Request completo (sanitizado)
- Response con código y tiempo
- Detalle del paso de la saga

**Si falla:** Estado → continúa (log de error, no bloquea)
**Compensación:** No aplica (es un registro inmutable)

**Servicio responsable:** `audit-service` (consume evento Kafka)

---

### Paso 11: COBRAR_COMISION

**Responsabilidad:** Cobrar la comisión por transferencia (si aplica).

**Acciones:**
- Crear asiento contable de comisión:
  ```
  DÉBITO  2002 - Dep. ahorros Abel    $2.50
  CRÉDITO 4020 - Comisiones           $2.50
  ```
- Decrementar `saldo_contable` y `saldo_disponible`

**Si falla:** Estado → log de error (comisión se cobra en batch nocturno)
**Compensación:** No aplica (es un cobro secundario)

**Servicio responsable:** `ledger-service` + `account-service`

---

### Paso 12: NOTIFICAR

**Responsabilidad:** Notificar a ambas partes la transferencia completada.

**Acciones:**
- Email a origen: "Transferencia enviada $200 a María López"
- Push a origen: notificación móvil
- Email a destino: "Recibiste $200 de Abel Gomez"
- Push a destino: notificación móvil
- Publicar evento Kafka: `transferencia.completada`

**Si falla:** Estado → log (notificación se reintenta)
**Compensación:** No aplica (es una notificación post-completado)

**Servicio responsable:** `notification-service` (consume evento Kafka)

---

## 3. Diagrama de Transiciones de Estado

```
                    ┌─────────────┐
                    │  PENDIENTE  │
                    └──────┬──────┘
                           │
                           ▼
                    ┌─────────────┐
                    │ VALIDANDO   │
                    └──────┬──────┘
                           │
              ┌────────────┴────────────┐
              │                         │
              ▼                         ▼
       ┌─────────────┐          ┌─────────────┐
       │ AUTORIZADA  │          │ RECHAZADA   │
       └──────┬──────┘          └─────────────┘
              │
       ┌──────┴──────┐
       │             │
       ▼             ▼
┌─────────────┐ ┌─────────────┐
│ EN_REVISION │ │ RESERVANDO  │
└──────┬──────┘ └──────┬──────┘
       │               │
       │               ▼
       │        ┌─────────────┐
       │        │ PROCESANDO  │
       │        └──────┬──────┘
       │               │
       │               ▼
       │        ┌─────────────┐
       │        │ ACREDITANDO │
       │        └──────┬──────┘
       │               │
       │        ┌──────┴──────┐
       │        │             │
       │        ▼             ▼
       │ ┌─────────────┐ ┌─────────────┐
       │ │ COMPLETADA  │ │ ERROR       │
       │ └──────┬──────┘ └──────┬──────┘
       │        │               │
       └────────┘               ▼
                          ┌─────────────┐
                          │ REVERTIDA   │
                          └─────────────┘
```

### Transiciones Permitidas

| Desde         | Hacia             | Condición                    |
|---------------|-------------------|------------------------------|
| PENDIENTE     | VALIDANDO         | Inicio de saga               |
| VALIDANDO     | AUTORIZADA        | Validaciones pasan           |
| VALIDANDO     | RECHAZADA         | Validación falla             |
| AUTORIZADA    | EN_REVISION       | Score 30-69                  |
| AUTORIZADA    | RESERVANDO        | Score 0-29                   |
| EN_REVISION   | RESERVANDO        | Aprobado por supervisor      |
| EN_REVISION   | RECHAZADA         | Rechazado por supervisor     |
| RESERVANDO    | PROCESANDO        | Fondos reservados            |
| RESERVANDO    | ERROR             | Fallo en reserva             |
| PROCESANDO    | ACREDITANDO       | Débito ejecutado             |
| PROCESANDO    | ERROR             | Fallo en débito              |
| ACREDITANDO   | COMPLETADA        | Crédito ejecutado            |
| ACREDITANDO   | ERROR             | Fallo en crédito             |
| ERROR         | REVERTIDA         | Compensación activada        |
| COMPLETADA    | REVERTIDA         | Solo supervisor puede revertir|

---

## 4. Compensating Transactions

Cuando un paso falla, se ejecutan compensaciones en orden inverso:

| Paso Fallido | Compensaciones (orden inverso) |
|--------------|-------------------------------|
| RESERVAR_FONDOS | — (no hay nada que compensar) |
| CREAR_EVENTO_KAFKA | LiberarReservaCompensation |
| EJECUTAR_DEBITO | RevertirEventoKafkaCompensation, LiberarReservaCompensation |
| EJECUTAR_CREDITO | RevertirDebitoCompensation, RevertirEventoKafkaCompensation, LiberarReservaCompensation |
| LIBERAR_RETENCION | RevertirCreditoCompensation, RevertirDebitoCompensation, RevertirEventoKafkaCompensation, LiberarReservaCompensation |

### Detalle de Compensaciones

#### LiberarReservaCompensation
- Decrementa `saldo_retenido` en cuenta origen
- Incrementa `saldo_disponible` en cuenta origen
- Crea asiento de reversión de retención

#### RevertirDebitoCompensation
- Incrementa `saldo_contable` en cuenta origen
- Incrementa `saldo_retenido` en cuenta origen
- Crea asiento de reversión del débito

#### RevertirCreditoCompensation
- Decrementa `saldo_contable` en cuenta destino
- Decrementa `saldo_disponible` en cuenta destino
- Crea asiento de reversión del crédito

#### RevertirEventoKafkaCompensation
- Publica evento `transferencia.revertida` en Kafka
- Notifica a audit-service y notification-service

---

## 5. Timeout y Reintentos

### Timeout
- **Default:** 120 segundos (`saga.timeout.seconds`)
- Si la saga no completa en este tiempo, se marca como `ERROR`
- Se activa la compensación automática

### Reintentos
- **Max reintentos por paso:** 3 (`saga.max.retries`)
- **Delay entre reintentos:** 500ms con backoff exponencial
- Los reintentos solo aplican a pasos idempotentas (no a reservas ni débitos)

---

## 6. Implementación en Código

### Clase Principal: TransferenciaSagaOrchestrator

```java
@Service
@Slf4j
public class TransferenciaSagaOrchestrator {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SagaResult ejecutarSaga(Transferencia transferencia) {
        try {
            ejecutarPaso(VALIDAR_DATOS, transferencia);
            ejecutarPaso(VERIFICAR_KYC, transferencia);
            ejecutarPaso(VALIDAR_LIMITES, transferencia);
            ejecutarPaso(EVALUAR_FRAUDE, transferencia);
            ejecutarPaso(RESERVAR_FONDOS, transferencia);
            ejecutarPaso(CREAR_EVENTO_KAFKA, transferencia);
            ejecutarPaso(EJECUTAR_DEBITO, transferencia);
            ejecutarPaso(EJECUTAR_CREDITO, transferencia);
            ejecutarPaso(LIBERAR_RETENCION, transferencia);
            ejecutarPaso(REGISTRAR_AUDITORIA, transferencia);
            ejecutarPaso(COBRAR_COMISION, transferencia);
            ejecutarPaso(NOTIFICAR, transferencia);

            return SagaResult.success();
        } catch (SagaStepException e) {
            compensar(transferencia, e.getPasoFallido());
            return SagaResult.error(e);
        }
    }
}
```

### Registro de Estado

Cada cambio de estado se registra en `transferencia_estados`:

| Campo | Descripción |
|-------|-------------|
| `id_transferencia` | FK a la transferencia |
| `estado_anterior` | Estado antes del cambio |
| `estado_nuevo` | Estado después del cambio |
| `paso_saga` | Paso de la saga que causó el cambio |
| `descripcion` | Detalle del cambio |
| `error_detalle` | Si falló, el mensaje de error |
| `fecha_cambio` | Timestamp inmutable |

---

## 7. Relación con Kafka

La saga publica eventos en estos topics:

| Evento | Topic | Cuándo |
|--------|-------|--------|
| TransferenciaIniciadaEvent | `transferencia.iniciada` | Paso 6 |
| TransferenciaValidadaEvent | `transferencia.validada` | Paso 1-3 completados |
| FondosReservadosEvent | `transferencia.fondos.reservados` | Paso 5 completado |
| DebitoEjecutadoEvent | `transferencia.debito.ejecutado` | Paso 7 completado |
| CreditoEjecutadoEvent | `transferencia.credito.ejecutado` | Paso 8 completado |
| TransferenciaCompletadaEvent | `transferencia.completada` | Paso 12 |
| TransferenciaRechazadaEvent | `transferencia.rechazada` | Cualquier rechazo |
| TransferenciaRevertidaEvent | `transferencia.revertida` | Compensación |
| TransferenciaEnRevisionEvent | `transferencia.en.revision` | Paso 4, score 30-69 |

Los topics consumen:
- **audit-service**: todos los eventos → trazabilidad completa
- **notification-service**: completada, rechazada, revertida → emails
- **ledger-service**: iniciada, fondos.reservados, debito, credito → asientos
- **account-service**: fondos.reservados, debito, credito → saldos
- **fraud-service**: completada → actualización de perfil transaccional
