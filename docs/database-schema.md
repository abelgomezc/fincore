# FinCore — Esquema de base de datos

Documentación generada a partir de las migraciones Flyway y de la inspección directa de cada BD. Incluye diagrama relacional completo, tablas por microservicio y relaciones cruzadas.

## Bases de datos por microservicio

| BD | Microservicio | Rol principal |
|---|---|---|
| `fincore_auth` | auth-service | Identidad, login, refresh tokens, sesiones, roles |
| `fincore_customers` | customer-service | Clientes, documentos, direcciones, KYC |
| `fincore_accounts` | account-service | Tipos de cuenta, cuentas, saldos, límites, beneficiarios |
| `fincore_transfers` | transfer-service | Transferencias, saga, estados, compensaciones |
| `fincore_ledger` | ledger-service | Plan de cuentas, asientos contables, líneas |
| `fincore_fraud` | fraud-service | Reglas, perfiles, lista negra, evaluaciones |
| `fincore_audit` | audit-service | Auditoría genérica y eventos de saga |
| `fincore_backoffice` | backoffice-service | Usuarios backoffice, auditoría interna, config fraude |
| `fincore_batch` | batch-service | Jobs batch y conciliaciones |
| `fincore_notifications` | notification-service | Notificaciones (email, push, WebSocket) |

## Tablas y cantidades reales

### auth-service (`fincore_auth`)
| Tabla | Registros |
|------|-----------|
| `usuarios` | 7 |
| `refresh_tokens` | 34 |
| `sesiones_activas` | 0 |
| `roles` | 5 |
| `permisos` | 9 |
| `rol_permisos` | 21 |
| `auditoria_passwords` | 0 |
| `auditoria_estados_usuario` | 0 |

### customer-service (`fincore_customers`)
| Tabla | Registros |
|------|-----------|
| `clientes` | 2 |
| `documentos_identidad` | 4 |
| `direcciones` | 4 |
| `contactos_emergencia` | 4 |
| `kyc_verificaciones` | 4 |
| `auditoria_estados_cliente` | 0 |

### account-service (`fincore_accounts`)
| Tabla | Registros |
|------|-----------|
| `tipos_cuenta` | 3 |
| `cuentas` | 2 |
| `saldos_historicos` | 0 |
| `limites_transaccion` | 2 |
| `beneficiarios_frecuentes` | 4 |
| `auditoria_estados_cuenta` | 0 |

### transfer-service (`fincore_transfers`)
| Tabla | Registros |
|------|-----------|
| `transferencias` | 0 |
| `transferencia_estados` | 0 |
| `saga_log` | 0 |
| `compensating_transactions_log` | 0 |
| `auditoria_transferencias` | 0 |

### ledger-service (`fincore_ledger`)
| Tabla | Registros |
|------|-----------|
| `plan_cuentas` | 22 |
| `asientos_contables` | 2 |
| `lineas_asiento` | 0 |

### fraud-service (`fincore_fraud`)
| Tabla | Registros |
|------|-----------|
| `reglas_fraude` | 0 |
| `perfil_transaccional` | 2 |
| `lista_negra` | 0 |
| `evaluaciones_fraude` | 0 |

### audit-service (`fincore_audit`)
| Tabla | Registros |
|------|-----------|
| `registros_auditoria` | 4 |
| `eventos_saga` | 0 |

### backoffice-service (`fincore_backoffice`)
| Tabla | Registros |
|------|-----------|
| `usuarios_sistema` | 1 |
| `auditorias_sistema` | 0 |
| `auditoria_cambios_backoffice` | 0 |
| `configuracion_fraude` | 0 |

### batch-service (`fincore_batch`)
| Tabla | Registros |
|------|-----------|
| `jobs_ejecutados` | 0 |
| `conciliaciones` | 0 |

### notification-service (`fincore_notifications`)
| Tabla | Registros |
|------|-----------|
| `notificaciones` | 0 |

## Diagrama relacional completo

