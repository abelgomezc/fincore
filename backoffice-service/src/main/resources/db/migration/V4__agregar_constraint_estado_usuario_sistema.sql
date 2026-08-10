-- ===================================================================
-- FinCore Backoffice Service — Agregar constraint de estado
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- V4__agregar_constraint_estado_usuario_sistema.sql
-- ===================================================================

-- Agregar constraint solo si no existe
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.constraint_column_usage
        WHERE table_name = 'usuarios_sistema' AND constraint_name = 'chk_estado_usuario_sistema'
    ) THEN
        ALTER TABLE usuarios_sistema ADD CONSTRAINT chk_estado_usuario_sistema
            CHECK (estado IN ('ACTIVO', 'INACTIVO', 'SUSPENDIDO', 'ELIMINADO'));
    END IF;
END $$;
