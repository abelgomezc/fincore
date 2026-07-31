-- ===================================================================
-- FinCore Customer Service — Esquema de base de datos
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- V1__esquema_clientes.sql
-- ===================================================================

-- ===================================================================
-- CLIENTES
-- ===================================================================
CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    tipo_cliente VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    primer_nombre VARCHAR(100) NOT NULL,
    segundo_nombre VARCHAR(100),
    primer_apellido VARCHAR(100) NOT NULL,
    segundo_apellido VARCHAR(100),
    fecha_nacimiento DATE,
    genero VARCHAR(20),
    email VARCHAR(255) UNIQUE,
    telefono VARCHAR(20),
    direccion TEXT,
    ciudad VARCHAR(100),
    pais VARCHAR(3) DEFAULT 'EC',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

ALTER TABLE clientes ADD CONSTRAINT chk_tipo_cliente
    CHECK (tipo_cliente IN ('NATURAL', 'JURIDICA'));

ALTER TABLE clientes ADD CONSTRAINT chk_estado_cliente
    CHECK (estado IN ('ACTIVO', 'INACTIVO', 'BLOQUEADO', 'SUSPENDIDO'));

ALTER TABLE clientes ADD CONSTRAINT chk_genero
    CHECK (genero IN ('MASCULINO', 'FEMENINO', 'OTRO'));

-- ===================================================================
-- DOCUMENTOS DE IDENTIDAD
-- ===================================================================
CREATE TABLE documentos_identidad (
    id BIGSERIAL PRIMARY KEY,
    id_cliente BIGINT NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
    tipo_documento VARCHAR(20) NOT NULL,
    numero_documento VARCHAR(20) NOT NULL,
    fecha_expedicion DATE NOT NULL,
    fecha_expiracion DATE NOT NULL,
    pais_emision VARCHAR(3) NOT NULL DEFAULT 'EC',
    verificado BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_verificacion TIMESTAMP,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(id_cliente, tipo_documento)
);

ALTER TABLE documentos_identidad ADD CONSTRAINT chk_tipo_documento
    CHECK (tipo_documento IN ('CEDULA', 'PASAPORTE', 'RUC', 'LICENCIA_CONDUCIR'));

-- ===================================================================
-- DIRECCIONES DE CLIENTE
-- ===================================================================
CREATE TABLE direcciones (
    id BIGSERIAL PRIMARY KEY,
    id_cliente BIGINT NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
    tipo_direccion VARCHAR(20) NOT NULL,
    calle_principal TEXT NOT NULL,
    calle_secundaria TEXT,
    ciudad VARCHAR(100) NOT NULL,
    provincia VARCHAR(100) NOT NULL,
    pais VARCHAR(3) NOT NULL DEFAULT 'EC',
    codigo_postal VARCHAR(20),
    latitud DECIMAL(10,8),
    longitud DECIMAL(11,8),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW()
);

ALTER TABLE direcciones ADD CONSTRAINT chk_tipo_direccion
    CHECK (tipo_direccion IN ('RESIDENCIA', 'TRABAJO', 'CORRESPONSAL'));

-- ===================================================================
-- CONTACTOS DE EMERGENCIA
-- ===================================================================
CREATE TABLE contactos_emergencia (
    id BIGSERIAL PRIMARY KEY,
    id_cliente BIGINT NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
    nombre VARCHAR(255) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    parentesco VARCHAR(50) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW()
);

ALTER TABLE contactos_emergencia ADD CONSTRAINT chk_parentesco
    CHECK (parentesco IN ('HERMANO', 'HERMANA', 'PADRE', 'MADRE', 'CONYUGE', 'AMIGO', 'OTRO'));

-- ===================================================================
-- VERIFICACIONES KYC
-- ===================================================================
CREATE TABLE kyc_verificaciones (
    id BIGSERIAL PRIMARY KEY,
    id_cliente BIGINT NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_verificacion TIMESTAMP,
    verificado_por VARCHAR(100),
    observaciones TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW()
);

ALTER TABLE kyc_verificaciones ADD CONSTRAINT chk_estado_kyc
    CHECK (estado IN ('PENDIENTE', 'EN_REVISION', 'APROBADO', 'RECHAZADO'));

-- ===================================================================
-- ÍNDICES
-- ===================================================================
CREATE INDEX idx_clientes_email ON clientes(email);
CREATE INDEX idx_clientes_tipo ON clientes(tipo_cliente);
CREATE INDEX idx_documentos_numero ON documentos_identidad(numero_documento);
CREATE INDEX idx_kyc_cliente ON kyc_verificaciones(id_cliente, estado);
