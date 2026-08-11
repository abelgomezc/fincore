-- ===================================================================
-- FinCore Auth Service — Auditoría de cambios de estado de usuario
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- V6__auditoria_estados_usuario.sql
-- ===================================================================

CREATE TABLE auditoria_estados_usuario (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
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

CREATE INDEX idx_auditoria_estados_usuario ON auditoria_estados_usuario(id_usuario);
CREATE INDEX idx_auditoria_estados_usuario_fecha ON auditoria_estados_usuario(fecha_cambio);
