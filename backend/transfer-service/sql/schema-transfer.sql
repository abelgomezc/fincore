CREATE TABLE IF NOT EXISTS transferencias (
    id BIGSERIAL PRIMARY KEY,
    estado VARCHAR(50) NOT NULL,
    monto NUMERIC(18,2) NOT NULL,
    moneda VARCHAR(3) NOT NULL,
    cuenta_origen VARCHAR(50) NOT NULL,
    cuenta_destino VARCHAR(50) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS historial_estados_transferencia (
    id BIGSERIAL PRIMARY KEY,
    transferencia_id BIGINT NOT NULL,
    estado_anterior VARCHAR(50),
    estado_nuevo VARCHAR(50) NOT NULL,
    fecha_cambio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motivo TEXT
);