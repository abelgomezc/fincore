# Documentación — Motor Antifraude

© 2026 Abel Gomez. Todos los derechos reservados.

---

## 1. Introducción

El motor antifraude de FinCore es un sistema de scoring que evalúa
TODAS las transferencias sin excepción. Cada transferencia recibe
un score de riesgo basado en 10 reglas configurables. El score
determina si la transferencia se aprueba, entra en revisión manual
o se rechaza automáticamente.

---

## 2. Las 10 Reglas de Fraude

### 2.1 MONTO_INUSUAL (15 puntos)

**Descripción:** El monto de la transferencia es inusualmente alto
comparado con el comportamiento histórico del cliente.

**Lógica:**
- Se obtiene el promedio móvil de 30 días del cliente
- Si `monto > promedio * 3` → activa la regla
- Si no hay historial (primer transferencia), no aplica

**Umbral configurable:** `fraud.monto.multiplo.umbral` (default: 3.0)

---

### 2.2 HORARIO_INUSUAL (10 puntos)

**Descripción:** La transferencia ocurre en un horario inusual para el cliente.

**Lógica:**
- Se obtienen los horarios habituales del cliente (profil transaccional)
- Horario de riesgo: 00:00 - 05:00 hora local
- Si la transferencia cae en este horario → activa la regla
- Se pondera con el patrón histórico del cliente

**Horario de riesgo configurable:** `fraud.horario.inicio` (default: 0), `fraud.horario.fin` (default: 5)

---

### 2.3 DISPOSITIVO_NUEVO (20 puntos)

**Descripción:** La transferencia proviene de un dispositivo no reconocido.

**Lógica:**
- Se extrae el `deviceId` del header de la petición
- Se consulta Redis: `cliente:{id}:dispositivos`
- Si el dispositivo no está en la lista → activa la regla
- Si es la primera transacción del cliente → activa la regla

**TTL de dispositivos:** 30 días (`fraud.dispositivo.ttl.dias`)

---

### 2.4 BENEFICIARIO_NUEVO (15 puntos)

**Descripción:** La transferencia va a un beneficiario no habitual.

**Lógica:**
- Se consulta `beneficiarios_frecuentes` del cliente
- Si la cuenta destino no está en la lista → activa la regla
- Si es la primera transferencia del cliente → activa la regla

---

### 2.5 PAIS_DIFERENTE (25 puntos)

**Descripción:** La IP de origen está en un país diferente al habitual.

**Lógica:**
- Se geolocaliza la IP usando ip-api.com
- Se compara con `paises_habituales` del perfil transaccional
- Si el país no está en la lista → activa la regla
- Si es la primera geolocalización → activa la regla

**API de geolocalización:** `http://ip-api.com/json/{ip}`

---

### 2.6 VELOCIDAD_ALTA (30 puntos)

**Descripción:** El cliente realiza más transacciones de las esperadas en un corto período.

**Lógica:**
- Se cuenta las transacciones del cliente en los últimos 60 minutos
- Se usa Redis con contador y TTL de 60 minutos
- Si `transacciones > 5` en 10 minutos → activa la regla
- El contador se incrementa con cada evaluación

**Límites configurables:**
- `fraud.velocity.max.transactions.per.hour` (default: 10)
- `fraud.velocity.window.minutes` (default: 60)

---

### 2.7 LISTA_NEGRA (100 puntos)

**Descripción:** La cuenta origen, destino, documento o IP está en la lista negra.

**Lógica:**
- Se consulta `lista_negra` en la base de datos
- Se busca por: número de cuenta origen, número de cuenta destino,
  documento del cliente, IP de origen
- Si cualquiera coincide y está activa → activa la regla (score = 100)

**Resultado:** Siempre RECHAZADO (score ≥ 70)

---

### 2.8 IP_SOSPECHOSA (40 puntos)

**Descripción:** La IP de origen ha sido reportada como maliciosa.

**Lógica:**
- Se consulta un servicio externo de reputación de IPs
- Lista local de IPs reportadas en `lista_negra` con tipo `IP`
- Si la IP está en la lista → activa la regla

---

### 2.9 PATRON_FRACCIONADO (35 puntos)

**Descripción:** El cliente realiza varias transferencias pequeñas seguidas,
posiblemente para evadir límites de detección.

**Lógica:**
- Se revisan las últimas 5 transferencias del cliente
- Si todas son pequeñas (< $100) y ocurrieron en < 10 minutos → activa
- Se usa Redis para tracking de patrones

**Umbral configurable:** `fraud.patron.monto.maximo` (default: 100.00)

---

### 2.10 PRIMER_TRANSFER_GRANDE (20 puntos)

**Descripción:** El cliente realiza su primera transferencia y es de alto monto.

**Lógica:**
- Se verifica si el cliente tiene historial de transferencias
- Si es la primera y el monto > $1,000 → activa la regla

**Umbral configurable:** `fraud.primer.transfer.grande.umbral` (default: 1000.00)

---

## 3. Scoring y Decisiones

### 3.1 Cálculo del Score

```
score = Σ(puntos de reglas activadas)
```

Ejemplo:
```
MONTO_INUSUAL:     15 puntos ✓
HORARIO_INUSUAL:   10 puntos ✓
DISPOSITIVO_NUEVO: 20 puntos ✓
BENEFICIARIO_NUEVO: 15 puntos ✓
VELOCIDAD_ALTA:    30 puntos ✗

Score total: 60 puntos → EN_REVISION
```

### 3.2 Decisiones

