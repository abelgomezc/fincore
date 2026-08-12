package com.fincore.auth.controller;

import com.fincore.auth.dto.request.LoginRequest;
import com.fincore.auth.dto.request.RefreshRequest;
import com.fincore.auth.dto.request.RegisterRequest;
import com.fincore.auth.dto.request.CambiarPasswordRequest;
import com.fincore.auth.dto.response.AuthResponse;
import com.fincore.auth.dto.response.UsuarioResponse;
import com.fincore.auth.dto.response.AuditoriaResponse;
import com.fincore.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
 * - PUT /api/auth/usuario/{id}/suspender — suspender usuario (ADMIN)
 * - PUT /api/auth/usuario/{id}/reactivar — reactivar usuario (ADMIN)
 * - PUT /api/auth/usuario/{id}/eliminar — eliminar usuario (ADMIN)
 * - PUT /api/auth/usuario/{id}/password — cambiar contraseña
 * - GET /api/auth/usuarios — listar todos los usuarios (ADMIN)
 * - GET /api/auth/auditoria/{userId} — consultar auditoría de usuario
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

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        log.info("GET /api/auth/usuarios");
        List<UsuarioResponse> response = authService.listarUsuarios();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auditoria/{userId}")
    public ResponseEntity<List<AuditoriaResponse>> consultarAuditoria(@PathVariable Long userId) {
        log.info("GET /api/auth/auditoria/{}", userId);
        List<AuditoriaResponse> response = authService.consultarAuditoriaUsuario(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auditoria/passwords/{userId}")
    public ResponseEntity<List<AuditoriaResponse>> consultarAuditoriaPasswords(@PathVariable Long userId) {
        log.info("GET /api/auth/auditoria/passwords/{}", userId);
        List<AuditoriaResponse> response = authService.consultarAuditoriaPasswords(userId);
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

    @PutMapping("/usuario/{id}/suspender")
    public ResponseEntity<Void> suspenderUsuario(@PathVariable Long id,
                                                   @RequestParam String motivo) {
        log.info("PUT /api/auth/usuario/{}/suspender — motivo: {}", id, motivo);
        authService.suspenderUsuario(id, motivo);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/usuario/{id}/reactivar")
    public ResponseEntity<Void> reactivarUsuario(@PathVariable Long id) {
        log.info("PUT /api/auth/usuario/{}/reactivar", id);
        authService.reactivarUsuario(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/usuario/{id}/eliminar")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        log.info("PUT /api/auth/usuario/{}/eliminar", id);
        authService.eliminarUsuario(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/usuario/{id}/password")
    public ResponseEntity<Void> cambiarPassword(@PathVariable Long id,
                                                  @Valid @RequestBody CambiarPasswordRequest request) {
        log.info("PUT /api/auth/usuario/{}/password", id);
        authService.cambiarPassword(id, request.getPasswordActual(), request.getPasswordNuevo(), request.getComentario());
        return ResponseEntity.ok().build();
    }
}
