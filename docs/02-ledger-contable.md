# Documentación — Ledger de Doble Partida

© 2026 Abel Gomez. Todos los derechos reservados.

---

## 1. Introducción

El ledger de doble partida es el módulo más importante de FinCore. Es la base
contable del banco, donde TODA transacción financiera genera un asiento
contable que debe cumplir la ecuación fundamental:

```
ACTIVOS = PASIVOS + PATRIMONIO
```

Todo asiento contable tiene débitos = créditos exactamente. Si no cuadran,
la operación es rechazada con `UnbalancedEntryException`.

Los asientos son **INMUTABLES**: nunca se actualizan ni eliminan. Si una
transacción necesita ser revertida, se crea un nuevo asiento de reversión.

---

## 2. Plan de Cuentas Contables

### 2.1 Estructura

```
1xxx — ACTIVOS (lo que el banco TIENE)
  1001 - Caja y efectivo
  1010 - Depósitos en Banco Central
  1020 - Inversiones
  1100 - Cartera de créditos
  1200 - Cuentas por cobrar

2xxx — PASIVOS (lo que el banco DEBE a clientes)
  2001 - Depósitos en cuenta corriente
  2002 - Depósitos en cuenta ahorros
  2010 - Depósitos a plazo fijo
  2100 - Fondos en tránsito
  2200 - Retenciones

3xxx — PATRIMONIO
  3001 - Capital social
  3100 - Reservas

4xxx — INGRESOS
  4001 - Intereses ganados en créditos
  4010 - Comisiones por servicios
  4020 - Comisiones por transferencias

5xxx — GASTOS
  5001 - Intereses pagados en depósitos
  5010 - Gastos operativos
```

### 2.2 Naturaleza de las Cuentas

| Tipo       | Naturaleza | Débito | Crédito |
|------------|------------|--------|---------|
| ACTIVO     | DEUDORA    | +      | -       |
| PASIVO     | ACREEDORA  | -      | +       |
| PATRIMONIO | ACREEDORA  | -      | +       |
| INGRESO    | DEUDORA    | +      | -       |
| GASTO      | ACREEDORA  | -      | +       |

---

## 3. Ejemplos de Asientos Reales

### 3.1 Apertura de Cuenta (Depósito Inicial)

Cliente deposita $1,000 en cuenta de ahorros:

```
Asiento: AS-2026-000001
  DÉBITO  1001 - Caja y efectivo          $1,000.00
  CRÉDITO 2002 - Depósitos ahorros         $1,000.00
  Suma débitos = Suma créditos ✅
```

### 3.2 Transferencia Intercuenta

Abel transfiere $200 a María (ambas cuentas de ahorros):

```
Asiento: AS-2026-000041
  DÉBITO  2002 - Dep. ahorros Abel          $200.00
  CRÉDITO 2002 - Dep. ahorros María         $200.00
  Suma débitos = Suma créditos ✅
```

### 3.3 Retención de Fondos (Durante la Saga)

Se reservan fondos mientras la transferencia está en proceso:

```
Asiento: AS-2026-000042
  DÉBITO  2002 - Dep. ahorros Abel          $200.00
  CRÉDITO 2100 - Fondos en tránsito         $200.00
  Suma débitos = Suma créditos ✅
```

### 3.4 Liberación de Retención (Transferencia Completada)

Se libera la retención y se acredita al destino:

```
Asiento: AS-2026-000043
  DÉBITO  2100 - Fondos en tránsito         $200.00
  CRÉDITO 2002 - Dep. ahorros María         $200.00
  Suma débitos = Suma créditos ✅
```

### 3.5 Comisión por Transferencia

Cobro de comisión de $2.50:

```
Asiento: AS-2026-000044
  DÉBITO  2002 - Dep. ahorros Abel          $2.50
  CRÉDITO 4020 - Comisiones por transfer.  $2.50
  Suma débitos = Suma créditos ✅
```

