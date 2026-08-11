-- ===================================================================
-- FinCore Transfer Service — Auditoría extendida de transferencias
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- V2__auditoria_transferencias.sql
-- ===================================================================

CREATE TABLE auditoria_transferencias (
    id BIGSERIAL PRIMARY KEY,
    id_transferencia BIGINT NOT NULL REFERENCES transferencias(id) ON DELETE CASCADE,
    accion VARCHAR(50) NOT NULL,
    estado_anterior VARCHAR(20),
    estado_nuevo VARCHAR(20),
    resultado VARCHAR(20) NOT NULL,
    detalle TEXT,
    error_detalle TEXT,
    id_usuario VARCHAR(100),
    ip_origen VARCHAR(45),
    dispositivo VARCHAR(255),
    trace_id VARCHAR(100),
    fecha_accion TIMESTAMP NOT NULL DEFAULT NOW(),
    creado_por VARCHAR(100),
    actualizado_por VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_auditoria_transferencias_transferencia ON auditoria_transferencias(id_transferencia);
CREATE INDEX idx_auditoria_transferencias_fecha ON auditoria_transferencias(fecha_accion);
CREATE INDEX idx_auditoria_transferencias_trace ON auditoria_transferencias(trace_id);
