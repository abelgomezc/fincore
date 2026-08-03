CREATE TABLE reglas_fraude (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    puntos INTEGER NOT NULL,
    parametros JSONB,
    es_activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP DEFAULT NOW()
);

CREATE TABLE perfil_transaccional (
    id BIGSERIAL PRIMARY KEY,
    id_cliente BIGINT NOT NULL UNIQUE,
    promedio_monto_30d DECIMAL(18,2),
    maximo_monto_30d DECIMAL(18,2),
    total_transferencias_30d INTEGER,
    paises_habituales JSONB,
    dispositivos_habituales JSONB,
    horarios_habituales JSONB,
    fecha_actualizacion TIMESTAMP DEFAULT NOW()
);

CREATE TABLE lista_negra (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL,
    valor VARCHAR(255) NOT NULL,
    motivo VARCHAR(255),
    es_activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_lista_negra_tipo_valor ON lista_negra(tipo, valor);

CREATE TABLE evaluaciones_fraude (
    id BIGSERIAL PRIMARY KEY,
    id_transferencia BIGINT NOT NULL,
    id_cliente BIGINT NOT NULL,
    score_total INTEGER NOT NULL,
    decision VARCHAR(20) NOT NULL,
    reglas_activadas JSONB NOT NULL,
    ip_origen VARCHAR(45),
    dispositivo VARCHAR(255),
    tiempo_evaluacion_ms INTEGER,
    revisado_por VARCHAR(100),
    fecha_revision TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT NOW()
);

INSERT INTO reglas_fraude (codigo, nombre, puntos) VALUES
('MONTO_INUSUAL', 'Monto inusual', 15),
('HORARIO_INUSUAL', 'Horario inusual', 10),
('DISPOSITIVO_NUEVO', 'Dispositivo nuevo', 20),
('BENEFICIARIO_NUEVO', 'Beneficiario nuevo', 15),
('PAIS_DIFERENTE', 'País diferente', 25),
('VELOCIDAD_ALTA', 'Velocidad alta', 30),
('LISTA_NEGRA', 'Lista negra', 100),
('IP_SOSPECHOSA', 'IP sospechosa', 40),
('PATRON_FRACCIONADO', 'Patrón fraccionado', 35),
('PRIMER_TRANSFER_GRANDE', 'Primer transfer grande', 20);
