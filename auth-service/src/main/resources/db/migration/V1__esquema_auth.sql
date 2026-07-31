-- ===================================================================
-- FinCore Auth Service — Esquema de base de datos
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- V1__esquema_auth.sql
-- ===================================================================

-- Extensiones
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ===================================================================
-- USUARIOS
-- ===================================================================
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    primer_nombre VARCHAR(100) NOT NULL,
    segundo_nombre VARCHAR(100),
    primer_apellido VARCHAR(100) NOT NULL,
    segundo_apellido VARCHAR(100),
    rol VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    id_cliente BIGINT,
    intentos_fallidos INTEGER NOT NULL DEFAULT 0,
    ultimo_intento_fallido TIMESTAMP,
    fecha_bloqueo TIMESTAMP,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ===================================================================
-- REFRESH TOKENS (rotativo)
-- ===================================================================
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(512) UNIQUE NOT NULL,
    id_usuario BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    fecha_expiracion TIMESTAMP NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_revocacion TIMESTAMP,
    es_revocado BOOLEAN NOT NULL DEFAULT FALSE,
    device_id VARCHAR(255),
    ip_origen VARCHAR(45),
    user_agent TEXT
);

CREATE INDEX idx_refresh_tokens_usuario ON refresh_tokens(id_usuario);
CREATE INDEX idx_refresh_tokens_expiracion ON refresh_tokens(fecha_expiracion);

-- ===================================================================
-- SESIONES ACTIVAS (Redis-backed, pero también se persisten para auditoría)
-- ===================================================================
CREATE TABLE sesiones_activas (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255) UNIQUE NOT NULL,
    id_usuario BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    device_id VARCHAR(255),
    ip_origen VARCHAR(45),
    user_agent TEXT,
    fecha_inicio TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_ultima_actividad TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_expiracion TIMESTAMP NOT NULL,
    es_activa BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_sesiones_usuario ON sesiones_activas(id_usuario);
CREATE INDEX idx_sesiones_expiracion ON sesiones_activas(fecha_expiracion);
CREATE INDEX idx_sesiones_activas ON sesiones_activas(es_activa);

-- ===================================================================
-- PERMISOS POR ROL (para expansión futura)
-- ===================================================================
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE permisos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE rol_permisos (
    id_rol BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    id_permiso BIGINT NOT NULL REFERENCES permisos(id) ON DELETE CASCADE,
    PRIMARY KEY (id_rol, id_permiso)
);

-- ===================================================================
-- CONSTRAINTS
-- ===================================================================
ALTER TABLE usuarios ADD CONSTRAINT chk_rol_usuario
    CHECK (rol IN ('CLIENTE', 'OPERADOR', 'SUPERVISOR', 'AUDITOR', 'ADMIN'));

ALTER TABLE usuarios ADD CONSTRAINT chk_estado_usuario
    CHECK (estado IN ('ACTIVO', 'INACTIVO', 'BLOQUEADO', 'SUSPENDIDO'));
