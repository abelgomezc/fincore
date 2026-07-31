-- ===================================================================
-- FinCore — Script de inicialización de bases de datos
-- © 2026 Abel Gomez. Todos los derechos reservados.
--
-- Ejecutar como superusuario de PostgreSQL antes de iniciar cualquier
-- microservicio. Crea las 9 bases de datos que componen el ecosistema
-- FinCore y otorga permisos al usuario de aplicación.
-- ===================================================================

-- Crear usuario de aplicación si no existe
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT FROM pg_catalog.pg_roles WHERE rolname = 'fincore_user'
    ) THEN
        CREATE ROLE fincore_user WITH LOGIN PASSWORD 'fincore_pass_2026!';
    END IF;
END
$$;

-- ===================================================================
-- Bases de datos principales
-- Cada microservicio tiene su propia base de datos para aislamiento
-- de datos y cumplimiento de principios de microservicios.
-- ===================================================================

CREATE DATABASE fincore_auth       OWNER fincore_user TEMPLATE template0;
CREATE DATABASE fincore_customers  OWNER fincore_user TEMPLATE template0;
CREATE DATABASE fincore_accounts   OWNER fincore_user TEMPLATE template0;
CREATE DATABASE fincore_ledger     OWNER fincore_user TEMPLATE template0;
CREATE DATABASE fincore_transfers  OWNER fincore_user TEMPLATE template0;
CREATE DATABASE fincore_fraud      OWNER fincore_user TEMPLATE template0;
CREATE DATABASE fincore_audit      OWNER fincore_user TEMPLATE template0;
CREATE DATABASE fincore_batch      OWNER fincore_user TEMPLATE template0;
CREATE DATABASE fincore_backoffice OWNER fincore_user TEMPLATE template0;

-- Otorgar permisos sobre todas las bases de datos
GRANT ALL PRIVILEGES ON DATABASE fincore_auth       TO fincore_user;
GRANT ALL PRIVILEGES ON DATABASE fincore_customers  TO fincore_user;
GRANT ALL PRIVILEGES ON DATABASE fincore_accounts   TO fincore_user;
GRANT ALL PRIVILEGES ON DATABASE fincore_ledger     TO fincore_user;
GRANT ALL PRIVILEGES ON DATABASE fincore_transfers  TO fincore_user;
GRANT ALL PRIVILEGES ON DATABASE fincore_fraud      TO fincore_user;
GRANT ALL PRIVILEGES ON DATABASE fincore_audit      TO fincore_user;
GRANT ALL PRIVILEGES ON DATABASE fincore_batch      TO fincore_user;
GRANT ALL PRIVILEGES ON DATABASE fincore_backoffice TO fincore_user;

-- ===================================================================
-- Extensiones necesarias
-- ===================================================================

-- Conectar a cada base de datos y crear extensiones
-- (ejecutar manualmente o usar un script que conecte a cada una)

-- Para fincore_ledger: extensión para UUIDs (si se necesita)
-- Para fincore_fraud: extensión para JSONB (incluida por defecto en PG16)

-- ===================================================================
-- Verificación
-- ===================================================================

SELECT datname, pg_size_pretty(pg_database_size(datname)) AS tamano
FROM pg_database
WHERE datname LIKE 'fincore_%'
ORDER BY datname;
