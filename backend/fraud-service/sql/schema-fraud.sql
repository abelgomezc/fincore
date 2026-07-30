CREATE TABLE IF NOT EXISTS reglas_antifraude (
    id_regla BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(500),
    tipo_regla VARCHAR(50) NOT NULL,
    parametros VARCHAR(1000),
    umbral NUMERIC(18,2),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    prioridad INTEGER NOT NULL DEFAULT 1,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS evaluaciones_fraude (
    id_evaluacion BIGSERIAL PRIMARY KEY,
    id_transaccion VARCHAR(50) NOT NULL UNIQUE,
    id_cuenta_origen VARCHAR(50),
    id_cuenta_destino VARCHAR(50),
    monto NUMERIC(18,2),
    moneda VARCHAR(3),
    decision VARCHAR(20) NOT NULL,
    puntuacion_riesgo NUMERIC(5,2),
    motivo VARCHAR(500),
    fecha_evaluacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 1
);
