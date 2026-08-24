CREATE TABLE IF NOT EXISTS documentos_plantillas (
    id SERIAL PRIMARY KEY,
    tipo_documento VARCHAR(50) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    contenido_html TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS documentos_generados (
    id SERIAL PRIMARY KEY,
    entidad VARCHAR(50) NOT NULL,
    id_entidad VARCHAR(100) NOT NULL,
    tipo_documento VARCHAR(50) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    nombre_archivo VARCHAR(255),
    url_archivo TEXT,
    contenido_html TEXT,
    hash_archivo VARCHAR(255),
    id_plantilla BIGINT,
    fecha_generacion TIMESTAMP,
    fecha_firma TIMESTAMP,
    fecha_vencimiento TIMESTAMP,
    creado_por VARCHAR(100),
    actualizado_por VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS firmas_electronicas (
    id SERIAL PRIMARY KEY,
    id_documento BIGINT NOT NULL,
    id_firmante VARCHAR(100) NOT NULL,
    nombre_firmante VARCHAR(200) NOT NULL,
    email_firmante VARCHAR(200),
    estado VARCHAR(20) NOT NULL,
    proveedor VARCHAR(100),
    id_transaccion_proveedor VARCHAR(150),
    fecha_envio TIMESTAMP,
    fecha_firma TIMESTAMP,
    ip_firmante VARCHAR(45),
    huella_digital VARCHAR(255),
    certificado_serial VARCHAR(100),
    motivo_rechazo TEXT,
    creado_por VARCHAR(100),
    actualizado_por VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_documentos_plantillas_tipo ON documentos_plantillas(tipo_documento);
CREATE INDEX IF NOT EXISTS idx_documentos_plantillas_version ON documentos_plantillas(version);
CREATE INDEX IF NOT EXISTS idx_documentos_generados_entidad ON documentos_generados(entidad, id_entidad);
CREATE INDEX IF NOT EXISTS idx_documentos_generados_fecha ON documentos_generados(fecha_generacion);
CREATE INDEX IF NOT EXISTS idx_firmas_electronicas_documento ON firmas_electronicas(id_documento);
CREATE INDEX IF NOT EXISTS idx_firmas_electronicas_fecha ON firmas_electronicas(fecha_firma);
