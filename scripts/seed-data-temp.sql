\set ON_ERROR_STOP off
-- ===================================================================
-- FinCore — Datos semilla para pruebas
-- © 2026 Abel Gomez. Todos los derechos reservados.
--
-- Este script carga datos realistas para demostrar el funcionamiento
-- completo del sistema. Incluye:
--   - 2 clientes: Abel Gomez y María López
--   - 2 cuentas de ahorros con saldos iniciales
--   - Plan de cuentas contable completo (códigos 1xxx-5xxx)
--   - Tipos de cuenta: corriente, ahorros, plazo fijo
--   - Usuario backoffice: supervisor@fincore.com
--
-- NOTA: Este script debe ejecutarse DESPUÉS de las migraciones
-- de Flyway de cada microservicio. Los datos se insertan en las
-- bases de datos correspondientes.
-- ===================================================================

-- ===================================================================
-- 1. PLAN DE CUENTAS CONTABLES (fin_core_ledger)
-- ===================================================================
-- Estructura basada en el plan de cuentas bancario estándar

-- ACTIVOS (1xxx)
INSERT INTO plan_cuentas (codigo, nombre, tipo, naturaleza, nivel, codigo_padre, es_hoja, es_activa) VALUES
('1000', 'Activo',                'ACTIVO',    'DEUDORA', 1, NULL, FALSE, TRUE),
('1001', 'Caja y efectivo',       'ACTIVO',    'DEUDORA', 2, '1000', TRUE, TRUE),
('1010', 'Depósitos en Banco Central', 'ACTIVO', 'DEUDORA', 2, '1000', TRUE, TRUE),
('1020', 'Inversiones',           'ACTIVO',    'DEUDORA', 2, '1000', TRUE, TRUE),
('1100', 'Cartera de créditos',   'ACTIVO',    'DEUDORA', 2, '1000', TRUE, TRUE),
('1200', 'Cuentas por cobrar',    'ACTIVO',    'DEUDORA', 2, '1000', TRUE, TRUE);

-- PASIVOS (2xxx)
INSERT INTO plan_cuentas (codigo, nombre, tipo, naturaleza, nivel, codigo_padre, es_hoja, es_activa) VALUES
('2000', 'Pasivo',                'PASIVO',    'ACREEDORA', 1, NULL, FALSE, TRUE),
('2001', 'Depósitos en cuenta corriente', 'PASIVO', 'ACREEDORA', 2, '2000', TRUE, TRUE),
('2002', 'Depósitos en cuenta ahorros',   'PASIVO', 'ACREEDORA', 2, '2000', TRUE, TRUE),
('2010', 'Depósitos a plazo fijo',        'PASIVO', 'ACREEDORA', 2, '2000', TRUE, TRUE),
('2100', 'Fondos en tránsito',            'PASIVO', 'ACREEDORA', 2, '2000', TRUE, TRUE),
('2200', 'Retenciones',                   'PASIVO', 'ACREEDORA', 2, '2000', TRUE, TRUE);

-- PATRIMONIO (3xxx)
INSERT INTO plan_cuentas (codigo, nombre, tipo, naturaleza, nivel, codigo_padre, es_hoja, es_activa) VALUES
('3000', 'Patrimonio',            'PATRIMONIO', 'ACREEDORA', 1, NULL, FALSE, TRUE),
('3001', 'Capital social',        'PATRIMONIO', 'ACREEDORA', 2, '3000', TRUE, TRUE),
('3100', 'Reservas',              'PATRIMONIO', 'ACREEDORA', 2, '3000', TRUE, TRUE);

-- INGRESOS (4xxx)
INSERT INTO plan_cuentas (codigo, nombre, tipo, naturaleza, nivel, codigo_padre, es_hoja, es_activa) VALUES
('4000', 'Ingresos',              'INGRESO',   'DEUDORA', 1, NULL, FALSE, TRUE),
('4001', 'Intereses ganados en créditos', 'INGRESO', 'DEUDORA', 2, '4000', TRUE, TRUE),
('4010', 'Comisiones por servicios',      'INGRESO', 'DEUDORA', 2, '4000', TRUE, TRUE),
('4020', 'Comisiones por transferencias', 'INGRESO', 'DEUDORA', 2, '4000', TRUE, TRUE);