```mermaid
erDiagram
    %% ============================================
    %% AUTH SERVICE
    %% ============================================
    usuarios {
        bigserial id PK
        varchar(255) email UK
        varchar(255) password_hash
        varchar(100) primer_nombre
        varchar(100) segundo_nombre
        varchar(100) primer_apellido
        varchar(100) segundo_apellido
        varchar(20) rol
        varchar(20) estado
        bigint id_cliente FK
        integer intentos_fallidos
        timestamp ultimo_intento_fallido
        timestamp fecha_bloqueo
        timestamp fecha_creacion
        timestamp fecha_actualizacion
        varchar(100) creado_por
        varchar(100) actualizado_por
        bigint version
    }

    refresh_tokens {
        bigserial id PK
        varchar(512) token UK
        bigint id_usuario FK
        timestamp fecha_expiracion
        timestamp fecha_creacion
        timestamp fecha_actualizacion
        timestamp fecha_revocacion
        boolean es_revocado
        varchar(255) device_id
        varchar(45) ip_origen
        text user_agent
        varchar(100) creado_por
        varchar(100) actualizado_por
        bigint version
    }

    sesiones_activas {
        bigserial id PK
        varchar(255) session_id UK
        bigint id_usuario FK
        varchar(255) device_id
        varchar(45) ip_origen
        text user_agent
        timestamp fecha_inicio
        timestamp fecha_creacion
        timestamp fecha_ultima_actividad
        timestamp fecha_expiracion
        timestamp fecha_actualizacion
        boolean es_activa
        varchar(100) creado_por
        varchar(100) actualizado_por
        bigint version
    }

    auditoria_passwords {
        bigserial id PK
        bigint id_usuario FK
        varchar(255) password_hash_anterior
        varchar(255) password_hash_nuevo
        varchar(45) ip_origen
        text user_agent
        varchar(255) dispositivo
        timestamp fecha_cambio
        varchar(100) creado_por
        varchar(100) actualizado_por
        bigint version
    }

    auditoria_estados_usuario {
        bigserial id PK
        bigint id_usuario FK
        varchar(20) estado_anterior
        varchar(20) estado_nuevo
        text motivo
        varchar(45) ip_origen
        text user_agent
        varchar(255) dispositivo
        timestamp fecha_cambio
        varchar(100) creado_por
        varchar(100) actualizado_por
        bigint version
    }

    roles {
        bigserial id PK
        varchar(50) nombre UK
        text descripcion
        timestamp fecha_creacion
    }

    permisos {
        bigserial id PK
        varchar(100) nombre UK
        text descripcion
        timestamp fecha_creacion
    }

    rol_permisos {
        bigint id_rol PK,FK
        bigint id_permiso PK,FK
    }

    %% ============================================
    %% CUSTOMER SERVICE
    %% ============================================
    clientes {
        bigserial id PK
        varchar(20) tipo_cliente
        varchar(20) estado
        varchar(100) primer_nombre
        varchar(100) segundo_nombre
        varchar(100) primer_apellido
        varchar(100) segundo_apellido
        date fecha_nacimiento
        varchar(20) genero
        varchar(255) email UK
        varchar(20) telefono
        text direccion
        varchar(100) ciudad
        varchar(3) pais
        timestamp fecha_registro
        timestamp fecha_actualizacion
        bigint version
    }

    documentos_identidad {
        bigserial id PK
        bigint id_cliente FK
        varchar(20) tipo_documento
        varchar(20) numero_documento
        date fecha_expedicion
        date fecha_expiracion
        varchar(3) pais_emision
        boolean verificado
        timestamp fecha_verificacion
        timestamp fecha_creacion
    }

    direcciones {
        bigserial id PK
        bigint id_cliente FK
        varchar(20) tipo_direccion
        text calle_principal
        text calle_secundaria
        varchar(100) ciudad
        varchar(100) provincia
        varchar(3) pais
        varchar(20) codigo_postal
        decimal(10,8) latitud
        decimal(11,8) longitud
        timestamp fecha_creacion
    }

    contactos_emergencia {
        bigserial id PK
        bigint id_cliente FK
        varchar(255) nombre
        varchar(20) telefono
        varchar(50) parentesco
        timestamp fecha_creacion
    }

    kyc_verificaciones {
        bigserial id PK
        bigint id_cliente FK
        varchar(20) estado
        timestamp fecha_verificacion
        varchar(100) verificado_por
        text observaciones
        timestamp fecha_creacion
        timestamp fecha_actualizacion
    }

    auditoria_estados_cliente {
        bigserial id PK
        bigint id_cliente FK
        varchar(20) estado_anterior
        varchar(20) estado_nuevo
        text motivo
        varchar(45) ip_origen
        text user_agent
        varchar(255) dispositivo
        timestamp fecha_cambio
        varchar(100) creado_por
        varchar(100) actualizado_por
        bigint version
    }

    %% ============================================
    %% ACCOUNT SERVICE
    %% ============================================
    tipos_cuenta {
        bigserial id PK
        varchar(10) codigo UK
        varchar(100) nombre
        text descripcion
        decimal(8,4) tasa_interes_anual
        decimal(18,2) saldo_minimo
        decimal(18,2) limite_transaccion_diario
        decimal(18,2) limite_monto_por_transaccion
        boolean permite_sobregiro
        timestamp fecha_creacion
    }

    cuentas {
        bigserial id PK
        varchar(20) numero_cuenta UK
        bigint id_cliente
        bigint id_tipo_cuenta FK
        varchar(20) estado
        varchar(3) moneda
        decimal(18,2) saldo_contable
        decimal(18,2) saldo_disponible
        decimal(18,2) saldo_retenido
        decimal(18,2) saldo_proyectado
        text motivo_bloqueo
        date fecha_apertura
        timestamp fecha_ultimo_movimiento
        date fecha_cierre
        bigint version
        timestamp fecha_creacion
        timestamp fecha_actualizacion
    }

    saldos_historicos {
        bigserial id PK
        bigint id_cuenta FK
        date fecha_snapshot
        decimal(18,2) saldo_contable
        decimal(18,2) saldo_disponible
        decimal(18,2) saldo_retenido
        decimal(18,2) saldo_proyectado
        timestamp fecha_creacion
    }

    limites_transaccion {
        bigserial id PK
        bigint id_cuenta FK
        decimal(18,2) monto_maximo_diario
        decimal(18,2) monto_maximo_por_transaccion
        decimal(18,2) monto_maximo_mensual
        decimal(18,2) contador_diario
        decimal(18,2) contador_mensual
        timestamp fecha_ultima_transaccion
        timestamp fecha_creacion
        timestamp fecha_actualizacion
    }

    beneficiarios_frecuentes {
        bigserial id PK
        bigint id_cliente
        bigint id_cuenta_beneficiario FK
        varchar(255) nombre_beneficiario
        varchar(20) numero_cuenta
        boolean activo
        timestamp fecha_creacion
        timestamp fecha_actualizacion
    }

    auditoria_estados_cuenta {
        bigserial id PK
        bigint id_cuenta FK
        varchar(20) estado_anterior
        varchar(20) estado_nuevo
        text motivo
        varchar(45) ip_origen
        text user_agent
        varchar(255) dispositivo
        timestamp fecha_cambio
        varchar(100) creado_por
        varchar(100) actualizado_por
        bigint version
    }

    %% ============================================
    %% TRANSFER SERVICE
    %% ============================================
    transferencias {
        bigserial id PK
        varchar(30) numero_transferencia UK
        bigint id_cuenta_origen
        varchar(20) numero_cuenta_origen
        bigint id_cuenta_destino
        varchar(20) numero_cuenta_destino
        varchar(255) nombre_beneficiario
        decimal(18,2) monto
        varchar(3) moneda
        decimal(18,2) comision
        text concepto
        varchar(20) estado
        varchar(50) paso_saga_actual
        integer intentos_saga
        integer score_fraude
        varchar(20) decision_fraude
        varchar(100) id_usuario
        varchar(45) ip_origen
        varchar(255) dispositivo
        varchar(100) trace_id
        timestamp fecha_iniciada
        timestamp fecha_completada
        timestamp fecha_revertida
        text motivo_rechazo
        bigint version
        timestamp fecha_creacion
        timestamp fecha_actualizacion
    }

    transferencia_estados {
        bigserial id PK
        bigint id_transferencia FK
        varchar(20) estado_anterior
        varchar(20) estado_nuevo
        varchar(50) paso_saga
        text descripcion
        text error_detalle
        timestamp fecha_cambio
    }

    saga_log {
        bigserial id PK
        bigint id_transferencia FK
        varchar(50) paso_saga
        integer orden
        varchar(20) estado_ejecucion
        text detalle
        text error_detalle
        integer tiempo_ejecucion_ms
        timestamp fecha_ejecucion
    }

    compensating_transactions_log {
        bigserial id PK
        bigint id_transferencia FK
        varchar(50) paso_original
        varchar(50) paso_compensacion
        varchar(20) estado_ejecucion
        text detalle
        text error_detalle
        timestamp fecha_ejecucion
    }

    auditoria_transferencias {
        bigserial id PK
        bigint id_transferencia FK
        varchar(50) accion
        varchar(20) estado_anterior
        varchar(20) estado_nuevo
        varchar(20) resultado
        text detalle
        text error_detalle
        varchar(100) id_usuario
        varchar(45) ip_origen
        varchar(255) dispositivo
        varchar(100) trace_id
        timestamp fecha_accion
        varchar(100) creado_por
        varchar(100) actualizado_por
        bigint version
    }

    %% ============================================
    %% LEDGER SERVICE
    %% ============================================
    plan_cuentas {
        bigserial id PK
        varchar(10) codigo UK
        varchar(200) nombre
        varchar(20) tipo
        varchar(10) naturaleza
        integer nivel
        varchar(10) codigo_padre
        boolean es_hoja
        boolean es_activa
        timestamp fecha_creacion
        varchar(255) creado_por
    }

    asientos_contables {
        bigserial id PK
        varchar(30) numero_asiento UK
        text descripcion
        bigint id_referencia
        varchar(50) tipo_referencia
        varchar(100) id_usuario
        varchar(45) ip_origen
        varchar(100) trace_id
        timestamp fecha_asiento
        varchar(20) estado
        timestamp fecha_creacion
    }

    lineas_asiento {
        bigserial id PK
        bigint id_asiento FK
        varchar(10) codigo_cuenta FK
        bigint id_cuenta_bancaria
        varchar(10) tipo_movimiento
        decimal(18,2) monto
        text descripcion
        timestamp fecha_creacion
    }

    %% ============================================
    %% FRAUD SERVICE
    %% ============================================
    reglas_fraude {
        bigserial id PK
        varchar(50) codigo UK
        varchar(100) nombre
        integer puntos
        jsonb parametros
        boolean es_activo
        timestamp fecha_creacion
        timestamp fecha_actualizacion
    }

    perfil_transaccional {
        bigserial id PK
        bigint id_cliente UK
        decimal(18,2) promedio_monto_30d
        decimal(18,2) maximo_monto_30d
        integer total_transferencias_30d
        jsonb paises_habituales
        jsonb dispositivos_habituales
        jsonb horarios_habituales
        timestamp fecha_actualizacion
    }

    lista_negra {
        bigserial id PK
        varchar(20) tipo
        varchar(255) valor
        varchar(255) motivo
        boolean es_activo
        timestamp fecha_creacion
        timestamp fecha_actualizacion
    }

    evaluaciones_fraude {
        bigserial id PK
        bigint id_transferencia
        bigint id_cliente
        integer score_total
        varchar(20) decision
        jsonb reglas_activadas
        varchar(45) ip_origen
        varchar(255) dispositivo
        integer tiempo_evaluacion_ms
        varchar(100) revisado_por
        timestamp fecha_revision
        timestamp fecha_creacion
    }

    %% ============================================
    %% AUDIT SERVICE
    %% ============================================
    registros_auditoria {
        bigserial id PK
        varchar(100) trace_id
        varchar(50) servicio
        varchar(255) endpoint
        varchar(10) metodo_http
        varchar(100) id_usuario
        varchar(50) rol_usuario
        varchar(45) ip_origen
        varchar(100) id_recurso
        varchar(50) tipo_recurso
        varchar(100) accion
        varchar(20) resultado
        text request_body
        integer response_codigo
        integer tiempo_respuesta_ms
        text detalle
        timestamp fecha_creacion
    }

    eventos_saga {
        bigserial id PK
        bigint id_transferencia
        varchar(50) numero_transferencia
        varchar(50) paso_saga
        integer orden
        varchar(20) estado_ejecucion
        text detalle
        text error_detalle
        integer duracion_ms
        timestamp fecha_creacion
    }

    %% ============================================
    %% BACKOFFICE SERVICE
    %% ============================================
    usuarios_sistema {
        bigserial id PK
        varchar(50) username UK
        varchar(255) password_hash
        varchar(100) nombre_completo
        varchar(100) email
        jsonb roles
        varchar(20) estado
        timestamp fecha_creacion
        timestamp fecha_actualizacion
    }

    auditorias_sistema {
        bigserial id PK
        varchar(100) id_usuario_sistema
        varchar(100) accion
        varchar(50) entidad
        varchar(100) id_entidad
        text detalle
        varchar(45) ip_origen
        timestamp fecha_creacion
    }

    configuracion_fraude {
        bigserial id PK
        varchar(100) codigo_variable UK
        text valor
        text descripcion
        varchar(20) tipo
        timestamp fecha_actualizacion
    }

    auditoria_cambios_backoffice {
        bigserial id PK
        bigint id_usuario_sistema FK
        varchar(50) entidad
        varchar(100) id_entidad
        varchar(100) accion
        jsonb valores_anteriores
        jsonb valores_nuevos
        varchar(45) ip_origen
        text user_agent
        varchar(255) dispositivo
        timestamp fecha_cambio
        varchar(100) creado_por
        varchar(100) actualizado_por
        bigint version
    }

    %% ============================================
    %% BATCH SERVICE
    %% ============================================
    jobs_ejecutados {
        bigserial id PK
        varchar(100) nombre_job
        timestamp fecha_inicio
        timestamp fecha_fin
        varchar(20) estado
        integer registros_procesados
        text detalle
        text error_detalle
        timestamp fecha_creacion
    }

    conciliaciones {
        bigserial id PK
        date fecha_procesamiento
        integer total_transferencias
        decimal(18,2) total_debitos
        decimal(18,2) total_creditos
        jsonb diferencias
        varchar(20) estado
        timestamp fecha_creacion
    }

    %% ============================================
    %% NOTIFICATION SERVICE
    %% ============================================
    notificaciones {
        bigserial id PK
        varchar(50) tipo_notificacion
        varchar(100) id_usuario
        bigint id_transferencia
        varchar(50) numero_transferencia
        varchar(20) canal
        varchar(20) estado
        varchar(255) titulo
        text mensaje
        jsonb datos_adicionales
        timestamp fecha_envio
        timestamp fecha_creacion
        timestamp fecha_actualizacion
    }

    %% ============================================
    %% NOTIFICATION SERVICE
    %% ============================================
    notificaciones {
        bigserial id PK
        varchar(50) tipo_notificacion
        varchar(100) id_usuario
        bigint id_transferencia
        varchar(50) numero_transferencia
        varchar(20) canal
        varchar(20) estado
        varchar(255) titulo
        text mensaje
        jsonb datos_adicionales
        timestamp fecha_envio
        timestamp fecha_creacion
        timestamp fecha_actualizacion
    }

    %% ============================================
    %% RELATIONSHIPS
    %% ============================================
    usuarios ||--o{ refresh_tokens : "tiene"
    usuarios ||--o{ sesiones_activas : "tiene"
    usuarios ||--o{ auditoria_passwords : "tiene"
    usuarios ||--o{ auditoria_estados_usuario : "tiene"
    roles ||--o{ rol_permisos : "tiene"
    permisos ||--o{ rol_permisos : "tiene"

    clientes ||--o{ documentos_identidad : "tiene"
    clientes ||--o{ direcciones : "tiene"
    clientes ||--o{ contactos_emergencia : "tiene"
    clientes ||--o{ kyc_verificaciones : "tiene"
    clientes ||--o{ auditoria_estados_cliente : "tiene"

    tipos_cuenta ||--o{ cuentas : "tiene"
    cuentas ||--o{ saldos_historicos : "tiene"
    cuentas ||--o{ limites_transaccion : "tiene"
    cuentas ||--o{ beneficiarios_frecuentes : "beneficiario"
    cuentas ||--o{ auditoria_estados_cuenta : "tiene"

    transferencias ||--o{ transferencia_estados : "tiene"
    transferencias ||--o{ saga_log : "tiene"
    transferencias ||--o{ compensating_transactions_log : "tiene"
    transferencias ||--o{ auditoria_transferencias : "tiene"
    transferencias ||--o{ notificaciones : "genera"

    asientos_contables ||--o{ lineas_asiento : "tiene"
    plan_cuentas ||--o{ lineas_asiento : "tiene"
    usuarios_sistema ||--o{ auditoria_cambios_backoffice : "tiene"
```

