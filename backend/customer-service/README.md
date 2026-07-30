# Customer Service - FinCore

Microservicio de gestión de clientes para la plataforma FinCore.

## Descripción

Este microservicio maneja la información de clientes (personas naturales y jurídicas) del ecosistema FinCore.

## Características

- Registro de clientes
- Consulta de clientes por ID y número de identificación
- Integración con Eureka para descubrimiento de servicios
- Publicación de eventos en Kafka cuando se crea un cliente
- Documentación de API con OpenAPI/Swagger

## Requisitos

- Java 17
- Spring Boot 3.2.5
- PostgreSQL
- Eureka Server
- Kafka

## Configuración

Variables de entorno disponibles en `.env.example`.

## Ejecución

```bash
mvn spring-boot:run
```

## API

[Documentación Swagger](/swagger-ui.html)

## Licencia

Copyright Abel Gomez 2026
