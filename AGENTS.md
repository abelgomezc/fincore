# AGENTS.md — FinCore Project

## Build & Compile

The project uses Maven. The parent POM lives at `fincore-parent/pom.xml`. Due to module path structure, install the parent first, then build individual modules:

```bash
# Install parent POM (required once)
mvn install -N -f fincore-parent/pom.xml

# Compile transfer-service
mvn compile -f transfer-service/pom.xml -DskipTests

# Run all module tests (context tests require test profile)
mvn test -f transfer-service/pom.xml -Dtest="SagaOrchestratorTest,NumeradorServiceTest,TransferenciaServiceImplTest"
mvn test -f fraud-service/pom.xml -Dtest="MontoInusualRuleTest,FraudServiceApplicationTest"
mvn test -f notification-service/pom.xml -Dtest="TipoNotificacionTest,NotificationServiceApplicationTest"
mvn test -f audit-service/pom.xml -Dtest="AuditServiceApplicationTest"
mvn test -f backoffice-service/pom.xml -Dtest="BackofficeServiceApplicationTest"
mvn test -f batch-service/pom.xml -Dtest="BatchServiceApplicationTest"

# Compile all modules (from project root)
mvn compile -DskipTests -f fincore-parent/pom.xml
mvn -f fincore-parent/pom.xml -pl transfer-service -am compile -DskipTests
```

## Project Structure

- `fincore-parent/` — Parent POM with dependency management and plugin config
- `eureka-server/` — Netflix Eureka discovery server
- `api-gateway/` — Spring Cloud Gateway
- `auth-service/` — JWT authentication service
- `customer-service/` — Customer data service
- `account-service/` — Account management & balance
- `ledger-service/` — Contable ledger (double-entry bookkeeping)
- `transfer-service/` — Transfer service with Saga Pattern orchestrator
 - `fraud-service/` — Fraud scoring (10-rule engine)
 - `notification-service/` — Email/push/WebSocket notifications
 - `audit-service/` — Audit trail
 - `backoffice-service/` — Administrative dashboard
 - `batch-service/` — Scheduled batch jobs

## Key Decisions

- Java 21 (virtual threads for async saga execution)
- Spring Boot 3.2.5, Spring Cloud 2023.0.0
- Lombok 1.18.32, MapStruct 1.5.5.Final
- gRPC 1.62.2 for inter-service communication (with `grpc-spring-boot-starter` 5.2.0)
- Kafka for event-driven communication
- Flyway for database migrations
- Asientos (ledger entries) are immutable — reversals create new asientos
- Saga Pattern: orchestrator-based with compensating transactions

## Module-Specific Notes

- **transfer-service**: Uses `protoc-maven-plugin` alternative — proto stub classes created manually since the Maven plugin isn't on Maven Central. gRPC clients use `ManagedChannelBuilder` directly instead of `@GrpcClient` annotation.
- **fraud-service**: Same gRPC dependency pattern as transfer-service. Rules engine with 10 configurable rules.
- **ledger-service**: Uses `jakarta.persistence.Version` for optimistic locking.
- **notification-service**: Uses Spring's `WebSocketMessageBrokerConfigurer` for STOMP/WebSocket.

## Transfer Service Specifics

- 12-step orchestrated saga with compensating transactions
- Async execution via `Thread.startVirtualThread()`
- WebSocket notifications at `/ws/transferencias?idUsuario=xxx`
- gRPC clients to account-service (port 9083), fraud-service (port 9086), ledger-service (port 9084)
