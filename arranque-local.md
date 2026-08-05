# Arranque Local — FinCore Banking

Guía completa para levantar el sistema FinCore Banking de forma local:
- **Redis y Kafka** → vía Docker (contenedores nombrados con prefijo `fincore-`)
- **Microservicios y Frontend** → ejecución local con `java -jar` y `npm run dev`

> Requiere: Java 21, Maven, Node.js 20+, Docker Desktop, PostgreSQL 16 (local o Docker)

---

## 1. Servicios de infraestructura (Docker)

### 1.1. Docker compose (recomendado)

Crear un archivo `.env.fincore` con las credenciales exactas del `.env`:

```bash
docker run -d \
  --name fincore-redis \
  -p 6379:6379 \
  -e REDIS_PASSWORD=redis_fincore_2026 \
  redis:7 \
  sh -c "redis-server --requirepass redis_fincore_2026 --appendonly yes"

docker run -d \
  --name fincore-zookeeper \
  -p 2181:2181 \
  -e ALLOW_ANONYMOUS_LOGIN=yes \
  bitnami/zookeeper:3.9

docker run -d \
  --name fincore-kafka \
  -p 9092:9092 \
  --link fincore-zookeeper:zookeeper \
  -e KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_CFG_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e KAFKA_CFG_AUTO_CREATE_TOPICS_ENABLE=true \
  -e KAFKA_CFG_NUM_PARTITIONS=1 \
  bitnami/kafka:3
```

### 1.2. Alternativa: Apache Kafka KRaft (un solo contenedor, sin Zookeeper)

```bash
docker run -d \
  --name fincore-kafka \
  -p 9092:9092 \
  -e KAFKA_NODE_ID=1 \
  -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@127.0.0.1:29093 \
  -e KAFKA_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:29093 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_AUTO_CREATE_TOPICS_ENABLE=true \
  -e KAFKA_NUM_PARTITIONS=1 \
  apache/kafka:latest
```

---

## 2. Topic de Kafka

Con `auto.create.topics.enable=true` en Kafka, los topics se crean
automáticamente al publicar/consumir. Para crear manualmente:

```bash
# Lista de todos los topics del proyecto
TOPICS=(
  transferencia.iniciada
  transferencia.completada
  transferencia.fallida
  transferencia.en.revision
  transferencia.fondos.reservados
  transferencia.debito.ejecutado
  transferencia.credito.ejecutado
  transferencia.revertida
  audit.transferencia.completada
  notification.transferencia
  batch.intereses.calculados
  cliente.creado
  cliente.actualizado
  cliente.bloqueado
  cliente.desbloqueado
  cliente.desactivado
  cliente.kyc.aprobado
  cliente.kyc.rechazado
  cliente.kyc.en_revision
  cuenta.creada
  cuenta.bloqueada
  cuenta.saldo.actualizado
  audit.events
)

for t in "${TOPICS[@]}"; do
  docker exec fincore-kafka kafka-topics.sh \
    --create --topic "$t" --bootstrap-server localhost:9092 \
    --partitions 1 --replication-factor 1 2>/dev/null || true
done
```

---

## 3. Variables de entorno

Cargar el `.env` del repositorio antes de iniciar los microservicios:

```bash
set -a && source .env && set +a
```

O en PowerShell:

```powershell
$env:POSTGRES_USER="fincore"
$env:POSTGRES_PASSWORD="fincore123"
$env:DB_AUTH="fincore_auth"
$env:DB_CUSTOMERS="fincore_customers"
$env:DB_ACCOUNTS="fincore_accounts"
$env:DB_LEDGER="fincore_ledger"
$env:DB_TRANSFERS="fincore_transfers"
$env:DB_FRAUD="fincore_fraud"
$env:DB_NOTIFICATION="fincore_notification"
$env:DB_AUDIT="fincore_audit"
$env:DB_BATCH="fincore_batch"
$env:DB_BACKOFFICE="fincore_backoffice"
$env:REDIS_HOST="localhost"
$env:REDIS_PORT="6379"
$env:REDIS_PASSWORD="redis_fincore_2026"
$env:KAFKA_BOOTSTRAP_SERVERS="localhost:9092"
$env:EUREKA_URL="http://localhost:8761/eureka/"
$env:JWT_SECRET="fincore_jwt_hs512_secret_minimo_64_caracteres_2026_abel_gomez_banking"
$env:DAILY_LIMIT="5000.00"
$env:TX_LIMIT="2000.00"
$env:MONTHLY_LIMIT="20000.00"
$env:SAGA_TIMEOUT="120"
$env:SAGA_MAX_RETRIES="3"
$env:FRAUD_SCORE_APPROVE="30"
$env:FRAUD_SCORE_REVIEW="70"
$env:FRAUD_SCORE_REJECT="70"
$env:FRAUD_VELOCITY_MAX_TX_PER_HOUR="10"
$env:FRAUD_VELOCITY_WINDOW_MINUTES="60"
$env:SMTP_FROM="noreply@fincore.banking"
$env:FRONTEND_URL="http://localhost:5173"
```

---

## 4. Compilación

