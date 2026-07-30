CREATE TABLE IF NOT EXISTS notificaciones (
    id BIGSERIAL PRIMARY KEY,
    canal VARCHAR(20) NOT NULL,
    destinatario VARCHAR(150) NOT NULL,
    asunto VARCHAR(200) NOT NULL,
    cuerpo VARCHAR(1000) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    intentos INTEGER NOT NULL,
    respuesta_externa VARCHAR(500),
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_notificaciones_destinatario ON notificaciones(destinatario);
CREATE INDEX IF NOT EXISTS idx_notificaciones_estado ON notificaciones(estado);
CREATE INDEX IF NOT EXISTS idx_notificaciones_creado_en ON notificaciones(creado_en);
