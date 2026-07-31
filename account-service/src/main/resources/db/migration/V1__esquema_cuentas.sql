-- ===================================================================
-- FinCore Account Service — Esquema de base de datos
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- V1__esquema_cuentas.sql
-- ===================================================================

-- ===================================================================
-- TIPOS DE CUENTA
-- ===================================================================
CREATE TABLE tipos_cuenta (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(10) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tasa_interes_anual DECIMAL(8,4) DEFAULT 0,
    saldo_minimo DECIMAL(18,2) DEFAULT 0,
    limite_transaccion_diario DECIMAL(18,2),
    limite_monto_por_transaccion DECIMAL(18,2),
    permite_sobregiro BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO tipos_cuenta (codigo, nombre, descripcion, tasa_interes_anual, saldo_minimo, limite_transaccion_diario, limite_monto_por_transaccion, permite_sobregiro, fecha_creacion) VALUES
('CA', 'Cuenta Corriente', 'Cuenta de uso diario con chequera', 0.0000, 0.00, 5000.00, 2000.00, FALSE, NOW()),
('CC', 'Cuenta Ahorros', 'Cuenta de ahorros con rendimiento mensual', 2.5000, 10.00, 5000.00, 2000.00, FALSE, NOW()),
('PF', 'Plazo Fijo', 'Cuenta con plazo fijo y tasa fija', 5.5000, 100.00, 10000.00, 5000.00, FALSE, NOW());

-- ===================================================================
-- CUENTAS BANCARIAS
-- ===================================================================
CREATE TABLE cuentas (
    id BIGSERIAL PRIMARY KEY,
    numero_cuenta VARCHAR(20) UNIQUE NOT NULL,
    id_cliente BIGINT NOT NULL,
    id_tipo_cuenta BIGINT NOT NULL REFERENCES tipos_cuenta(id),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',
    moneda VARCHAR(3) NOT NULL DEFAULT 'USD',
    saldo_contable DECIMAL(18,2) NOT NULL DEFAULT 0,
    saldo_disponible DECIMAL(18,2) NOT NULL DEFAULT 0,
    saldo_retenido DECIMAL(18,2) NOT NULL DEFAULT 0,
    saldo_proyectado DECIMAL(18,2) NOT NULL DEFAULT 0,
    motivo_bloqueo TEXT,
    fecha_apertura DATE NOT NULL,
    fecha_ultimo_movimiento TIMESTAMP,
    fecha_cierre DATE,
    version BIGINT NOT NULL DEFAULT 0,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW()
);

ALTER TABLE cuentas ADD CONSTRAINT chk_estado_cuenta
    CHECK (estado IN ('ACTIVA', 'BLOQUEADA', 'CONGELADA', 'CERRADA'));

ALTER TABLE cuentas ADD CONSTRAINT chk_saldo_disponible_no_negativo
    CHECK (saldo_disponible >= 0);

CREATE INDEX idx_cuentas_numero ON cuentas(numero_cuenta);
CREATE INDEX idx_cuentas_cliente ON cuentas(id_cliente);
CREATE INDEX idx_cuentas_tipo ON cuentas(id_tipo_cuenta);

-- ===================================================================
-- SALDOS HISTÓRICOS (snapshot diario)
-- ===================================================================
CREATE TABLE saldos_historicos (
    id BIGSERIAL PRIMARY KEY,
    id_cuenta BIGINT NOT NULL REFERENCES cuentas(id) ON DELETE CASCADE,
    fecha_snapshot DATE NOT NULL DEFAULT CURRENT_DATE,
    saldo_contable DECIMAL(18,2) NOT NULL DEFAULT 0,
    saldo_disponible DECIMAL(18,2) NOT NULL DEFAULT 0,
    saldo_retenido DECIMAL(18,2) NOT NULL DEFAULT 0,
    saldo_proyectado DECIMAL(18,2) NOT NULL DEFAULT 0,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_saldos_historicos_cuenta_fecha
    ON saldos_historicos(id_cuenta, fecha_snapshot);

-- ===================================================================
-- LÍMITES DE TRANSACCIÓN POR CUENTA
-- ===================================================================
CREATE TABLE limites_transaccion (
    id BIGSERIAL PRIMARY KEY,
    id_cuenta BIGINT NOT NULL REFERENCES cuentas(id) ON DELETE CASCADE,
    monto_maximo_diario DECIMAL(18,2) NOT NULL DEFAULT 5000.00,
    monto_maximo_por_transaccion DECIMAL(18,2) NOT NULL DEFAULT 2000.00,
    monto_maximo_mensual DECIMAL(18,2) NOT NULL DEFAULT 20000.00,
    contador_diario DECIMAL(18,2) NOT NULL DEFAULT 0,
    contador_mensual DECIMAL(18,2) NOT NULL DEFAULT 0,
    fecha_ultima_transaccion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_limites_cuenta ON limites_transaccion(id_cuenta);

-- ===================================================================
-- BENEFICIARIOS FRECUENTES
-- ===================================================================
CREATE TABLE beneficiarios_frecuentes (
    id BIGSERIAL PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    id_cuenta_beneficiario BIGINT NOT NULL REFERENCES cuentas(id) ON DELETE CASCADE,
    nombre_beneficiario VARCHAR(255) NOT NULL,
    numero_cuenta VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_beneficiarios_cliente ON beneficiarios_frecuentes(id_cliente);
CREATE INDEX idx_beneficiarios_cuenta ON beneficiarios_frecuentes(id_cuenta_beneficiario);
