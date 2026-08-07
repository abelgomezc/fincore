# Documentación Técnica — FinCore

© 2026 Abel Gomez. Todos los derechos reservados.

---

## 1. Arquitectura General

### 1.1 Visión General

FinCore es una simulación de un core banking moderno, inspirado en los
sistemas utilizados por bancos como BBVA, Santander, Bancolombia, Pichincha
y Davivienda. No es un CRUD de cuentas bancarias: cada operación pasa por
validaciones, reglas de negocio, estados, auditoría, eventos y conciliaciones
antes de considerarse completada.

### 1.2 Diagrama de Arquitectura

```
                    ┌─────────────────────────────────────────────┐
                    │              API GATEWAY :8080               │
                    │  JWT Validation · Rate Limiting · CORS       │
                    └──────┬───────────────┬───────────────┬──────┘
                           │               │               │
              ┌────────────┘               │               └────────────┐
              │                          │                            │
    ┌─────────▼────────┐      ┌─────────▼────────┐      ┌───────────▼──────────┐
    │  EUREKA-SERVER    │      │  AUTH-SERVICE     │      │  CUSTOMER-SERVICE     │
    │  :8761 Discovery  │      │  :8081            │      │  :8082                │
    │                   │      │  Keycloak + OAuth2│      │  Clientes + KYC + AML │
    └─────────┬────────┘      │  + JWT            │      └───────────┬──────────┘
              │               └─────────┬─────────┘                  │
              │                         │                            │
    ┌─────────▼────────┐      ┌─────────▼────────┐      ┌───────────▼──────────┐
     │  ACCOUNT-SERVICE │      │  LEDGER-SERVICE   │      │  TRANSFER-SERVICE     │
     │  :8083            │      │  :8084            │      │  :8085                │
     │  Cuentas + CQRS   │      │  Ledger doble     │      │  Saga + Estados       │
     │  + gRPC Server    │      │  partida + gRPC   │      │  + WebSocket          │
     │  + REST → customer│      │                   │      │                       │
     └─────────┬────────┘      └─────────┬────────┘      └───────────┬──────────┘
               │                         │                            │
    ┌─────────▼────────┐      ┌─────────▼────────┐      ┌───────────▼──────────┐
    │  FRAUD-SERVICE   │      │  NOTIFICATION     │      │  AUDIT-SERVICE        │
    │  :8086            │      │  :8087            │      │  :8088                │
    │  Scoring + gRPC  │      │  Kafka + Email    │      │  Auditoría inmutable  │
    └─────────┬────────┘      └─────────┬─────────┘      └───────────┬──────────┘
              │                         │                            │
    ┌─────────▼────────┐      ┌─────────▼────────┐      ┌───────────▼──────────┐
    │  BATCH-SERVICE   │      │  BACKOFFICE-SVC  │      │  FINCORE-UI          │
    │  :8089            │      │  :8090            │      │  :5173              │
    │  Spring Batch     │      │  Portal empleados │      │  React + Vite        │
    └─────────┬────────┘      └───────────────────┘      └─────────────────────┘
              │
    ┌─────────▼────────┐
    │   KAFKA :9092    │
    │   Eventos de     │
    │   dominio        │
    └──────────────────┘
              │
    ┌─────────▼────────┐
    │   REDIS :6379    │
    │   Cache · Rate   │
    │   Limit · Sesiones│
    └──────────────────┘
              │
    ┌─────────▼────────┐
    │  POSTGRES :5432  │
    │  9 bases de datos│
    │  una por servicio│
    └──────────────────┘
```

### 1.3 Comunicación entre Servicios

| Canal         | Uso                                      | Servicios involucrados                          |
|---------------|------------------------------------------|--------------------------------------------------|
| REST/HTTP     | API externas (gateway → servicios)       | Todos                                            |
| REST/HTTP     | Comunicación inter-servicios síncrona     | account-service ↔ customer-service (enriquecimiento de cuenta) |
| gRPC          | Comunicación interna síncrona            | transfer ↔ account, transfer ↔ ledger, transfer ↔ fraud |
| Kafka (async) | Eventos de dominio                       | transfer, account, ledger, fraud, audit, notification |
| Redis         | Cache, rate limiting, sesiones, velocidad| gateway, auth, account, fraud                    |

### 1.4 Principios de Diseño

1. **BaseEntity bancaria**: `@Version` para optimistic locking, campos de auditoría
2. **Ledger inmutable**: asientos y líneas nunca se modifican ni eliminan
3. **4 tipos de saldo**: contable, disponible, retenido, proyectado — siempre sincronizados
4. **Saga orquestada**: 12 pasos con compensating transactions
5. **Audit trail completo**: todo se registra en audit-service
6. **Fail-fast**: validaciones en cada paso antes de continuar
7. **Resiliencia**: circuit breaker + retry en comunicaciones gRPC
8. **Enriquecimiento REST**: `account-service` consulta `customer-service` vía `RestTemplate` para incluir datos del propietario en `CuentaResponse`

---

## 2. Flujo de una Transferencia de Punta a Punta

