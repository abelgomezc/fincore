-- ============================================
-- Validaciones de identidad y biometría
-- ============================================

CREATE TABLE IF NOT EXISTS validaciones_identidad (
    bigserial PRIMARY KEY,
    bigint id_cliente NOT NULL,
    varchar(50) tipo_validacion NOT NULL,
    varchar(20) estado NOT NULL,
    varchar(100) proveedor,
    integer puntaje_confianza,
    text detalle,
    timestamp fecha_validacion,
    varchar(100) id_transaccion,
    varchar(100) creado_por,
    varchar(100) actualizado_por,
    bigint version NOT NULL DEFAULT 0,
    timestamp fecha_creacion DEFAULT CURRENT_TIMESTAMP,
    timestamp fecha_actualizacion DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sesiones_biometricas (
    bigserial PRIMARY KEY,
    bigint id_cliente NOT NULL,
    varchar(50) tipo_biometria NOT NULL,
    varchar(20) estado NOT NULL,
    varchar(100) proveedor,
    integer puntaje_confianza,
    timestamp fecha_inicio NOT NULL,
    timestamp fecha_fin,
    varchar(150) id_sesion_proveedor,
    text detalle,
    varchar(100) creado_por,
    varchar(100) actualizado_por,
    bigint version NOT NULL DEFAULT 0,
    timestamp fecha_creacion DEFAULT CURRENT_TIMESTAMP,
    timestamp fecha_actualizacion DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_validaciones_identidad_cliente ON validaciones_identidad(id_cliente);
CREATE INDEX IF NOT EXISTS idx_validaciones_identidad_fecha ON validaciones_identidad(fecha_validacion);
CREATE INDEX IF NOT EXISTS idx_sesiones_biometricas_cliente ON sesiones_biometricas(id_cliente);
CREATE INDEX IF NOT EXISTS idx_sesiones_biometricas_fecha ON sesiones_biometricas(fecha_inicio);
