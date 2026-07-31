package com.fincore.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Auth Service — Servicio de autenticación y autorización.
 *
 * Funciones:
 * - OAuth2 + JWT (HS512)
 * - Login con BCrypt + bloqueo tras 5 intentos fallidos
 * - Refresh token rotativo
 * - Registro de sesiones activas en Redis
 * - Gestión de usuarios y roles
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
