-- ===================================================================
-- FinCore Ledger Service — Esquema de base de datos
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- V1__esquema_ledger.sql
-- ===================================================================

-- Extensiones
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ===================================================================
-- PLAN DE CUENTAS CONTABLES
-- ===================================================================
CREATE TABLE plan_cuentas (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(10) UNIQUE NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    naturaleza VARCHAR(10) NOT NULL,
    nivel INTEGER NOT NULL,
    codigo_padre VARCHAR(10),
    es_hoja BOOLEAN NOT NULL DEFAULT TRUE,
    es_activa BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW()
);

ALTER TABLE plan_cuentas ADD CONSTRAINT chk_tipo_cuenta_contable
    CHECK (tipo IN ('ACTIVO', 'PASIVO', 'PATRIMONIO', 'INGRESO', 'GASTO'));

ALTER TABLE plan_cuentas ADD CONSTRAINT chk_naturaleza_cuenta
    CHECK (naturaleza IN ('DEUDORA', 'ACREEDORA'));

-- ===================================================================
-- ASIENTOS CONTABLES (INMUTABLES — nunca se modifican)
-- ===================================================================
CREATE TABLE asientos_contables (
    id BIGSERIAL PRIMARY KEY,
    numero_asiento VARCHAR(30) UNIQUE NOT NULL,
    descripcion TEXT NOT NULL,
    id_referencia BIGINT,
    tipo_referencia VARCHAR(50),
    id_usuario VARCHAR(100),
    ip_origen VARCHAR(45),
    trace_id VARCHAR(100),
    fecha_asiento TIMESTAMP NOT NULL DEFAULT NOW(),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW()
    -- NO fecha_actualizacion — entidad inmutable
);

ALTER TABLE asientos_contables ADD CONSTRAINT chk_estado_asiento
    CHECK (estado IN ('ACTIVO', 'REVERSADO'));

CREATE INDEX idx_asientos_referencia ON asientos_contables(id_referencia, tipo_referencia);
CREATE INDEX idx_asientos_fecha ON asientos_contables(fecha_asiento DESC);
CREATE INDEX idx_asientos_trace ON asientos_contables(trace_id);

-- ===================================================================
-- LÍNEAS DE ASIENTO (INMUTABLES — nunca se modifican)
-- ===================================================================
CREATE TABLE lineas_asiento (
    id BIGSERIAL PRIMARY KEY,
    id_asiento BIGINT NOT NULL REFERENCES asientos_contables(id) ON DELETE CASCADE,
    codigo_cuenta VARCHAR(10) NOT NULL REFERENCES plan_cuentas(codigo),
    id_cuenta_bancaria BIGINT,
    tipo_movimiento VARCHAR(10) NOT NULL,
    monto DECIMAL(18,2) NOT NULL CHECK (monto > 0),
    descripcion TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW()
    -- NO fecha_actualizacion — entidad inmutable
);

ALTER TABLE lineas_asiento ADD CONSTRAINT chk_tipo_movimiento
    CHECK (tipo_movimiento IN ('DEBITO', 'CREDITO'));

CREATE INDEX idx_lineas_asiento ON lineas_asiento(id_asiento);
CREATE INDEX idx_lineas_cuenta ON lineas_asiento(id_cuenta_bancaria);
CREATE INDEX idx_lineas_codigo_cuenta ON lineas_asiento(codigo_cuenta);

-- ===================================================================
-- SECUENCIA PARA NÚMEROS DE ASIENTO
-- ===================================================================
CREATE SEQUENCE IF NOT EXISTS secuencia_asientos
    START 1
    INCREMENT 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;
