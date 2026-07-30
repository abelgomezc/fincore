CREATE TABLE IF NOT EXISTS auditoria (
    id BIGSERIAL PRIMARY KEY,
    entidad_tipo VARCHAR(100) NOT NULL,
    entidad_id BIGINT NOT NULL,
    accion VARCHAR(50) NOT NULL,
    estado_anterior TEXT,
    estado_nuevo TEXT,
    usuario_tipo VARCHAR(50),
    usuario_id BIGINT,
    ip_address VARCHAR(45),
    trace_id VARCHAR(100),
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_auditoria_entidad ON auditoria(entidad_tipo, entidad_id);
CREATE INDEX IF NOT EXISTS idx_auditoria_usuario ON auditoria(usuario_tipo, usuario_id);
CREATE INDEX IF NOT EXISTS idx_auditoria_creado_en ON auditoria(creado_en);
