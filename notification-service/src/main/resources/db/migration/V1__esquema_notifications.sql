CREATE TABLE notificaciones (
    id BIGSERIAL PRIMARY KEY,
    tipo_notificacion VARCHAR(50) NOT NULL,
    id_usuario VARCHAR(100),
    id_transferencia BIGINT,
    numero_transferencia VARCHAR(50),
    canal VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    titulo VARCHAR(255),
    mensaje TEXT,
    datos_adicionales JSONB,
    fecha_envio TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_notificaciones_usuario ON notificaciones(id_usuario);
CREATE INDEX idx_notificaciones_transferencia ON notificaciones(id_transferencia);
CREATE INDEX idx_notificaciones_estado ON notificaciones(estado);
