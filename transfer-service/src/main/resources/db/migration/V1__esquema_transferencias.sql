-- ===================================================================
-- FinCore Transfer Service — Esquema de base de datos
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- V1__esquema_transferencias.sql
-- ===================================================================

-- ===================================================================
-- TRANSFERENCIAS
-- ===================================================================
CREATE TABLE transferencias (
    id BIGSERIAL PRIMARY KEY,
    numero_transferencia VARCHAR(30) UNIQUE NOT NULL,
    id_cuenta_origen BIGINT NOT NULL,
    numero_cuenta_origen VARCHAR(20) NOT NULL,
    id_cuenta_destino BIGINT NOT NULL,
    numero_cuenta_destino VARCHAR(20) NOT NULL,
    nombre_beneficiario VARCHAR(255) NOT NULL,
    monto DECIMAL(18,2) NOT NULL CHECK (monto > 0),
    moneda VARCHAR(3) NOT NULL DEFAULT 'USD',
    comision DECIMAL(18,2) NOT NULL DEFAULT 0,
    concepto TEXT,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    paso_saga_actual VARCHAR(50),
    intentos_saga INTEGER NOT NULL DEFAULT 0,
    score_fraude INTEGER,
    decision_fraude VARCHAR(20),
    id_usuario VARCHAR(100) NOT NULL,
    ip_origen VARCHAR(45) NOT NULL,
    dispositivo VARCHAR(255),
    trace_id VARCHAR(100),
    fecha_iniciada TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_completada TIMESTAMP,
    fecha_revertida TIMESTAMP,
    motivo_rechazo TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW()
);

ALTER TABLE transferencias ADD CONSTRAINT chk_estado_transferencia
    CHECK (estado IN ('PENDIENTE', 'VALIDANDO', 'AUTORIZADA', 'EN_REVISION',
                      'RESERVANDO', 'PROCESANDO', 'ACREDITANDO', 'COMPLETADA',
                      'RECHAZADA', 'REVERTIDA', 'ERROR'));

CREATE INDEX idx_transferencias_origen ON transferencias(id_cuenta_origen);
CREATE INDEX idx_transferencias_destino ON transferencias(id_cuenta_destino);
CREATE INDEX idx_transferencias_estado ON transferencias(estado);
CREATE INDEX idx_transferencias_usuario ON transferencias(id_usuario);
CREATE INDEX idx_transferencias_trace ON transferencias(trace_id);

-- ===================================================================
-- HISTORIAL DE ESTADOS (audit trail de la saga — INMUTABLE)
-- ===================================================================
CREATE TABLE transferencia_estados (
    id BIGSERIAL PRIMARY KEY,
    id_transferencia BIGINT NOT NULL REFERENCES transferencias(id) ON DELETE CASCADE,
    estado_anterior VARCHAR(20),
    estado_nuevo VARCHAR(20) NOT NULL,
    paso_saga VARCHAR(50),
    descripcion TEXT,
    error_detalle TEXT,
    fecha_cambio TIMESTAMP NOT NULL DEFAULT NOW()
    -- INMUTABLE — no tiene fecha_actualizacion
);

CREATE INDEX idx_estados_transferencia ON transferencia_estados(id_transferencia);
CREATE INDEX idx_estados_fecha ON transferencia_estados(fecha_cambio);

-- ===================================================================
-- LOG DE SAGA (registro de cada paso ejecutado)
-- ===================================================================
CREATE TABLE saga_log (
    id BIGSERIAL PRIMARY KEY,
    id_transferencia BIGINT NOT NULL REFERENCES transferencias(id) ON DELETE CASCADE,
    paso_saga VARCHAR(50) NOT NULL,
    orden INTEGER NOT NULL,
    estado_ejecucion VARCHAR(20) NOT NULL,
    detalle TEXT,
    error_detalle TEXT,
    tiempo_ejecucion_ms INTEGER,
    fecha_ejecucion TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_saga_transferencia ON saga_log(id_transferencia);
CREATE INDEX idx_saga_paso ON saga_log(paso_saga);

-- ===================================================================
-- LOG DE TRANSACCIONES DE COMPENSACIÓN
-- ===================================================================
CREATE TABLE compensating_transactions_log (
    id BIGSERIAL PRIMARY KEY,
    id_transferencia BIGINT NOT NULL REFERENCES transferencias(id) ON DELETE CASCADE,
    paso_original VARCHAR(50) NOT NULL,
    paso_compensacion VARCHAR(50) NOT NULL,
    estado_ejecucion VARCHAR(20) NOT NULL,
    detalle TEXT,
    error_detalle TEXT,
    fecha_ejecucion TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_compensacion_transferencia ON compensating_transactions_log(id_transferencia);
