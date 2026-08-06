-- ===================================================================
-- FinCore Ledger Service — Plan de cuentas inicial
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- V2__plan_cuentas_inicial.sql
-- ===================================================================

-- ACTIVOS (1xxx) — DEUDORAS
INSERT INTO plan_cuentas (codigo, nombre, tipo, naturaleza, nivel, codigo_padre, es_hoja, es_activa, fecha_creacion, creado_por) VALUES
('1000', 'Activo',                    'ACTIVO',     'DEUDORA',   1, NULL,    FALSE, TRUE, NOW(), 'system'),
('1001', 'Caja y efectivo',           'ACTIVO',     'DEUDORA',   2, '1000',  TRUE,  TRUE, NOW(), 'system'),
('1010', 'Depositos en Banco Central', 'ACTIVO',    'DEUDORA',   2, '1000',  TRUE,  TRUE, NOW(), 'system'),
('1020', 'Inversiones',               'ACTIVO',     'DEUDORA',   2, '1000',  TRUE,  TRUE, NOW(), 'system'),
('1100', 'Cartera de creditos',       'ACTIVO',     'DEUDORA',   2, '1000',  TRUE,  TRUE, NOW(), 'system'),
('1200', 'Cuentas por cobrar',        'ACTIVO',     'DEUDORA',   2, '1000',  TRUE,  TRUE, NOW(), 'system');

-- PASIVOS (2xxx) — ACREEDORAS
INSERT INTO plan_cuentas (codigo, nombre, tipo, naturaleza, nivel, codigo_padre, es_hoja, es_activa, fecha_creacion, creado_por) VALUES
('2000', 'Pasivo',                    'PASIVO',     'ACREEDORA', 1, NULL,    FALSE, TRUE, NOW(), 'system'),
('2001', 'Depositos en cuenta corriente', 'PASIVO',  'ACREEDORA', 2, '2000',  TRUE,  TRUE, NOW(), 'system'),
('2002', 'Depositos en cuenta ahorros',   'PASIVO',  'ACREEDORA', 2, '2000',  TRUE,  TRUE, NOW(), 'system'),
('2010', 'Depositos a plazo fijo',        'PASIVO',  'ACREEDORA', 2, '2000',  TRUE,  TRUE, NOW(), 'system'),
('2100', 'Fondos en transito',            'PASIVO',  'ACREEDORA', 2, '2000',  TRUE,  TRUE, NOW(), 'system'),
('2200', 'Retenciones',                   'PASIVO',  'ACREEDORA', 2, '2000',  TRUE,  TRUE, NOW(), 'system');

-- PATRIMONIO (3xxx) — ACREEDORAS
INSERT INTO plan_cuentas (codigo, nombre, tipo, naturaleza, nivel, codigo_padre, es_hoja, es_activa, fecha_creacion, creado_por) VALUES
('3000', 'Patrimonio',                'PATRIMONIO', 'ACREEDORA', 1, NULL,    FALSE, TRUE, NOW(), 'system'),
('3001', 'Capital social',            'PATRIMONIO', 'ACREEDORA', 2, '3000',  TRUE,  TRUE, NOW(), 'system'),
('3100', 'Reservas',                  'PATRIMONIO', 'ACREEDORA', 2, '3000',  TRUE,  TRUE, NOW(), 'system');

-- INGRESOS (4xxx) — DEUDORAS
INSERT INTO plan_cuentas (codigo, nombre, tipo, naturaleza, nivel, codigo_padre, es_hoja, es_activa, fecha_creacion, creado_por) VALUES
('4000', 'Ingresos',                  'INGRESO',    'DEUDORA',  1, NULL,    FALSE, TRUE, NOW(), 'system'),
('4001', 'Intereses ganados en creditos', 'INGRESO', 'DEUDORA', 2, '4000',  TRUE,  TRUE, NOW(), 'system'),
('4010', 'Comisiones por servicios',        'INGRESO', 'DEUDORA', 2, '4000',  TRUE,  TRUE, NOW(), 'system'),
('4020', 'Comisiones por transferencias',   'INGRESO', 'DEUDORA', 2, '4000',  TRUE,  TRUE, NOW(), 'system');

-- GASTOS (5xxx) — ACREEDORAS
INSERT INTO plan_cuentas (codigo, nombre, tipo, naturaleza, nivel, codigo_padre, es_hoja, es_activa, fecha_creacion, creado_por) VALUES
('5000', 'Gastos',                    'GASTO',      'ACREEDORA', 1, NULL,    FALSE, TRUE, NOW(), 'system'),
('5001', 'Intereses pagados en depositos', 'GASTO',   'ACREEDORA', 2, '5000',  TRUE,  TRUE, NOW(), 'system'),
('5010', 'Gastos operativos',          'GASTO',      'ACREEDORA', 2, '5000',  TRUE,  TRUE, NOW(), 'system');
