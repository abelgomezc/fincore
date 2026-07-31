package com.fincore.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway — Punto de entrada único para FinCore.
 *
 * Funciones:
 * - Validación de JWT (OAuth2)
 * - Rate limiting por IP (Redis)
 * - Registro de auditoría de todas las peticiones
 * - Rutas a todos los microservicios
 * - CORS para el frontend (localhost:5173)
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
