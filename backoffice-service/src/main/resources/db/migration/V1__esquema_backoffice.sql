CREATE TABLE usuarios_sistema (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100),
    email VARCHAR(100),
    roles JSONB NOT NULL,
    es_activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP DEFAULT NOW()
);

CREATE TABLE configuracion_fraude (
    id BIGSERIAL PRIMARY KEY,
    codigo_variable VARCHAR(100) NOT NULL UNIQUE,
    valor TEXT,
    descripcion TEXT,
    tipo VARCHAR(20),
    fecha_actualizacion TIMESTAMP DEFAULT NOW()
);

CREATE TABLE auditorias_sistema (
    id BIGSERIAL PRIMARY KEY,
    id_usuario_sistema BIGINT,
    accion VARCHAR(100),
    entidad VARCHAR(50),
    id_entidad VARCHAR(100),
    detalle TEXT,
    ip_origen VARCHAR(45),
    fecha_creacion TIMESTAMP DEFAULT NOW()
);
