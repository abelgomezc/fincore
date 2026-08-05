@echo off
setlocal enabledelayedexpansion

REM ===== FinCore Local Startup Script =====
REM Redis y Kafka deben estar corriendo en Docker antes de ejecutar este script
REM Los logs se guardan en cada directorio de microservicio: <service>\logs\

REM Crear directorios de logs
for %%S in (
    eureka-server api-gateway auth-service customer-service
    account-service ledger-service transfer-service fraud-service
    notification-service audit-service backoffice-service batch-service
) do (
    if not exist "%%S\logs" mkdir "%%S\logs"
)

set EUREKA_URL=http://localhost:8761/eureka/
set KAFKA_BOOTSTRAP_SERVERS=localhost:9092
set REDIS_HOST=localhost
set REDIS_PORT=6379
set REDIS_PASSWORD=redis_fincore_2026
set JWT_SECRET=fincore_jwt_hs512_secret_minimo_64_caracteres_2026_abel_gomez_banking
set POSTGRES_USER=fincore
set POSTGRES_PASSWORD=fincore123
set DB_AUTH=fincore_auth
set DB_CUSTOMERS=fincore_customers
set DB_ACCOUNTS=fincore_accounts
set DB_LEDGER=fincore_ledger
set DB_TRANSFERS=fincore_transfers
set DB_FRAUD=fincore_fraud
set DB_NOTIFICATION=fincore_notification
set DB_AUDIT=fincore_audit
set DB_BATCH=fincore_batch
set DB_BACKOFFICE=fincore_backoffice
set DAILY_LIMIT=5000.00
set TX_LIMIT=2000.00
set MONTHLY_LIMIT=20000.00
set SAGA_TIMEOUT=120
set SAGA_MAX_RETRIES=3
set FRONTEND_URL=http://localhost:5173
set SMTP_FROM=noreply@fincore.banking

set COMMON_ARGS=--spring.flyway.enabled=false --spring.jpa.hibernate.ddl-auto=update --grpc.security.auth.enabled=false

echo [1/12] Eureka Server (8761) iniciando...
start /b cmd /c "java -jar eureka-server/target/eureka-server-1.0.0.jar %COMMON_ARGS% --server.port=8761 > eureka-server/logs/eureka.log 2>&1"

timeout /t 25 /nobreak >nul

echo [2/12] API Gateway (8080) iniciando...
start /b cmd /c "java -jar api-gateway/target/api-gateway-1.0.0.jar %COMMON_ARGS% --server.port=8080 > api-gateway/logs/api-gateway.log 2>&1"

echo [3/12] Auth Service (8081) iniciando...
start /b cmd /c "java -jar auth-service/target/auth-service-1.0.0.jar %COMMON_ARGS% --server.port=8081 > auth-service/logs/auth-service.log 2>&1"

echo [4/12] Customer Service (8082) iniciando...
start /b cmd /c "java -jar customer-service/target/customer-service-1.0.0.jar %COMMON_ARGS% --server.port=8082 > customer-service/logs/customer-service.log 2>&1"

echo [5/12] Account Service (8083) iniciando...
start /b cmd /c "java -jar account-service/target/account-service-1.0.0.jar %COMMON_ARGS% --server.port=8083 > account-service/logs/account-service.log 2>&1"

echo [6/12] Ledger Service (8084) iniciando...
start /b cmd /c "java -jar ledger-service/target/ledger-service-1.0.0.jar %COMMON_ARGS% --server.port=8084 > ledger-service/logs/ledger-service.log 2>&1"

echo [7/12] Transfer Service (8085) iniciando...
start /b cmd /c "java -jar transfer-service/target/transfer-service-1.0.0.jar %COMMON_ARGS% --server.port=8085 > transfer-service/logs/transfer-service.log 2>&1"

echo [8/12] Fraud Service (8090) iniciando...
start /b cmd /c "java -jar fraud-service/target/fraud-service-1.0.0.jar %COMMON_ARGS% --server.port=8090 > fraud-service/logs/fraud-service.log 2>&1"

echo [9/12] Audit Service (8091) iniciando...
start /b cmd /c "java -jar audit-service/target/audit-service-1.0.0.jar %COMMON_ARGS% --server.port=8091 > audit-service/logs/audit-service.log 2>&1"

echo [10/12] Notification Service (8092) iniciando...
start /b cmd /c "java -jar notification-service/target/notification-service-1.0.0.jar %COMMON_ARGS% --server.port=8092 > notification-service/logs/notification-service.log 2>&1"

echo [11/12] Backoffice Service (8093) iniciando...
start /b cmd /c "java -jar backoffice-service/target/backoffice-service-1.0.0.jar %COMMON_ARGS% --server.port=8093 > backoffice-service/logs/backoffice-service.log 2>&1"

echo [12/12] Batch Service (8094) iniciando...
start /b cmd /c "java -jar batch-service/target/batch-service-1.0.0.jar %COMMON_ARGS% --server.port=8094 > batch-service/logs/batch-service.log 2>&1"

echo.
echo Todos los microservicios han sido iniciados.
echo Logs en: <servicio>\logs\*.log
echo Esperando 60 segundos para que los servicios se registren en Eureka...
timeout /t 60 /nobreak >nul
echo.
echo Verifique puertos: netstat -an ^| findstr LISTENING
echo Revise logs: type eureka-server\logs\eureka.log ^| more