```bash
# Instalar el POM padre
mvn install -N -f fincore-parent/pom.xml

# Compilar todos los módulos
mvn compile -DskipTests -f fincore-parent/pom.xml

# Generar los JAR ejecutables
mvn package -DskipTests -P '!docker' -f eureka-server/pom.xml
mvn package -DskipTests -P '!docker' -f auth-service/pom.xml
mvn package -DskipTests -P '!docker' -f api-gateway/pom.xml
mvn package -DskipTests -P '!docker' -f customer-service/pom.xml
mvn package -DskipTests -P '!docker' -f account-service/pom.xml
mvn package -DskipTests -P '!docker' -f ledger-service/pom.xml
mvn package -DskipTests -P '!docker' -f transfer-service/pom.xml
mvn package -DskipTests -P '!docker' -f fraud-service/pom.xml
mvn package -DskipTests -P '!docker' -f notification-service/pom.xml
mvn package -DskipTests -P '!docker' -f audit-service/pom.xml
mvn package -DskipTests -P '!docker' -f backoffice-service/pom.xml
mvn package -DskipTests -P '!docker' -f batch-service/pom.xml
```

---

## 5. Inicio de microservicios (orden de arranque)

### 5.1. Eureka Server (primero)

```bash
java -jar eureka-server/target/eureka-server-1.0.0.jar \
  --spring.flyway.enabled=false \
  --spring.jpa.hibernate.ddl-auto=update \
  --grpc.security.auth.enabled=false \
  --server.port=8761
```

### 5.2. Microservicios (en paralelo)

| Servicio | Puerto | gRPC |
|---|---|---|
| api-gateway | 8080 | — |
| auth-service | 8081 | — |
| customer-service | 8082 | — |
| account-service | 8083 | 9083 |
| ledger-service | 8084 | 9084 |
| transfer-service | 8085 | — |
| fraud-service | 8090 | 9086 |
| audit-service | 8091 | — |
| notification-service | 8092 | — |
| backoffice-service | 8093 | — |
| batch-service | 8094 | — |

```bash
# Eureka
java -jar eureka-server/target/eureka-server-1.0.0.jar \
  --spring.flyway.enabled=false \
  --spring.jpa.hibernate.ddl-auto=update \
  --grpc.security.auth.enabled=false \
  --server.port=8761 &

sleep 25

# Microservicios (todos en paralelo)
java -jar api-gateway/target/api-gateway-1.0.0.jar \
  --spring.flyway.enabled=false \
  --spring.jpa.hibernate.ddl-auto=update \
  --grpc.security.auth.enabled=false \
  --server.port=8080 &

java -jar auth-service/target/auth-service-1.0.0.jar \
  --spring.flyway.enabled=false \
  --spring.jpa.hibernate.ddl-auto=update \
  --grpc.security.auth.enabled=false \
  --server.port=8081 &

java -jar customer-service/target/customer-service-1.0.0.jar \
  --spring.flyway.enabled=false \
  --spring.jpa.hibernate.ddl-auto=update \
  --grpc.security.auth.enabled=false \
  --server.port=8082 &

java -jar account-service/target/account-service-1.0.0.jar \
  --spring.flyway.enabled=false \
  --spring.jpa.hibernate.ddl-auto=update \
  --grpc.security.auth.enabled=false \
  --server.port=8083 &

java -jar ledger-service/target/ledger-service-1.0.0.jar \
  --spring.flyway.enabled=false \
  --spring.jpa.hibernate.ddl-auto=update \
  --grpc.security.auth.enabled=false \
  --server.port=8084 &

java -jar transfer-service/target/transfer-service-1.0.0.jar \
  --spring.flyway.enabled=false \
  --spring.jpa.hibernate.ddl-auto=update \
  --grpc.security.auth.enabled=false \
  --server.port=8085 &

java -jar fraud-service/target/fraud-service-1.0.0.jar \
  --spring.flyway.enabled=false \
  --spring.jpa.hibernate.ddl-auto=update \
  --grpc.security.auth.enabled=false \
  --server.port=8090 &

java -jar notification-service/target/notification-service-1.0.0.jar \
  --spring.flyway.enabled=false \
  --spring.jpa.hibernate.ddl-auto=update \
  --grpc.security.auth.enabled=false \
  --server.port=8092 &

java -jar audit-service/target/audit-service-1.0.0.jar \
  --spring.flyway.enabled=false \
  --spring.jpa.hibernate.ddl-auto=update \
  --grpc.security.auth.enabled=false \
  --server.port=8091 &

java -jar backoffice-service/target/backoffice-service-1.0.0.jar \
  --spring.flyway.enabled=false \
  --spring.jpa.hibernate.ddl-auto=update \
  --grpc.security.auth.enabled=false \
  --server.port=8093 &

java -jar batch-service/target/batch-service-1.0.0.jar \
  --spring.flyway.enabled=false \
  --spring.jpa.hibernate.ddl-auto=update \
  --grpc.security.auth.enabled=false \
  --server.port=8094 &
```

---

## 6. Frontend

```bash
cd frontend
npm install
npm run dev
```

Acceder a `http://localhost:5173`

---

## 7. Health checks

| Servicio | URL |
|---|---|
| Eureka | http://localhost:8761/ |
| API Gateway | http://localhost:8080/actuator/health |
| Auth | http://localhost:8081/actuator/health |
| Customer | http://localhost:8082/actuator/health |
| Account | http://localhost:8083/actuator/health |
| Ledger | http://localhost:8084/actuator/health |
| Transfer | http://localhost:8085/actuator/health |
| Fraud | http://localhost:8090/actuator/health |
| Audit | http://localhost:8091/actuator/health |
| Notification | http://localhost:8092/actuator/health |
| Backoffice | http://localhost:8093/actuator/health |
| Batch | http://localhost:8094/actuator/health |

---

## 8. Detener todo

```bash
# Detener microservicios
pkill -f "java -jar"

# Detener contenedores Docker
docker stop fincore-redis fincore-kafka
docker rm fincore-redis fincore-kafka

# Detener frontend
# Ctrl+C en la terminal del frontend
```
