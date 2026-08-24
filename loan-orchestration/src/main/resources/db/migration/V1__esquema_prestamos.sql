CREATE TABLE solicitudes_prestamo (
    id SERIAL PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    numero_solicitud VARCHAR(50) NOT NULL,
    tipo_prestamo VARCHAR(50) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    monto_solicitado NUMERIC(15,2) NOT NULL,
    plazo_meses INTEGER NOT NULL,
    tasa_interes_anual NUMERIC(5,2),
    monto_aprobado NUMERIC(15,2),
    score_buro INTEGER,
    score_riesgo INTEGER,
    evaluacion_riesgo VARCHAR(30),
    motivo_rechazo TEXT,
    id_documento_contrato BIGINT,
    id_documento_pagare BIGINT,
    fecha_solicitud TIMESTAMP NOT NULL,
    fecha_aprobacion TIMESTAMP,
    fecha_rechazo TIMESTAMP,
    fecha_desembolso TIMESTAMP,
    creado_por VARCHAR(100),
    actualizado_por VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE evaluaciones_riesgo (
    id SERIAL PRIMARY KEY,
    id_solicitud BIGINT NOT NULL,
    estado VARCHAR(30) NOT NULL,
    score_buro INTEGER,
    score_interno INTEGER,
    score_final INTEGER,
    monto_aprobado NUMERIC(15,2),
    tasa_interes_aprobada NUMERIC(5,2),
    plazo_aprobado_meses INTEGER,
    detalle TEXT,
    reglas_aplicadas TEXT,
    fecha_evaluacion TIMESTAMP,
    evaluado_por VARCHAR(100),
    creado_por VARCHAR(100),
    actualizado_por VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE historial_solicitudes_prestamo (
    id SERIAL PRIMARY KEY,
    id_solicitud BIGINT NOT NULL,
    estado_anterior VARCHAR(50),
    estado_nuevo VARCHAR(50) NOT NULL,
    motivo TEXT,
    fecha_cambio TIMESTAMP NOT NULL,
    creado_por VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_solicitudes_prestamo_numero ON solicitudes_prestamo(numero_solicitud);
CREATE INDEX IF NOT EXISTS idx_solicitudes_prestamo_cliente ON solicitudes_prestamo(id_cliente);
CREATE INDEX IF NOT EXISTS idx_solicitudes_prestamo_estado ON solicitudes_prestamo(estado);
CREATE INDEX IF NOT EXISTS idx_solicitudes_prestamo_fecha ON solicitudes_prestamo(fecha_solicitud);
CREATE INDEX IF NOT EXISTS idx_evaluaciones_riesgo_solicitud ON evaluaciones_riesgo(id_solicitud);
CREATE INDEX IF NOT EXISTS idx_historial_solicitudes_solicitud ON historial_solicitudes_prestamo(id_solicitud);
