-- ===================================================================
-- FinCore Auth Service — Agregar estado ELIMINADO
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- V4__agregar_estado_eliminado_usuario.sql
-- ===================================================================

ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS chk_estado_usuario;

ALTER TABLE usuarios ADD CONSTRAINT chk_estado_usuario
    CHECK (estado IN ('ACTIVO', 'INACTIVO', 'BLOQUEADO', 'SUSPENDIDO', 'ELIMINADO'));
