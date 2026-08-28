-- ============================================
-- Datos demo para FinCore
-- ============================================

-- CLIENTE DEMO
INSERT INTO clientes (id_cliente, nombre, apellido, dni, email, telefono, estado, fecha_creacion)
VALUES (1, 'Juan', 'Perez', '12345678', 'juan.perez@example.com', '555-0100', 'ACTIVO', CURRENT_TIMESTAMP)
ON CONFLICT (id_cliente) DO NOTHING;

-- CUENTA DEMO
INSERT INTO cuentas (id_cuenta, id_cliente, numero_cuenta, tipo_cuenta, moneda, saldo, estado, fecha_creacion)
VALUES (1, 1, 'ACC-1001', 'AHORRO', 'ARS', 50000.00, 'ACTIVA', CURRENT_TIMESTAMP)
ON CONFLICT (id_cuenta) DO NOTHING;

INSERT INTO cuentas (id_cuenta, id_cliente, numero_cuenta, tipo_cuenta, moneda, saldo, estado, fecha_creacion)
VALUES (2, 1, 'ACC-1002', 'CORRIENTE', 'ARS', 12000.00, 'ACTIVA', CURRENT_TIMESTAMP)
ON CONFLICT (id_cuenta) DO NOTHING;

-- USUARIO BACKOFFICE DEMO
INSERT INTO usuarios_sistema (id, email, password_hash, primer_nombre, primer_apellido, rol, estado, fecha_creacion)
VALUES (1, 'admin@fincore.local', '$2a$10$N9qo8uLOickgx2ZMRZo5HeNV0QFq8q3W6p2o5eK5vK5eK5vK5vK5eK', 'Admin', 'FinCore', 'ADMIN', 'ACTIVO', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- SOLICITUD DE PRESTAMO DEMO
INSERT INTO solicitudes_prestamo (id_cliente, numero_solicitud, tipo_prestamo, estado, monto_solicitado, plazo_meses, tasa_interes_anual, fecha_solicitud)
VALUES (1, 'SOL-2026-001', 'PERSONAL', 'ENVIADA', 10000.00, 24, 18.00, CURRENT_TIMESTAMP)
ON CONFLICT (numero_solicitud) DO NOTHING;
