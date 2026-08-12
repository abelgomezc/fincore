-- ===================================================================
-- FinCore Backoffice Service — Auditoría de cambios administrativos
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- V2__auditoria_cambios_backoffice.sql
-- ===================================================================

CREATE TABLE auditoria_cambios_backoffice (
    id BIGSERIAL PRIMARY KEY,
    id_usuario_sistema BIGINT NOT NULL REFERENCES usuarios_sistema(id) ON DELETE CASCADE,
    entidad VARCHAR(50) NOT NULL,
    id_entidad VARCHAR(100) NOT NULL,
    accion VARCHAR(100) NOT NULL,
    valores_anteriores JSONB,
    valores_nuevos JSONB,
    comentario TEXT,
    ip_origen VARCHAR(45),
    user_agent TEXT,
    dispositivo VARCHAR(255),
    fecha_cambio TIMESTAMP NOT NULL DEFAULT NOW(),
    creado_por VARCHAR(100),
    actualizado_por VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_auditoria_cambios_backoffice_usuario ON auditoria_cambios_backoffice(id_usuario_sistema);
CREATE INDEX idx_auditoria_cambios_backoffice_entidad ON auditoria_cambios_backoffice(entidad, id_entidad);
CREATE INDEX idx_auditoria_cambios_backoffice_fecha ON auditoria_cambios_backoffice(fecha_cambio);
