CREATE TABLE jobs_ejecutados (
    id BIGSERIAL PRIMARY KEY,
    nombre_job VARCHAR(100) NOT NULL,
    fecha_inicio TIMESTAMP,
    fecha_fin TIMESTAMP,
    estado VARCHAR(20),
    registros_procesados INTEGER,
    detalle TEXT,
    error_detalle TEXT,
    fecha_creacion TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_jobs_fecha ON jobs_ejecutados(fecha_creacion);
CREATE INDEX idx_jobs_nombre ON jobs_ejecutados(nombre_job);

CREATE TABLE conciliaciones (
    id BIGSERIAL PRIMARY KEY,
    fecha_procesamiento DATE,
    total_transferencias INTEGER,
    total_debitos DECIMAL(18,2),
    total_creditos DECIMAL(18,2),
    diferencias JSONB,
    estado VARCHAR(20),
    fecha_creacion TIMESTAMP DEFAULT NOW()
);
