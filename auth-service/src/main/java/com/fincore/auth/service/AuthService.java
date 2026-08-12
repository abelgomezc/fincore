package com.fincore.auth.service;

import com.fincore.auth.dto.request.LoginRequest;
import com.fincore.auth.dto.request.RefreshRequest;
import com.fincore.auth.dto.request.RegisterRequest;
import com.fincore.auth.dto.response.AuthResponse;
import com.fincore.auth.dto.response.UsuarioResponse;
import com.fincore.auth.dto.response.AuditoriaResponse;

import java.util.List;

/**
 * Interfaz del servicio de autenticación.
 *
 * Define las operaciones de login, registro, refresh token,
 * bloqueo de usuarios y gestión de sesiones.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);

    AuthResponse refreshToken(RefreshRequest request);

    void logout(String sessionId);

    void logoutAllSessions(Long userId);

    UsuarioResponse obtenerUsuarioPorEmail(String email);

    void bloquearUsuario(Long userId, String motivo);

    void desbloquearUsuario(Long userId);

    void suspenderUsuario(Long userId, String motivo);

    void reactivarUsuario(Long userId);

    void eliminarUsuario(Long userId);

    void cambiarPassword(Long userId, String passwordActual, String passwordNuevo, String comentario);

    List<UsuarioResponse> listarUsuarios();

    List<AuditoriaResponse> consultarAuditoriaUsuario(Long userId);

    List<AuditoriaResponse> consultarAuditoriaPasswords(Long userId);
}
