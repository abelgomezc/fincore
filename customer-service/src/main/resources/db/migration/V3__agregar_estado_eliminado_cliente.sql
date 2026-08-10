-- ===================================================================
-- FinCore Customer Service — Agregar estado ELIMINADO
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- V2__agregar_estado_eliminado_cliente.sql
-- ===================================================================

ALTER TABLE clientes DROP CONSTRAINT IF EXISTS chk_estado_cliente;

ALTER TABLE clientes ADD CONSTRAINT chk_estado_cliente
    CHECK (estado IN ('ACTIVO', 'INACTIVO', 'BLOQUEADO', 'SUSPENDIDO', 'ELIMINADO'));
