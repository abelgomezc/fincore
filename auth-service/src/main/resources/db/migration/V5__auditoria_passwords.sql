-- ===================================================================
-- FinCore Auth Service — Auditoría de cambios de contraseña
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- V5__auditoria_passwords.sql
-- ===================================================================

CREATE TABLE auditoria_passwords (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    password_hash_anterior TEXT NOT NULL,
    password_hash_nuevo TEXT NOT NULL,
    ip_origen VARCHAR(45),
    user_agent TEXT,
    dispositivo VARCHAR(255),
    motivo TEXT,
    fecha_cambio TIMESTAMP NOT NULL DEFAULT NOW(),
    creado_por VARCHAR(100),
    actualizado_por VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_auditoria_passwords_usuario ON auditoria_passwords(id_usuario);
CREATE INDEX idx_auditoria_passwords_fecha ON auditoria_passwords(fecha_cambio);
