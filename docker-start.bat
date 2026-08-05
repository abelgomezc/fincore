@echo off
setlocal

echo ========================================
echo   FinCore - Docker Infrastructure
echo ========================================
echo.
echo Starting containers for FinCore project only...
echo.

REM Verificar que Docker Desktop esté corriendo
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker Desktop is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

REM Levantar servicios
docker-compose up -d

echo.
echo ========================================
echo   FinCore containers started!
echo ========================================
echo.
echo Services:
echo   - PostgreSQL:   localhost:5432
echo   - Redis:        localhost:6379
echo   - Zookeeper:    localhost:2181
echo   - Kafka:        localhost:9092
echo   - Kafka UI:     http://localhost:8080
echo.
echo Check status: docker-compose ps
echo View logs:    docker-compose logs -f
echo.
echo To stop:      docker-compose down
echo.

pause
