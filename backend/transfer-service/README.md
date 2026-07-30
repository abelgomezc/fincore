# FinCore Transfer Service

Copyright Abel Gomez 2026

## Descripción

Microservicio de transferencias para la plataforma FinCore.

## Arquitectura

- **Java 17**, **Spring Boot 3.2.5**
- **Spring Data JPA**, **Spring Web**, **Eureka Client**, **Kafka Producer**, **gRPC**, **Resilience4j**, **OpenAPI**
- **Saga Pattern** para gestión de transacciones distribuidas
- **Optimistic Locking** con `@Version`
- **Inmutabilidad financiera**: registros financieros sin operaciones UPDATE ni DELETE

## Estructura del Proyecto

```
transfer-service/
├── pom.xml
├── Dockerfile
├── .env.example
├── README.md
├── sql/
│   └── schema-transfer.sql
├── scripts/
├── postman/
├── docs/
└── src/
    ├── main/
    │   ├── java/com/fincore/transferservice/
    │   │   ├── TransferServiceApplication.java
    │   │   ├── domain/
    │   │   │   ├── enums/EstadoTransferencia.java
    │   │   │   ├── entity/Transferencia.java
    │   │   │   ├── entity/HistorialEstadoTransferencia.java
    │   │   │   └── dto/
    │   │   │       ├── IniciarTransferenciaRequest.java
    │   │   │       └── TransferenciaResponse.java
    │   │   ├── repository/
    │   │   │   ├── TransferenciaRepository.java
    │   │   │   └── HistorialEstadoTransferenciaRepository.java
    │   │   ├── service/
    │   │   │   ├── TransferService.java
    │   │   │   └── impl/TransferServiceImpl.java
    │   │   ├── controller/TransferController.java
    │   │   └── event/TransferenciaEvent.java
    │   └── resources/application.properties
    └── test/
```

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/v1/transferencias` | Iniciar una nueva transferencia |
| GET | `/api/v1/transferencias/{id}` | Obtener transferencia por id |
| GET | `/api/v1/transferencias` | Listar todas las transferencias |

## Estados de Transferencia

- `PENDIENTE`
- `VALIDANDO`
- `AUTORIZADA`
- `EN_REVISION`
- `RESERVANDO`
- `PROCESANDO`
- `ACREDITANDO`
- `COMPLETADA`
- `RECHAZADA`
- `REVERTIDA`
- `ERROR`

## Configuración

Copiar `.env.example` a `.env` y ajustar las variables de entorno según el entorno de despliegue.

## Docker

```bash
docker build -t transfer-service .
docker run -p 8081:8081 transfer-service
```

## License

Copyright Abel Gomez 2026