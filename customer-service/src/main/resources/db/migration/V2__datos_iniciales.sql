-- ===================================================================
-- FinCore Customer Service — Datos iniciales
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- V2__datos_iniciales.sql
-- ===================================================================

-- Los datos de clientes (Abel y María) ya están en seed-data.sql
-- Este script inserta datos adicionales necesarios para el servicio

-- ===================================================================
-- VERIFICACIONES KYC INICIALES
-- ===================================================================
INSERT INTO kyc_verificaciones (id_cliente, estado, fecha_verificacion, verificado_por, observaciones)
VALUES
(1, 'APROBADO', '2024-01-16 09:30:00', 'sistema', 'Verificación automática completada — cédula vigente'),
(2, 'APROBADO', '2024-01-16 09:45:00', 'sistema', 'Verificación automática completada — cédula vigente');

-- Nota: los clientes, documentos, direcciones y contactos de emergencia
-- se insertan mediante los microservicios al momento del registro.
-- Este script puede usarse para cargar datos de prueba adicionales.
