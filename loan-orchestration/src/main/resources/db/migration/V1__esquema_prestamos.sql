-- ============================================
-- Loan Orchestration - Esquema inicial
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- ============================================

CREATE TABLE IF NOT EXISTS solicitudes_prestamo (
    bigserial PRIMARY KEY,
    bigint id_cliente NOT NULL,
    varchar(50) numero_solicitud UNIQUE NOT NULL,
    varchar(50) tipo_prestamo NOT NULL,
    varchar(50) estado NOT NULL,
    numeric(15,2) monto_solicitado NOT NULL,
    integer plazo_meses NOT NULL,
    numeric(5,2) tasa_interes_anual,
    numeric(15,2) monto_aprobado,
    integer score_buro,
    integer score_riesgo,
    varchar(30) evaluacion_riesgo,
    text motivo_rechazo,
    bigint id_documento_contrato,
    bigint id_documento_pagare,
    timestamp fecha_solicitud NOT NULL,
    timestamp fecha_aprobacion,
    timestamp fecha_rechazo,
    timestamp fecha_desembolso,
    varchar(100) creado_por,
    varchar(100) actualizado_por,
    bigint version NOT NULL DEFAULT 0,
    timestamp fecha_creacion DEFAULT CURRENT_TIMESTAMP,
    timestamp fecha_actualizacion DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS evaluaciones_riesgo (
    bigserial PRIMARY KEY,
    bigint id_solicitud NOT NULL,
    varchar(30) estado NOT NULL,
    integer score_buro,
    integer score_interno,
    integer score_final,
    numeric(15,2) monto_aprobado,
    numeric(5,2) tasa_interes_aprobada,
    integer plazo_aprobado_meses,
    text detalle,
    text reglas_aplicadas,
    timestamp fecha_evaluacion,
    varchar(100) evaluado_por,
    varchar(100) creado_por,
    varchar(100) actualizado_por,
    bigint version NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS historial_solicitudes_prestamo (
    bigserial PRIMARY KEY,
    bigint id_solicitud NOT NULL,
    varchar(50) estado_anterior,
    varchar(50) estado_nuevo NOT NULL,
    text motivo,
    timestamp fecha_cambio NOT NULL,
    varchar(100) creado_por,
    bigint version NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_solicitudes_prestamo_cliente ON solicitudes_prestamo(id_cliente);
CREATE INDEX IF NOT EXISTS idx_solicitudes_prestamo_estado ON solicitudes_prestamo(estado);
CREATE INDEX IF NOT EXISTS idx_solicitudes_prestamo_fecha ON solicitudes_prestamo(fecha_solicitud);
CREATE INDEX IF NOT EXISTS idx_evaluaciones_riesgo_solicitud ON evaluaciones_riesgo(id_solicitud);
CREATE INDEX IF NOT EXISTS idx_historial_solicitudes_solicitud ON historial_solicitudes_prestamo(id_solicitud);