### 3.6 Reversión de Transferencia

Si una transferencia completada se revierte:

```
Asiento: AS-2026-000050 (reversión)
  DÉBITO  2002 - Dep. ahorros María         $200.00
  CRÉDITO 2002 - Dep. ahorros Abel          $200.00
  Suma débitos = Suma créditos ✅
```

---

## 4. Integridad del Ledger

### 4.1 Validación de Equilibrio

Antes de persistir cualquier asiento, el `LedgerService` valida:

```java
BigDecimal sumDebits = lines.stream()
    .filter(l -> l.getTipoMovimiento() == DEBITO)
    .map(LineaAsientoDTO::getMonto)
    .reduce(BigDecimal.ZERO, BigDecimal::add);

BigDecimal sumCredits = lines.stream()
    .filter(l -> l.getTipoMovimiento() == CREDITO)
    .map(LineaAsientoDTO::getMonto)
    .reduce(BigDecimal.ZERO, BigDecimal::add);

if (sumDebits.compareTo(sumCredits) != 0) {
    throw new UnbalancedEntryException(
        "Débitos y créditos no cuadran: " +
        sumDebits + " != " + sumCredits
    );
}
```

### 4.2 Inmutabilidad

- `asientos_contables`: NO tiene columna `fecha_actualizacion`
- `lineas_asiento`: NO tiene columna `fecha_actualizacion`
- Los repositorios NO exponen métodos `save()` para actualizar
- Las reversiónes crean nuevos asientos, nunca modifican existentes

### 4.3 Verificación de Equilibrio Global

El endpoint `/ledger/equilibrio` verifica que la suma de todos los movimientos
del ledger sea cero (débitos totales = créditos totales):

```
SELECT SUM(CASE WHEN tipo_movimiento = 'DEBITO' THEN monto ELSE 0 END) -
       SUM(CASE WHEN tipo_movimiento = 'CREDITO' THEN monto ELSE 0 END) AS balance
FROM lineas_asiento la
JOIN asientos_contables a ON la.id_asiento = a.id
WHERE a.estado = 'ACTIVO';
```

El resultado debe ser siempre 0.00.

---

## 5. AsientoFactory

La clase `AsientoFactory` en `ledger-service` contiene métodos estáticos
para crear asientos para cada tipo de operación:

| Método | Descripción |
|--------|-------------|
| `crearAsientoDeposito(cuenta, monto)` | Apertura de cuenta con depósito inicial |
| `crearAsientoTransferencia(origen, destino, monto)` | Transferencia entre cuentas |
| `crearAsientoRetencion(cuenta, monto)` | Retención de fondos durante la saga |
| `crearAsientoLiberacion(cuenta, monto)` | Liberación de retención |
| `crearAsientoComision(cuenta, monto)` | Cobro de comisión |
| `crearAsientoReversionTransferencia(transferencia)` | Reversión de transferencia completada |
| `crearAsientoIntereses(cuenta, monto)` | Acreditación de intereses (batch nocturno) |

---

## 6. Relación con Otros Módulos

```
┌─────────────────┐     gRPC      ┌──────────────────┐
│ Transfer-Service│◄──────────────│  Ledger-Service   │
│ (Saga Orch.)    │               │  (Asientos)       │
└─────────────────┘               └────────┬─────────┘
         │                                 │
         │ Kafka: transferencia.*         │
         ▼                                 ▼
┌─────────────────┐              ┌──────────────────┐
│ Account-Service │              │  Audit-Service    │
│ (Saldos CQRS)   │              │  (Inmutable)       │
└─────────────────┘              └──────────────────┘
```

- Transfer-Service llama a Ledger-Service vía gRPC para crear asientos
- Ledger-Service consume eventos de Kafka para asientos de conciliación
- Account-Service consume eventos de Kafka para actualizar saldos (read model)
- Audit-Service consume todos los eventos para trazabilidad completa