-- GASTOS (5xxx)
INSERT INTO plan_cuentas (codigo, nombre, tipo, naturaleza, nivel, codigo_padre, es_hoja, es_activa) VALUES
('5000', 'Gastos',                'GASTO',     'ACREEDORA', 1, NULL, FALSE, TRUE),
('5001', 'Intereses pagados en depósitos', 'GASTO', 'ACREEDORA', 2, '5000', TRUE, TRUE),
('5010', 'Gastos operativos',     'GASTO',     'ACREEDORA', 2, '5000', TRUE, TRUE);

-- ===================================================================
-- 2. CLIENTES (fincore_customers)
-- ===================================================================

INSERT INTO clientes (
    id, tipo_cliente, estado, primer_nombre, segundo_nombre,
    primer_apellido, segundo_apellido, fecha_nacimiento,
    genero, email, telefono, direccion, ciudad, pais,
    fecha_registro, fecha_actualizacion
) VALUES
(1, 'NATURAL', 'ACTIVO', 'Abel', 'Alejandro', 'Gomez', 'Salazar',
    '1990-05-15', 'MASCULINO', 'abel.gomez@fincore.com',
    '+593991234567', 'Av. Amazonas N34-567, Quito', 'Quito', 'EC',
    '2024-01-15 10:00:00', '2024-01-15 10:00:00'),
(2, 'NATURAL', 'ACTIVO', 'María', 'Fernanda', 'López', 'García',
    '1992-08-22', 'FEMENINO', 'maria.lopez@fincore.com',
    '+593987654321', 'Calle 10 de Agosto N12-34, Guayaquil', 'Guayaquil', 'EC',
    '2024-01-15 10:00:00', '2024-01-15 10:00:00');

-- Documentos de identidad
INSERT INTO documentos_identidad (id_cliente, tipo_documento, numero_documento, fecha_expedicion, fecha_expiracion, pais_emision, verificado) VALUES
(1, 'CEDULA', '1712345678', '2018-05-15', '2028-05-15', 'EC', TRUE),
(2, 'CEDULA', '0102345678', '2019-08-22', '2029-08-22', 'EC', TRUE);

-- KYC verificaciones (aprobadas)
INSERT INTO kyc_verificaciones (id_cliente, estado, fecha_verificacion, verificado_por, observaciones) VALUES
(1, 'APROBADO', '2024-01-16 09:30:00', 'sistema', 'Verificación automática completada'),
(2, 'APROBADO', '2024-01-16 09:45:00', 'sistema', 'Verificación automática completada');

-- Direcciones
INSERT INTO direcciones (id_cliente, tipo_direccion, calle_principal, calle_secundaria, ciudad, provincia, pais, codigo_postal) VALUES
(1, 'RESIDENCIA', 'Av. Amazonas N34-567', 'Edificio FinCore Piso 3', 'Quito', 'Pichincha', 'EC', '010101'),
(2, 'RESIDENCIA', 'Calle 10 de Agosto N12-34', 'Apto 5B', 'Guayaquil', 'Guayas', 'EC', '020101');

-- Contactos de emergencia
INSERT INTO contactos_emergencia (id_cliente, nombre, telefono, parentesco) VALUES
(1, 'Ana Gomez', '+593998765432', 'HERMANA'),
(2, 'Carlos López', '+593976543210', 'HERMANO');

-- ===================================================================
-- 3. USUARIOS DE AUTENTICACIÓN (fincore_auth)
-- ===================================================================

INSERT INTO usuarios (email, password_hash, primer_nombre, primer_apellido, rol, estado, intentos_fallidos, fecha_creacion, fecha_actualizacion) VALUES
('abel.gomez@fincore.com', '$2a$10$J6QU3iYz0Y5nQ8wR7sT2e.5K8mN0pQ3rS6tU9vW2xY5zA8bC1dE4fG7hJ0k', 'Abel', 'Gomez', 'CLIENTE', 'ACTIVO', 0, '2024-01-15 10:00:00', '2024-01-15 10:00:00'),
('maria.lopez@fincore.com', '$2a$10$K7RT4jZa1Y6oP9wQ8sT3u.6L9nO1pQ4rS7tU0vW3xY6zA9bC2dE5fG8hJ1k', 'María', 'López', 'CLIENTE', 'ACTIVO', 0, '2024-01-15 10:00:00', '2024-01-15 10:00:00'),
('supervisor@fincore.com', '$2a$10$M8ST5kAb2Z7pQ0wR9sT4v.7M0oP2qR5sT8uV1wX4yZ7aB0cD3eF6gH9jK2l', 'Supervisor', 'FinCore', 'SUPERVISOR', 'ACTIVO', 0, '2024-01-15 10:00:00', '2024-01-15 10:00:00'),
('auditor@fincore.com', '$2a$10$N9TU6lBc3A8qR1wS0tU5w.8N1pQ3rS6tU9vW2xY5zA8bC1dE4fG7hJ0kM3n', 'Auditor', 'FinCore', 'AUDITOR', 'ACTIVO', 0, '2024-01-15 10:00:00', '2024-01-15 10:00:00'),
('admin@fincore.com', '$2a$10$O0UV7mCd4B9sR2wT1uV6x.9O2pQ4rS7tU0vW3xY6zA9bC2dE5fG8hJ1kM4n', 'Admin', 'FinCore', 'ADMIN', 'ACTIVO', 0, '2024-01-15 10:00:00', '2024-01-15 10:00:00');

