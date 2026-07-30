-- Schema del microservicio customer-service para FinCore
-- Creado por Abel Gomez - 2026

CREATE TABLE IF NOT EXISTS clientes (
    id BIGSERIAL PRIMARY KEY,
    tipo_persona_id VARCHAR(20) NOT NULL,
    numero_identificacion VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100),
    razon_social VARCHAR(150),
    fecha_nacimiento DATE,
    nacionalidad VARCHAR(50),
    direccion TEXT,
    telefono VARCHAR(20),
    email VARCHAR(100),
    estado VARCHAR(20) NOT NULL,
    version INTEGER NOT NULL DEFAULT 0,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_clientes_numero_identificacion ON clientes(numero_identificacion);
CREATE INDEX IF NOT EXISTS idx_clientes_estado ON clientes(estado);
