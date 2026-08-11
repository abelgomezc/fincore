-- ===================================================================
-- FinCore Customer Service — Auditoría de cambios de estado de cliente
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- V5__auditoria_estados_cliente.sql
-- ===================================================================

CREATE TABLE auditoria_estados_cliente (
    id BIGSERIAL PRIMARY KEY,
    id_cliente BIGINT NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
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

CREATE INDEX idx_auditoria_estados_cliente ON auditoria_estados_cliente(id_cliente);
CREATE INDEX idx_auditoria_estados_cliente_fecha ON auditoria_estados_cliente(fecha_cambio);