-- ===================================================================
-- 4. TIPOS DE CUENTA (fincore_accounts)
-- ===================================================================

INSERT INTO tipos_cuenta (codigo, nombre, descripcion, tasa_interes_anual, saldo_minimo, limite_transaccion_diario, limite_monto_por_transaccion, permite_sobregiro, fecha_creacion) VALUES
('CA', 'Cuenta Corriente', 'Cuenta de uso diario con chequera', 0.0000, 0.00, 5000.00, 2000.00, FALSE, '2024-01-15 10:00:00'),
('CC', 'Cuenta Ahorros', 'Cuenta de ahorros con rendimiento mensual', 2.5000, 10.00, 5000.00, 2000.00, FALSE, '2024-01-15 10:00:00'),
('PF', 'Plazo Fijo', 'Cuenta con plazo fijo y tasa fija', 5.5000, 100.00, 10000.00, 5000.00, FALSE, '2024-01-15 10:00:00');

-- ===================================================================
-- 5. CUENTAS BANCARIAS (fincore_accounts)
-- ===================================================================

INSERT INTO cuentas (
    id, numero_cuenta, id_cliente, id_tipo_cuenta, estado, moneda,
    saldo_contable, saldo_disponible, saldo_retenido, saldo_proyectado,
    fecha_apertura, fecha_ultimo_movimiento, version, fecha_creacion, fecha_actualizacion
) VALUES
(1, '202600000001', 1, 2, 'ACTIVA', 'USD',
    1000.00, 1000.00, 0.00, 1000.00,
    '2024-01-15', '2024-01-15 10:00:00', 0, '2024-01-15 10:00:00', '2024-01-15 10:00:00'),
(2, '202600000002', 2, 2, 'ACTIVA', 'USD',
    500.00, 500.00, 0.00, 500.00,
    '2024-01-15', '2024-01-15 10:00:00', 0, '2024-01-15 10:00:00', '2024-01-15 10:00:00');

-- Beneficiarios frecuentes
INSERT INTO beneficiarios_frecuentes (id_cliente, id_cuenta_beneficiario, nombre_beneficiario, numero_cuenta, activo) VALUES
(1, 2, 'María López', '202600000002', TRUE),
(2, 1, 'Abel Gomez', '202600000001', TRUE);

-- ===================================================================
-- 6. ASIENTOS CONTABLES INICIALES (fincore_ledger)
-- ===================================================================
-- Asiento inicial: apertura de cuentas con depósitos iniciales

-- Asiento 1: Depósito inicial de Abel ($1,000)
INSERT INTO asientos_contables (numero_asiento, descripcion, id_referencia, tipo_referencia, id_usuario, ip_origen, trace_id, estado, fecha_asiento, fecha_creacion)
VALUES ('AS-2026-000001', 'Apertura de cuenta — depósito inicial', 1, 'APERTURA_CUENTA', 'sistema', '127.0.0.1', 'init-001', 'ACTIVO', '2024-01-15 10:00:00', '2024-01-15 10:00:00');

INSERT INTO lineas_asiento (id_asiento, codigo_cuenta, id_cuenta_bancaria, tipo_movimiento, monto, descripcion, fecha_creacion)
SELECT currval('asientos_contables_id_seq'), '1001', 1, 'DEBITO', 1000.00, 'Apertura de cuenta — efectivo', '2024-01-15 10:00:00'
UNION ALL
SELECT currval('asientos_contables_id_seq'), '2002', 1, 'CREDITO', 1000.00, 'Apertura de cuenta — depósito inicial', '2024-01-15 10:00:00';

