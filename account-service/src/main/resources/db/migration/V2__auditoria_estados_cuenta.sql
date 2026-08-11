-- ===================================================================
-- FinCore Account Service — Auditoría de cambios de estado de cuenta
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- V2__auditoria_estados_cuenta.sql
-- ===================================================================

CREATE TABLE auditoria_estados_cuenta (
    id BIGSERIAL PRIMARY KEY,
    id_cuenta BIGINT NOT NULL REFERENCES cuentas(id) ON DELETE CASCADE,
    estado_anterior VARCHAR(20) NOT NULL,
    estado_nuevo VARCHAR(20) NOT NULL,
    motivo TEXT,
    ip_origen VARCHAR(45),
    user_agent TEXT,
    dispositivo VARCHAR(255),
    fecha_cambio TIMESTAMP NOT NULL DEFAULT NOW(),
    creado_por VARCHAR(100),
    actualizado_por VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_auditoria_estados_cuenta ON auditoria_estados_cuenta(id_cuenta);
CREATE INDEX idx_auditoria_estados_cuenta_fecha ON auditoria_estados_cuenta(fecha_cambio);
