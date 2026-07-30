-- ============================================================
-- FINCORE — Seed data inicial
-- Datos de prueba realistas para operación local
-- ============================================================

-- ============================================================
-- Catálogos
-- ============================================================

INSERT INTO tipos_persona (nombre, descripcion) VALUES
('NATURAL', 'Persona natural'),
('JURIDICA', 'Persona jurídica');

INSERT INTO tipos_cuenta (nombre, descripcion) VALUES
('AHORROS', 'Cuenta de ahorros'),
('CORRIENTE', 'Cuenta corriente'),
('PLAZO_FIJO', 'Depósito a plazo fijo');

INSERT INTO monedas (codigo, nombre, simbolo) VALUES
('USD', 'Dólar estadounidense', '$'),
('EUR', 'Euro', '€');

-- ============================================================
-- Clientes de prueba
-- ============================================================

INSERT INTO clientes (tipo_persona_id, numero_identificacion, nombres, apellidos, fecha_nacimiento, nacionalidad, direccion, telefono, email)
VALUES
(1, '0102030405', 'Abel', 'Gomez', '1992-05-14', 'Ecuatoriana', 'Av. Amazonas N30-200', '+593987654321', 'abel@fincore.local'),
(1, '0203040506', 'María', 'López', '1988-11-22', 'Ecuatoriana', 'Calle Loyola 123', '+593987654322', 'maria@fincore.local'),
(1, '0304050607', 'Carlos', 'Ruiz', '1995-01-10', 'Ecuatoriana', 'Calle Almagro 456', '+593987654323', 'carlos@fincore.local'),
(1, '0405060708', 'Ana', 'Martínez', '1990-07-30', 'Ecuatoriana', 'Av. de la Prensa 789', '+593987654324', 'ana@fincore.local');

-- ============================================================
-- Cuentas
-- ============================================================

INSERT INTO cuentas (cliente_id, tipo_cuenta_id, moneda_id, numero_cuenta, saldo_contable, saldo_disponible, saldo_retenido, saldo_proyectado)
VALUES
(1, 1, 1, '2200100001001', 1000.00, 800.00, 200.00, 1000.00),
(2, 1, 1, '2200100001002', 500.00, 500.00, 0.00, 500.00),
(3, 2, 1, '2200200001003', 2500.00, 2500.00, 0.00, 2500.00),
(4, 1, 1, '2200100001004', 150.00, 150.00, 0.00, 150.00);

-- ============================================================
-- Plan de cuentas
-- ============================================================

INSERT INTO plan_cuentas (codigo, nombre, naturaleza, nivel, cuenta_padre_id)
VALUES
('1', 'ACTIVOS', 'DEUDOR', 1, NULL),
('1001', 'Caja y efectivo', 'DEUDOR', 2, 1),
('1010', 'Depósitos en Banco Central', 'DEUDOR', 2, 1),
('1100', 'Cartera de créditos', 'DEUDOR', 2, 1),
('2', 'PASIVOS', 'ACREEDOR', 1, NULL),
('2001', 'Depósitos en cuenta corriente', 'ACREEDOR', 2, 2),
('2002', 'Depósitos en cuenta ahorros', 'ACREEDOR', 2, 2),
('2100', 'Fondos en tránsito', 'ACREEDOR', 2, 2),
('2200', 'Retenciones', 'ACREEDOR', 2, 2),
('3', 'PATRIMONIO', 'ACREEDOR', 1, NULL),
('3001', 'Capital social', 'ACREEDOR', 2, 3),
('4', 'INGRESOS', 'ACREEDOR', 1, NULL),
('4001', 'Intereses ganados', 'ACREEDOR', 2, 4),
('4010', 'Comisiones por servicios', 'ACREEDOR', 2, 4),
('5', 'GASTOS', 'DEUDOR', 1, NULL),
('5001', 'Intereses pagados en depósitos', 'DEUDOR', 2, 5);

-- ============================================================
-- Usuarios backoffice
-- ============================================================

INSERT INTO roles (nombre, descripcion) VALUES
('CLIENTE', 'Cliente final'),
('OPERADOR', 'Operador de backoffice'),
('SUPERVISOR', 'Supervisor de operaciones'),
('AUDITOR', 'Auditor'),
('FRAUDE', 'Analista de fraude'),
('ADMIN', 'Administrador');

INSERT INTO usuarios_sistema (username, password_hash, nombres, apellidos, email)
VALUES
('abel', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Abel', 'Gomez', 'abel@fincore.local'),
('maria', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'María', 'López', 'maria@fincore.local'),
('supervisor', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Supervisor', 'Backoffice', 'supervisor@fincore.local'),
('auditor', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Auditor', 'Backoffice', 'auditor@fincore.local');

INSERT INTO usuarios_roles (usuario_id, rol_id) VALUES
(1, 1),
(2, 1),
(3, 3),
(4, 4);

-- ============================================================
-- Reglas antifraude base
-- ============================================================

INSERT INTO reglas_antifraude (codigo, nombre, descripcion, puntaje_riesgo, activa, parametros)
VALUES
('MONTO_INUSUAL', 'Monto inusual', 'Monto mayor a 3 veces el promedio historico', 15, TRUE, '{"multiplicador": 3}'),
('HORARIO_INUSUAL', 'Horario inusual', 'Transaccion entre 00:00 y 05:00', 10, TRUE, '{"inicio": "00:00", "fin": "05:00"}'),
('DISPOSITIVO_NUEVO', 'Dispositivo nuevo', 'Primer uso del dispositivo', 20, TRUE, '{}'),
('BENEFICIARIO_NUEVO', 'Beneficiario nuevo', 'Primer envio a esta cuenta', 15, TRUE, '{}'),
('PAIS_DIFERENTE', 'Pais diferente', 'IP de pais diferente al habitual', 25, TRUE, '{}'),
('VELOCIDAD_ALTA', 'Velocidad alta', 'Mas de 5 transacciones en 10 minutos', 30, TRUE, '{"limite": 5, "ventana_minutos": 10}'),
('LISTA_NEGRA', 'Lista negra', 'Cuenta o persona en lista restrictiva', 100, TRUE, '{}'),
('IP_SOSPECHOSA', 'IP sospechosa', 'IP reportada como maliciosa', 40, TRUE, '{}'),
('PATRON_FRACCIONADO', 'Patron fraccionado', 'Microtransacciones repetidas', 35, TRUE, '{"limite_micro": 10}'),
('PRIMER_TRANSFER_GRANDE', 'Primer transferencia grande', 'Primera vez y monto alto', 20, TRUE, '{"monto_minimo": 1000}');
