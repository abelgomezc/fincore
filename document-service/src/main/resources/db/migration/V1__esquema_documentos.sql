-- ============================================
-- Document Service - Esquema inicial
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- ============================================

CREATE TABLE IF NOT EXISTS documentos_plantillas (
    bigserial PRIMARY KEY,
    varchar(50) tipo_documento NOT NULL,
    varchar(150) nombre NOT NULL,
    text descripcion,
    text contenido_html NOT NULL,
    integer version NOT NULL DEFAULT 1,
    boolean activo NOT NULL DEFAULT TRUE,
    timestamp fecha_creacion NOT NULL DEFAULT CURRENT_TIMESTAMP,
    timestamp fecha_actualizacion NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS documentos_generados (
    bigserial PRIMARY KEY,
    varchar(50) entidad NOT NULL,
    varchar(100) id_entidad NOT NULL,
    varchar(50) tipo_documento NOT NULL,
    varchar(20) estado NOT NULL,
    varchar(255) nombre_archivo,
    text url_archivo,
    text contenido_html,
    varchar(255) hash_archivo,
    bigint id_plantilla,
    timestamp fecha_generacion,
    timestamp fecha_firma,
    timestamp fecha_vencimiento,
    varchar(100) creado_por,
    varchar(100) actualizado_por,
    bigint version NOT NULL DEFAULT 0,
    timestamp fecha_creacion DEFAULT CURRENT_TIMESTAMP,
    timestamp fecha_actualizacion DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS firmas_electronicas (
    bigserial PRIMARY KEY,
    bigint id_documento NOT NULL,
    varchar(100) id_firmante NOT NULL,
    varchar(200) nombre_firmante NOT NULL,
    varchar(200) email_firmante,
    varchar(20) estado NOT NULL,
    varchar(100) proveedor,
    varchar(150) id_transaccion_proveedor,
    timestamp fecha_envio,
    timestamp fecha_firma,
    varchar(45) ip_firmante,
    varchar(255) huella_digital,
    varchar(100) certificado_serial,
    text motivo_rechazo,
    varchar(100) creado_por,
    varchar(100) actualizado_por,
    bigint version NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_documentos_plantillas_tipo ON documentos_plantillas(tipo_documento);
CREATE INDEX IF NOT EXISTS idx_documentos_plantillas_version ON documentos_plantillas(version);
CREATE INDEX IF NOT EXISTS idx_documentos_generados_entidad ON documentos_generados(entidad, id_entidad);
CREATE INDEX IF NOT EXISTS idx_documentos_generados_fecha ON documentos_generados(fecha_generacion);
CREATE INDEX IF NOT EXISTS idx_firmas_electronicas_documento ON firmas_electronicas(id_documento);
CREATE INDEX IF NOT EXISTS idx_firmas_electronicas_fecha ON firmas_electronicas(fecha_firma);
