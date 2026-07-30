-- Schema Account Service
CREATE TABLE IF NOT EXISTS cuentas (
    id BIGSERIAL PRIMARY KEY,
    numero_cuenta VARCHAR(20) NOT NULL UNIQUE,
    tipo_cuenta VARCHAR(20) NOT NULL,
    moneda VARCHAR(3) NOT NULL,
    saldo_contable NUMERIC(18,2) NOT NULL DEFAULT 0,
    saldo_disponible NUMERIC(18,2) NOT NULL DEFAULT 0,
    saldo_retenido NUMERIC(18,2) NOT NULL DEFAULT 0,
    saldo_proyectado NUMERIC(18,2) NOT NULL DEFAULT 0,
    estado VARCHAR(20) NOT NULL,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS movimientos (
    id BIGSERIAL PRIMARY KEY,
    cuenta_id BIGINT NOT NULL REFERENCES cuentas(id),
    tipo_movimiento VARCHAR(20) NOT NULL,
    monto NUMERIC(18,2) NOT NULL,
    moneda VARCHAR(3) NOT NULL,
    descripcion TEXT,
    saldo_anterior NUMERIC(18,2) NOT NULL,
    saldo_posterior NUMERIC(18,2) NOT NULL,
    referencia VARCHAR(50),
    estado VARCHAR(20) NOT NULL,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL DEFAULT 'system',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_movimientos_cuenta_id ON movimientos(cuenta_id);
CREATE INDEX idx_movimientos_fecha ON movimientos(created_at);