-- ============================================================
-- FINCORE — Schema inicial
-- Tablas en ESPAÑOL snake_case, IDs BIGSERIAL
-- ============================================================

-- ============================================================
-- Catálogos base
-- ============================================================

CREATE TABLE IF NOT EXISTS tipos_cuenta (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tipos_persona (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS monedas (
    id BIGSERIAL PRIMARY KEY,
    codigo CHAR(3) NOT NULL UNIQUE,
    nombre VARCHAR(50) NOT NULL,
    simbolo VARCHAR(10),
    activo BOOLEAN DEFAULT TRUE
);

-- ============================================================
-- Clientes
-- ============================================================

CREATE TABLE IF NOT EXISTS clientes (
    id BIGSERIAL PRIMARY KEY,
    tipo_persona_id BIGINT NOT NULL REFERENCES tipos_persona(id),
    numero_identificacion VARCHAR(50) NOT NULL UNIQUE,
    nombres VARCHAR(200),
    apellidos VARCHAR(200),
    razon_social VARCHAR(300),
    fecha_nacimiento DATE,
    nacionalidad VARCHAR(100),
    direccion TEXT,
    telefono VARCHAR(50),
    email VARCHAR(200),
    estado VARCHAR(50) NOT NULL DEFAULT 'ACTIVO',
    version INTEGER NOT NULL DEFAULT 0,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS clientes_documentos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    tipo_documento VARCHAR(50) NOT NULL,
    numero VARCHAR(100) NOT NULL,
    fecha_emision DATE,
    fecha_expiracion DATE,
    archivo_url TEXT,
    estado VARCHAR(50) NOT NULL DEFAULT 'VIGENTE',
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS listas_restrictivas (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    fuente VARCHAR(100) NOT NULL,
    descripcion TEXT,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Cuentas
-- ============================================================

CREATE TABLE IF NOT EXISTS cuentas (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    tipo_cuenta_id BIGINT NOT NULL REFERENCES tipos_cuenta(id),
    moneda_id BIGINT NOT NULL REFERENCES monedas(id),
    numero_cuenta VARCHAR(50) NOT NULL UNIQUE,
    saldo_contable NUMERIC(18,2) NOT NULL DEFAULT 0,
    saldo_disponible NUMERIC(18,2) NOT NULL DEFAULT 0,
    saldo_retenido NUMERIC(18,2) NOT NULL DEFAULT 0,
    saldo_proyectado NUMERIC(18,2) NOT NULL DEFAULT 0,
    estado VARCHAR(50) NOT NULL DEFAULT 'ACTIVA',
    motivo_cierre TEXT,
    fecha_apertura DATE NOT NULL DEFAULT CURRENT_DATE,
    fecha_cierre DATE,
    version INTEGER NOT NULL DEFAULT 0,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS movimientos (
    id BIGSERIAL PRIMARY KEY,
    cuenta_id BIGINT NOT NULL REFERENCES cuentas(id),
    tipo_movimiento VARCHAR(50) NOT NULL,
    monto NUMERIC(18,2) NOT NULL,
    saldo_resultante NUMERIC(18,2) NOT NULL,
    referencia TEXT,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Ledger / Contabilidad
-- ============================================================

CREATE TABLE IF NOT EXISTS plan_cuentas (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(200) NOT NULL,
    naturaleza VARCHAR(20) NOT NULL,
    nivel INTEGER NOT NULL,
    cuenta_padre_id BIGINT REFERENCES plan_cuentas(id),
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS asientos_contables (
    id BIGSERIAL PRIMARY KEY,
    numero_asiento VARCHAR(50) NOT NULL UNIQUE,
    fecha DATE NOT NULL DEFAULT CURRENT_DATE,
    descripcion TEXT,
    tipo_asiento VARCHAR(50) NOT NULL,
    origen_tipo VARCHAR(100),
    origen_id BIGINT,
    total_debitos NUMERIC(18,2) NOT NULL,
    total_creditos NUMERIC(18,2) NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'BALANCEADO',
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS lineas_asiento (
    id BIGSERIAL PRIMARY KEY,
    asiento_id BIGINT NOT NULL REFERENCES asientos_contables(id),
    cuenta_id BIGINT NOT NULL REFERENCES plan_cuentas(id),
    tipo_movimiento VARCHAR(20) NOT NULL,
    monto NUMERIC(18,2) NOT NULL,
    descripcion TEXT,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Transferencias
-- ============================================================

CREATE TABLE IF NOT EXISTS transferencias (
    id BIGSERIAL PRIMARY KEY,
    numero_transferencia VARCHAR(50) NOT NULL UNIQUE,
    cuenta_origen_id BIGINT NOT NULL REFERENCES cuentas(id),
    cuenta_destino_id BIGINT NOT NULL REFERENCES cuentas(id),
    monto NUMERIC(18,2) NOT NULL,
    moneda_id BIGINT NOT NULL REFERENCES monedas(id),
    concepto TEXT,
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    referencia_externa VARCHAR(100),
    metadata JSONB,
    version INTEGER NOT NULL DEFAULT 0,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS historial_estados_transferencia (
    id BIGSERIAL PRIMARY KEY,
    transferencia_id BIGINT NOT NULL REFERENCES transferencias(id),
    estado_anterior VARCHAR(50),
    estado_nuevo VARCHAR(50) NOT NULL,
    motivo TEXT,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Antifraude
-- ============================================================

CREATE TABLE IF NOT EXISTS reglas_antifraude (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    puntaje_riesgo INTEGER NOT NULL,
    activa BOOLEAN DEFAULT TRUE,
    parametros JSONB,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS evaluaciones_fraude (
    id BIGSERIAL PRIMARY KEY,
    transferencia_id BIGINT NOT NULL REFERENCES transferencias(id),
    regla_id BIGINT REFERENCES reglas_antifraude(id),
    puntaje_obtenido INTEGER NOT NULL,
    decision VARCHAR(50) NOT NULL,
    detalle JSONB,
    evaluado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Auditoría
-- ============================================================

CREATE TABLE IF NOT EXISTS auditoria (
    id BIGSERIAL PRIMARY KEY,
    entidad_tipo VARCHAR(100) NOT NULL,
    entidad_id BIGINT NOT NULL,
    accion VARCHAR(50) NOT NULL,
    estado_anterior JSONB,
    estado_nuevo JSONB,
    usuario_tipo VARCHAR(50),
    usuario_id BIGINT,
    ip_address VARCHAR(45),
    trace_id VARCHAR(100),
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Usuarios y roles (Backoffice)
-- ============================================================

CREATE TABLE IF NOT EXISTS usuarios_sistema (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    nombres VARCHAR(200),
    apellidos VARCHAR(200),
    email VARCHAR(200),
    activo BOOLEAN DEFAULT TRUE,
    ultimo_acceso TIMESTAMP,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS usuarios_roles (
    usuario_id BIGINT NOT NULL REFERENCES usuarios_sistema(id),
    rol_id BIGINT NOT NULL REFERENCES roles(id),
    PRIMARY KEY (usuario_id, rol_id)
);

-- ============================================================
-- Productos (placeholders)
-- ============================================================

CREATE TABLE IF NOT EXISTS tarjetas (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    numero_tarjeta VARCHAR(20) NOT NULL UNIQUE,
    tipo VARCHAR(50) NOT NULL,
    limite NUMERIC(18,2) NOT NULL DEFAULT 0,
    saldo_utilizado NUMERIC(18,2) NOT NULL DEFAULT 0,
    estado VARCHAR(50) NOT NULL DEFAULT 'ACTIVA',
    fecha_corte DATE,
    fecha_pago DATE,
    version INTEGER NOT NULL DEFAULT 0,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS prestamos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    numero_prestamo VARCHAR(50) NOT NULL UNIQUE,
    monto_principal NUMERIC(18,2) NOT NULL,
    tasa_interes NUMERIC(8,4) NOT NULL,
    plazo_meses INTEGER NOT NULL,
    saldo_pendiente NUMERIC(18,2) NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'DESEMBOLSADO',
    version INTEGER NOT NULL DEFAULT 0,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pagos_servicios (
    id BIGSERIAL PRIMARY KEY,
    cuenta_id BIGINT NOT NULL REFERENCES cuentas(id),
    servicio VARCHAR(100) NOT NULL,
    identificador VARCHAR(100) NOT NULL,
    monto NUMERIC(18,2) NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    referencia_externa VARCHAR(100),
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Índices básicos
-- ============================================================

CREATE INDEX idx_clientes_numero_identificacion ON clientes(numero_identificacion);
CREATE INDEX idx_cuentas_cliente_id ON cuentas(cliente_id);
CREATE INDEX idx_cuentas_numero_cuenta ON cuentas(numero_cuenta);
CREATE INDEX idx_transferencias_cuenta_origen ON transferencias(cuenta_origen_id);
CREATE INDEX idx_transferencias_cuenta_destino ON transferencias(cuenta_destino_id);
CREATE INDEX idx_transferencias_estado ON transferencias(estado);
CREATE INDEX idx_asientos_contables_numero ON asientos_contables(numero_asiento);
CREATE INDEX idx_lineas_asiento_asiento_id ON lineas_asiento(asiento_id);
CREATE INDEX idx_auditoria_entidad ON auditoria(entidad_tipo, entidad_id);
CREATE INDEX idx_evaluaciones_fraude_transferencia ON evaluaciones_fraude(transferencia_id);
CREATE INDEX idx_historial_estados_transferencia_transferencia ON historial_estados_transferencia(transferencia_id);
