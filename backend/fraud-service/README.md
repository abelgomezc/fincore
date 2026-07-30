# Fraud Service

Microservicio de deteccion y prevencion de fraude para FinCore.

## Descripcion
Servicio encargado de evaluar transacciones en tiempo real contra reglas antifraude.

## Caracteristicas
- Spring Boot 3.2.5
- Java 17
- Spring Data JPA
- Spring Web
- Eureka Client
- Kafka Consumer/Producer
- OpenAPI / Swagger

## Estructura
- `src/main/java/com/fincore/fraudservice/` - Codigo fuente principal
- `sql/` - Scripts de base de datos
- `docs/` - Documentacion adicional
- `postman/` - Colecciones Postman
- `scripts/` - Scripts de despliegue y utilidades

## Ejecucion local

1. Configurar variables de entorno segun `.env.example`.
2. Ejecutar script `sql/schema-fraud.sql` en la base de datos.
3. Levantar servicio con Maven: `mvn spring-boot:run`.

## API
- `POST /api/v1/fraud/evaluar` - Evalua una transaccion contra las reglas antifraude.

Copyright Abel Gomez 2026
