-- ===================================================================
-- FinCore Auth Service — Datos iniciales
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- V2__datos_iniciales.sql
-- ===================================================================

-- Roles
INSERT INTO roles (nombre, descripcion) VALUES
('CLIENTE', 'Usuario cliente del banco'),
('OPERADOR', 'Operador de backoffice'),
('SUPERVISOR', 'Supervisor que puede aprobar/rechazar operaciones'),
('AUDITOR', 'Auditor que puede consultar registros'),
('ADMIN', 'Administrador del sistema');

-- Permisos
INSERT INTO permisos (nombre, descripcion) VALUES
('transferencia.realizar', 'Puede realizar transferencias'),
('transferencia.revisar', 'Puede revisar transferencias en espera'),
('transferencia.revertir', 'Puede revertir transferencias completadas'),
('cuenta.bloquear', 'Puede bloquear cuentas'),
('cuenta.crear', 'Puede crear cuentas'),
('fraude.listar_negra', 'Puede gestionar lista negra'),
('auditoria.consultar', 'Puede consultar auditoría'),
('backoffice.acceder', 'Acceso al portal de backoffice'),
('admin.configurar', 'Puede configurar el sistema');

-- Asignación de permisos a roles
-- CLIENTE
INSERT INTO rol_permisos (id_rol, id_permiso) VALUES
(1, 1); -- transferencia.realizar

-- OPERADOR
INSERT INTO rol_permisos (id_rol, id_permiso) VALUES
(2, 1), (2, 6), (2, 8); -- realizar, lista negra, backoffice

-- SUPERVISOR
INSERT INTO rol_permisos (id_rol, id_permiso) VALUES
(3, 2), (3, 3), (3, 4), (3, 5), (3, 6), (3, 8); -- revisar, revertir, bloquear, crear, lista negra, backoffice

-- AUDITOR
INSERT INTO rol_permisos (id_rol, id_permiso) VALUES
(4, 7), (4, 8); -- auditoria, backoffice

-- ADMIN
INSERT INTO rol_permisos (id_rol, id_permiso) VALUES
(5, 1), (5, 2), (5, 3), (5, 4), (5, 5), (5, 6), (5, 7), (5, 8), (5, 9); -- todos

-- ===================================================================
-- USUARIOS INICIALES
-- ===================================================================
-- Nota: Los hashes de contraseña son BCrypt.
-- Para pruebas, usar password: "password123"
-- Abel Gomez — Cliente
INSERT INTO usuarios (email, password_hash, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, rol, estado, id_cliente, fecha_creacion, fecha_actualizacion)
VALUES ('abel.gomez@fincore.com', '$2a$10$J6QU3iYz0Y5nQ8wR7sT2e.5K8mN0pQ3rS6tU9vW2xY5zA8bC1dE4fG7hJ0k', 'Abel', 'Alejandro', 'Gomez', 'Salazar', 'CLIENTE', 'ACTIVO', 1, '2024-01-15 10:00:00', '2024-01-15 10:00:00');

-- María López — Cliente
INSERT INTO usuarios (email, password_hash, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, rol, estado, id_cliente, fecha_creacion, fecha_actualizacion)
VALUES ('maria.lopez@fincore.com', '$2a$10$K7RT4jZa1Y6oP9wQ8sT3u.6L9nO1pQ4rS7tU0vW3xY6zA9bC2dE5fG8hJ1k', 'María', 'Fernanda', 'López', 'García', 'CLIENTE', 'ACTIVO', 2, '2024-01-15 10:00:00', '2024-01-15 10:00:00');

-- Supervisor
INSERT INTO usuarios (email, password_hash, primer_nombre, primer_apellido, rol, estado, fecha_creacion, fecha_actualizacion)
VALUES ('supervisor@fincore.com', '$2a$10$M8ST5kAb2Z7pQ0wR9sT4v.7M0oP2qR5sT8uV1wX4yZ7aB0cD3eF6gH9jK2l', 'Supervisor', 'FinCore', 'SUPERVISOR', 'ACTIVO', '2024-01-15 10:00:00', '2024-01-15 10:00:00');

-- Auditor
INSERT INTO usuarios (email, password_hash, primer_nombre, primer_apellido, rol, estado, fecha_creacion, fecha_actualizacion)
VALUES ('auditor@fincore.com', '$2a$10$N9TU6lBc3A8qR1wS0tU5w.8N1pQ3rS6tU9vW2xY5zA8bC1dE4fG7hJ0kM3n', 'Auditor', 'FinCore', 'AUDITOR', 'ACTIVO', '2024-01-15 10:00:00', '2024-01-15 10:00:00');

-- Admin
INSERT INTO usuarios (email, password_hash, primer_nombre, primer_apellido, rol, estado, fecha_creacion, fecha_actualizacion)
VALUES ('admin@fincore.com', '$2a$10$O0UV7mCd4B9sR2wT1uV6x.9O2pQ4rS7tU0vW3xY6zA9bC2dE5fG8hJ1kM4n', 'Admin', 'FinCore', 'ADMIN', 'ACTIVO', '2024-01-15 10:00:00', '2024-01-15 10:00:00');

-- Credenciales de prueba:
-- Abel Gomez / abel.gomez@fincore.com / password123
-- María López / maria.lopez@fincore.com / password123
-- Supervisor / supervisor@fincore.com / password123
-- Auditor / auditor@fincore.com / password123
-- Admin / admin@fincore.com / password123
