package com.fincore.auth.controller;

import com.fincore.auth.dto.request.LoginRequest;
import com.fincore.auth.dto.request.RefreshRequest;
import com.fincore.auth.dto.request.RegisterRequest;
import com.fincore.auth.dto.response.AuthResponse;
import com.fincore.auth.dto.response.UsuarioResponse;
import com.fincore.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación.
 *
 * Endpoints:
 * - POST /api/auth/login — autenticación con BCrypt
 * - POST /api/auth/register — registro de nuevos usuarios
 * - POST /api/auth/refresh — renovación de access token
 * - POST /api/auth/logout — cierre de sesión
 * - POST /api/auth/logout-all — cierre de todas las sesiones
 * - GET /api/auth/usuario/{email} — obtener información del usuario
 * - PUT /api/auth/usuario/{id}/bloquear — bloquear usuario (ADMIN)
 * - PUT /api/auth/usuario/{id}/desbloquear — desbloquear usuario (ADMIN)
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login — email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/auth/register — email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshRequest request) {
        log.info("POST /api/auth/refresh");
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        log.info("POST /api/auth/logout — sessionId: {}", sessionId);
        if (sessionId != null) {
            authService.logout(sessionId);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(@RequestHeader("X-User-Id") String userId) {
        log.info("POST /api/auth/logout-all — userId: {}", userId);
        authService.logoutAllSessions(Long.valueOf(userId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/usuario/{email}")
    public ResponseEntity<UsuarioResponse> obtenerUsuario(@PathVariable String email) {
        log.info("GET /api/auth/usuario/{}", email);
        UsuarioResponse response = authService.obtenerUsuarioPorEmail(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/usuario/{id}/bloquear")
    public ResponseEntity<Void> bloquearUsuario(@PathVariable Long id,
                                                 @RequestParam String motivo) {
        log.info("PUT /api/auth/usuario/{}/bloquear — motivo: {}", id, motivo);
        authService.bloquearUsuario(id, motivo);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/usuario/{id}/desbloquear")
    public ResponseEntity<Void> desbloquearUsuario(@PathVariable Long id) {
        log.info("PUT /api/auth/usuario/{}/desbloquear", id);
        authService.desbloquearUsuario(id);
        return ResponseEntity.ok().build();
    }
}
