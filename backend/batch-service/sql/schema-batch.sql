CREATE SCHEMA IF NOT EXISTS batch;

CREATE TABLE IF NOT EXISTS batch.ejecucion_batch (
    id BIGSERIAL PRIMARY KEY,
    nombre_job VARCHAR(255) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP,
    registros_procesados BIGINT DEFAULT 0,
    registros_fallidos BIGINT DEFAULT 0,
    mensaje_error TEXT
);

CREATE TABLE IF NOT EXISTS batch.movimiento_pendiente (
    id BIGSERIAL PRIMARY KEY,
    cuenta_origen_id BIGINT NOT NULL,
    cuenta_destino_id BIGINT NOT NULL,
    monto NUMERIC(18,2) NOT NULL,
    fecha_movimiento TIMESTAMP NOT NULL,
    estado VARCHAR(50) NOT NULL,
    referencia VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS batch.interes_generado (
    id BIGSERIAL PRIMARY KEY,
    cuenta_id BIGINT NOT NULL,
    saldo_promedio NUMERIC(18,2) NOT NULL,
    tasa_interes NUMERIC(5,4) NOT NULL,
    interes_calculado NUMERIC(18,2) NOT NULL,
    fecha_calculo TIMESTAMP NOT NULL,
    estado VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS batch.conciliacion_movimiento (
    id BIGSERIAL PRIMARY KEY,
    movimiento_id BIGINT NOT NULL,
    lote_conciliacion_id BIGINT NOT NULL,
    estado_conciliacion VARCHAR(50) NOT NULL,
    observaciones TEXT,
    fecha_conciliacion TIMESTAMP
);