## Relaciones cruzadas entre microservicios

| Origen | Destino | Tipo de relación | Descripción |
|--------|---------|------------------|-------------|
| `auth-service.usuarios.id` | `customer-service.clientes.id` | Lógica / eventual | Un usuario puede estar asociado a un cliente |
| `customer-service.clientes.id` | `account-service.cuentas.id_cliente` | Lógica | Un cliente puede tener muchas cuentas |
| `account-service.cuentas.id` | `transfer-service.transferencias.id_cuenta_origen` | Lógica | Una cuenta puede originar transferencias |
| `account-service.cuentas.id` | `transfer-service.transferencias.id_cuenta_destino` | Lógica | Una cuenta puede recibir transferencias |
| `transfer-service.transferencias.id` | `ledger-service.asientos_contables.id_referencia` | Lógica | Una transferencia genera asientos contables |
| `transfer-service.transferencias.id` | `fraud-service.evaluaciones_fraude.id_transferencia` | Lógica | Cada transferencia puede tener una evaluación de fraude |
| `transfer-service.transferencias.trace_id` | `audit-service.registros_auditoria.trace_id` | Lógica | Trazabilidad de auditoría |
| `transfer-service.transferencias.trace_id` | `ledger-service.asientos_contables.trace_id` | Lógica | Trazabilidad contable |
| `auth-service.usuarios.id` | `backoffice-service.usuarios_sistema.id` | Lógica | Usuarios backoffice separados de usuarios de app |
| `customer-service.clientes.id` | `fraud-service.perfil_transaccional.id_cliente` | Lógica | Perfil transaccional por cliente |
| `transfer-service.transferencias.id` | `notification-service.notificaciones.id_transferencia` | Lógica | Notificaciones por transferencia |

## Observaciones importantes

- La BD `fincore_notifications` ya existe y la tabla `notificaciones` está creada con su migración V1 aplicada.
- Se agregaron tablas de auditoría especializadas: `auditoria_passwords`, `auditoria_estados_usuario`, `auditoria_estados_cliente`, `auditoria_estados_cuenta`, `auditoria_transferencias` y `auditoria_cambios_backoffice`.
- `clientes` está repetido 2 veces en `fincore_customers` según la consulta de tablas; revisar si es un dato duplicado o una vista/materialized view.
- En `auth-service`, `usuarios.estado` ahora incluye `ELIMINADO` en su constraint.
- El diagrama es orientado a tablas y relaciones DDL reales; no incluye aún las tablas del gateway porque usa `localhost:8080` y no corría al momento de la inspección.
- Algunas relaciones son lógicas por `id` o `trace_id` porque cada microservicio tiene su propia BD; no hay FK físicas entre servicios.