-- Asiento 2: Depósito inicial de María ($500)
INSERT INTO asientos_contables (numero_asiento, descripcion, id_referencia, tipo_referencia, id_usuario, ip_origen, trace_id, estado, fecha_asiento, fecha_creacion)
VALUES ('AS-2026-000002', 'Apertura de cuenta — depósito inicial', 2, 'APERTURA_CUENTA', 'sistema', '127.0.0.1', 'init-002', 'ACTIVO', '2024-01-15 10:00:00', '2024-01-15 10:00:00');

INSERT INTO lineas_asiento (id_asiento, codigo_cuenta, id_cuenta_bancaria, tipo_movimiento, monto, descripcion, fecha_creacion)
SELECT currval('asientos_contables_id_seq'), '1001', 2, 'DEBITO', 500.00, 'Apertura de cuenta — efectivo', '2024-01-15 10:00:00'
UNION ALL
SELECT currval('asientos_contables_id_seq'), '2002', 2, 'CREDITO', 500.00, 'Apertura de cuenta — depósito inicial', '2024-01-15 10:00:00';

-- ===================================================================
-- 7. LÍMITES DE TRANSACCIÓN (fincore_accounts)
-- ===================================================================

INSERT INTO limites_transaccion (id_cuenta, monto_maximo_diario, monto_maximo_por_transaccion, monto_maximo_mensual, contador_diario, contador_mensual, fecha_ultima_transaccion, fecha_creacion)
VALUES
(1, 5000.00, 2000.00, 20000.00, 0.00, 0.00, '2024-01-15 10:00:00', '2024-01-15 10:00:00'),
(2, 5000.00, 2000.00, 20000.00, 0.00, 0.00, '2024-01-15 10:00:00', '2024-01-15 10:00:00');

-- ===================================================================
-- 8. PERFIL TRANSACCIONAL INICIAL (fincore_fraud)
-- ===================================================================

INSERT INTO perfil_transaccional (id_cliente, promedio_monto_30d, maximo_monto_30d, total_transferencias_30d, paises_habituales, dispositivos_habituales, horarios_habituales, fecha_actualizacion)
VALUES
(1, 0.00, 0.00, 0, '["EC"]', '[]', '{"morning": 0.0, "afternoon": 0.0, "night": 0.0, "early_morning": 0.0}', '2024-01-15 10:00:00'),
(2, 0.00, 0.00, 0, '["EC"]', '[]', '{"morning": 0.0, "afternoon": 0.0, "night": 0.0, "early_morning": 0.0}', '2024-01-15 10:00:00');

-- ===================================================================
-- 9. REGISTROS DE AUDITORÍA INICIALES (fincore_audit)
-- ===================================================================

INSERT INTO registros_auditoria (trace_id, servicio, endpoint, metodo_http, id_usuario, rol_usuario, ip_origen, id_recurso, tipo_recurso, accion, resultado, request_body, response_codigo, tiempo_respuesta_ms, detalle, fecha_creacion)
VALUES
('init-001', 'CUSTOMER-SERVICE', '/api/clientes', 'POST', '1', 'CLIENTE', '127.0.0.1', '1', 'CLIENTE', 'CREAR_CLIENTE', 'EXITOSO', '{"tipo":"NATURAL","nombre":"Abel Gomez"}', 201, 45, 'Cliente creado e initializado', '2024-01-15 10:00:00'),
('init-002', 'CUSTOMER-SERVICE', '/api/clientes', 'POST', '2', 'CLIENTE', '127.0.0.1', '2', 'CLIENTE', 'CREAR_CLIENTE', 'EXITOSO', '{"tipo":"NATURAL","nombre":"María López"}', 201, 38, 'Cliente creado e initializado', '2024-01-15 10:00:00');

-- ===================================================================
-- Resumen de datos semilla
-- ===================================================================
-- Clientes: 2 (Abel Gomez ID=1, María López ID=2)
-- Cuentas: 2 (Abel ID=1 saldo $1,000, María ID=2 saldo $500)
-- Tipos de cuenta: 3 (CA, CC, PF)
-- Plan de cuentas: 19 códigos (1000-5010)
-- Asientos iniciales: 2 (apertura de cuentas)
-- Usuarios auth: 5 (2 clientes + supervisor + auditor + admin)
-- Límites: 2 (uno por cuenta)
-- Perfiles transaccionales: 2
-- Auditoría: 2 registros iniciales
-- ===================================================================
