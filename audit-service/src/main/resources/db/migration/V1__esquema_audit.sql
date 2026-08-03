CREATE TABLE registros_auditoria (
    id BIGSERIAL PRIMARY KEY,
    trace_id VARCHAR(100) NOT NULL,
    servicio VARCHAR(50) NOT NULL,
    endpoint VARCHAR(255),
    metodo_http VARCHAR(10),
    id_usuario VARCHAR(100),
    rol_usuario VARCHAR(50),
    ip_origen VARCHAR(45),
    id_recurso VARCHAR(100),
    tipo_recurso VARCHAR(50),
    accion VARCHAR(100),
    resultado VARCHAR(20),
    request_body TEXT,
    response_codigo INTEGER,
    tiempo_respuesta_ms INTEGER,
    detalle TEXT,
    fecha_creacion TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_auditoria_trace_id ON registros_auditoria(trace_id);
CREATE INDEX idx_auditoria_servicio ON registros_auditoria(servicio);
CREATE INDEX idx_auditoria_fecha ON registros_auditoria(fecha_creacion);
CREATE INDEX idx_auditoria_tipo_recurso ON registros_auditoria(tipo_recurso);
CREATE INDEX idx_auditoria_id_usuario ON registros_auditoria(id_usuario);

CREATE TABLE eventos_saga (
    id BIGSERIAL PRIMARY KEY,
    id_transferencia BIGINT NOT NULL,
    numero_transferencia VARCHAR(50),
    paso_saga VARCHAR(50),
    orden INTEGER,
    estado_ejecucion VARCHAR(20),
    detalle TEXT,
    error_detalle TEXT,
    duracion_ms INTEGER,
    fecha_creacion TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_eventos_saga_transferencia ON eventos_saga(id_transferencia);
CREATE INDEX idx_eventos_saga_paso ON eventos_saga(paso_saga);