| Score | Decisión | Acción |
|-------|----------|--------|
| 0-29  | APROBADO | Transferencia continúa en la saga |
| 30-69 | EN_REVISION | Transferencia pausada, espera aprobación manual de supervisor |
| 70+   | RECHAZADO | Transferencia rechazada, se notifica al cliente |

### 3.3 Configuración de Thresholds

```properties
fraud.score.threshold.auto.approve=30
fraud.score.threshold.review=70
fraud.score.threshold.auto.reject=70
```

---

## 4. Reglas Configurables en Base de Datos

Las reglas se almacenan en la tabla `reglas_fraude` para permitir
ajustes sin redeploy:

```sql
CREATE TABLE reglas_fraude (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    puntos INTEGER NOT NULL,
    parametros JSONB,              -- parámetros específicos de la regla
    es_activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP DEFAULT NOW()
);
```

Datos iniciales:

| Código | Nombre | Puntos | Activa |
|--------|--------|--------|--------|
| MONTO_INUSUAL | Monto inusual | 15 | ✓ |
| HORARIO_INUSUAL | Horario inusual | 10 | ✓ |
| DISPOSITIVO_NUEVO | Dispositivo nuevo | 20 | ✓ |
| BENEFICIARIO_NUEVO | Beneficiario nuevo | 15 | ✓ |
| PAIS_DIFERENTE | País diferente | 25 | ✓ |
| VELOCIDAD_ALTA | Velocidad alta | 30 | ✓ |
| LISTA_NEGRA | Lista negra | 100 | ✓ |
| IP_SOSPECHOSA | IP sospechosa | 40 | ✓ |
| PATRON_FRACCIONADO | Patrón fraccionado | 35 | ✓ |
| PRIMER_TRANSFER_GRANDE | Primer transfer grande | 20 | ✓ |

---

## 5. Perfil Transaccional

El motor mantiene un perfil transaccional por cliente para detectar
anomalías:

```sql
CREATE TABLE perfil_transaccional (
    id BIGSERIAL PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    promedio_monto_30d DECIMAL(18,2),
    maximo_monto_30d DECIMAL(18,2),
    total_transferencias_30d INTEGER,
    paises_habituales JSONB,         -- ["EC", "CO", "PE"]
    dispositivos_habituales JSONB,   -- [{"id":"dev1","ultima":"2024-01-15"}]
    horarios_habituales JSONB,       -- {"morning":0.6,"afternoon":0.3,"night":0.1}
    fecha_actualizacion TIMESTAMP DEFAULT NOW(),
    UNIQUE(id_cliente)
);
```

### Actualización del Perfil

El perfil se actualiza después de que una transferencia se completa:

1. Se recalculan los promedios de 30 días
2. Se actualiza el máximo monto
3. Se incrementa el contador de transferencias
4. Se agrega el país/IP/dispositivo a las listas habituales
5. Se recalculan los horarios con distribución ponderada

---

## 6. Lista Negra

### 6.1 Tipos de Entradas

| Tipo | Descripción |
|------|-------------|
| CUENTA | Número de cuenta bancaria |
| DOCUMENTO | Número de documento de identidad |
| IP | Dirección IP |
| DISPOSITIVO | Device ID |

### 6.2 Gestión

- **Agregar:** POST `/backoffice/lista-negra` (rol SUPERVISOR)
- **Remover:** DELETE `/backoffice/lista-negra/{id}` (rol SUPERVISOR)
- **Listar:** GET `/backoffice/lista-negra` (rol SUPERVISOR, AUDITOR)

---

## 7. Integración con la Saga

### Flujo de Evaluación

```
Transfer-Service
    │
    │  (gRPC) EvaluarTransferenciaRequest
    ▼
Fraud-Service
    │
    ├─ Consulta perfil_transaccional (BD)
    ├─ Consulta lista_negra (BD)
    ├─ Consulta dispositivos en Redis
    ├─ Consulta IP en ip-api.com
    ├─ Aplica 10 reglas
    ├─ Calcula score
    └─ Retorna EvaluarTransferenciaResponse
         { score, decision, reglasActivadas }
    │
    ▼
Transfer-Service
    │
    ├─ score < 30 → continúa saga (AUTORIZADA)
    ├─ 30 ≤ score < 70 → EN_REVISION (pausa saga)
    └─ score ≥ 70 → RECHAZADA (cancela saga)
```

### En Caso de EN_REVISION

1. La transferencia se pausa en estado `EN_REVISION`
2. Se publica evento Kafka `transferencia.en.revision`
3. El backoffice muestra la transferencia en la lista de revisión
4. Un supervisor aprueba o rechaza manualmente
5. Si se aprueba → la saga continúa desde el paso 5 (RESERVAR_FONDOS)
6. Si se rechaza → estado → RECHAZADA

---

## 8. Auditoría de Evaluaciones

Todas las evaluaciones se registran en `evaluaciones_fraude`:

```sql
CREATE TABLE evaluaciones_fraude (
    id BIGSERIAL PRIMARY KEY,
    id_transferencia BIGINT NOT NULL,
    id_cliente BIGINT NOT NULL,
    score_total INTEGER NOT NULL,
    decision VARCHAR(20) NOT NULL,
    reglas_activadas JSONB NOT NULL,
    ip_origen VARCHAR(45),
    dispositivo VARCHAR(255),
    tiempo_evaluacion_ms INTEGER,
    revisado_por VARCHAR(100),
    fecha_revision TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT NOW()
);
```

Esto permite:
- Auditoría completa de decisiones de fraude
- Análisis de patrones de reglas activadas
- Métricas de efectividad del motor
- Cumplimiento normativo (Basel III, PCI DSS)