```
[Cliente] → [Gateway] → [Transfer-Service]
                             ↓
                    [Saga Orchestrator]
                             ↓
  ┌─────────────┬─────────────┬─────────────┬─────────────┐
  │ Paso 1      │ Paso 2      │ Paso 3      │ Paso 4      │
  │ Validar     │ Verificar   │ Validar     │ Evaluar     │
  │ Datos       │ KYC         │ Límites     │ Fraude      │
  └─────────────┴─────────────┴─────────────┴─────────────┘
                             ↓
  ┌─────────────┬─────────────┬─────────────┬─────────────┬─────────────┬─────────────┐
  │ Paso 5      │ Paso 6      │ Paso 7      │ Paso 8      │ Paso 9      │ Paso 10     │
  │ Reservar    │ Crear       │ Débito      │ Crédito     │ Liberar     │ Auditoría   │
  │ Fondos      │ Evento      │ Ledger      │ Ledger      │ Retención   │             │
  └─────────────┴─────────────┴─────────────┴─────────────┴─────────────┴─────────────┘
                             ↓
  ┌─────────────┬─────────────┐
  │ Paso 11     │ Paso 12     │
  │ Cobrar      │ Notificar   │
  │ Comisión    │             │
  └─────────────┴─────────────┘
                             ↓
                    [Kafka Events] → [Audit-Service]
                                    → [Notification-Service]
                                    → [Ledger-Service (asientos)]
                                    → [Account-Service (saldos)]
                                    → [Fraud-Service (perfil)]
```

### 2.1 Detalle del Flujo

1. El cliente autenticado envía una transferencia vía REST al API Gateway
2. El Gateway valida el JWT y registra la auditoría
3. Transfer-Service crea la entidad `Transferencia` en estado `PENDIENTE`
4. El `SagaOrchestrator` inicia la ejecución de los 12 pasos
5. Cada paso es un microservicio que responde vía gRPC
6. Si un paso falla, se ejecutan compensating transactions en orden inverso
7. Al completar, se publican eventos de Kafka para auditoría, notificación y actualización de saldos
8. El frontend recibe actualizaciones en tiempo real vía WebSocket

---

## 3. Microservicios

| Servicio            | Puerto | Base de Datos           | Responsabilidad                          |
|---------------------|--------|-------------------------|------------------------------------------|
| eureka-server       | 8761   | -                       | Service Discovery                        |
| api-gateway         | 8080   | -                       | Gateway, JWT, Rate Limiting, CORS        |
| auth-service        | 8081   | fincore_auth            | OAuth2, JWT, usuarios, sesiones          |
| customer-service    | 8082   | fincore_customers       | Clientes, KYC, AML, documentos (REST público en `/api/clientes/**`) |
| account-service     | 8083   | fincore_accounts        | Cuentas, saldos, CQRS, gRPC server, enriquecimiento con customer-service |
| ledger-service      | 8084   | fincore_ledger          | Ledger doble partida, asientos contables  |
| transfer-service    | 8085   | fincore_transfers       | Saga orchestrator, WebSocket, estados     |
| fraud-service       | 8086   | fincore_fraud           | Motor antifraude, scoring, lista negra    |
| notification-service| 8087   | -                       | Email, push, plantillas                   |
| audit-service       | 8088   | fincore_audit           | Auditoría inmutable, trazabilidad          |
| batch-service       | 8089   | fincore_batch           | Conciliación nocturna, intereses, PDFs    |
| backoffice-service  | 8090   | fincore_backoffice      | Portal de empleados, reportes             |
| fincore-ui          | 5173   | -                       | React dashboard + demo en vivo            |

---

## 4. Tecnologías

### Backend
- **Java 21** — Virtual Threads para alto throughput
- **Spring Boot 3.2.5** — Base de microservicios
- **Spring Cloud 2023.0.0** — Eureka, Gateway, LoadBalancer
- **Spring Security 6** — OAuth2 Resource Server
- **Spring Data JPA** — ORM + repositorios
- **Spring Batch 5.x** — Procesamiento nocturno
- **PostgreSQL 16** — Base de datos principal
- **Flyway** — Migraciones BD
- **Redis 7** — Cache + rate limiting + sesiones
- **Apache Kafka 3.6** — Eventos financieros
- **gRPC** — Comunicación interna
- **Resilience4J** — Circuit Breaker
- **OpenTelemetry** — Trazabilidad distribuida
- **Lombok** — Boilerplate
- **MapStruct** — Mapeo entidades DTOs
- **iText 7** — Generación de extractos PDF

### Frontend
- **React 18** — Framework
- **Vite 5** — Build tool
- **TypeScript 5** — Tipado
- **Tailwind CSS 3** — Estilos
- **TanStack Query v5** — Server state
- **Zustand** — Client state
- **Recharts** — Gráficas financieras
- **React Router v6** — Navegación
- **Axios** — HTTP (timeout 30s)
- **Framer Motion** — Animaciones
- **WebSocket** — Demo transferencia en tiempo real

---

## 5. Reglas de Código

| Regla | Descripción |
|-------|-------------|
| 1 | SOLO application.properties. NUNCA .yml ni .yaml |
| 2 | IDs: SIEMPRE Long con BIGSERIAL. NUNCA UUID |
| 3 | Secrets: SIEMPRE `${VARIABLE:valor_local}` |
| 4 | FetchType.LAZY por defecto. Excepciones documentadas y justificadas |
| 5 | @Autowired en campos. NUNCA inyección por constructor |
| 6 | NUNCA lógica en controladores. SIEMPRE en servicios |
| 7 | Interfaces en service/. Implementaciones en service/impl/ |
| 8 | Enums en enums/ en cada microservicio |
| 9 | Entidades: @Getter @Setter @Builder (NUNCA @Data en JPA) |
| 10 | DTOs: @Data @Builder @NoArgsConstructor @AllArgsConstructor |
| 11 | Código INGLÉS. BD y comentarios ESPAÑOL |
| 12 | Copyright © 2026 Abel Gomez en footer y Application.java |
| 13 | Optimistic locking (@Version) en todas las entidades financieras |
| 14 | @Transactional en escrituras críticas |
| 15 | El ledger es INMUTABLE — nunca UPDATE ni DELETE en asientos |
| 16 | Todo movimiento financiero SIEMPRE genera asiento contable |
