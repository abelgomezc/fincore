package com.fincore.auth.service;

import com.fincore.auth.entity.Usuario;
import io.jsonwebtoken.Claims;

/**
 * Interfaz del servicio JWT.
 *
 * Define operaciones de generación y validación de tokens.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface JwtService {

    String generarAccessToken(Usuario usuario, String sessionId, String deviceId);

    String generarRefreshToken(Usuario usuario, String sessionId, String deviceId);

    Claims extraerClaims(String token);

    String extraerEmail(String token);

    String extraerUserId(String token);

    String extraerRol(String token);

    String extraerSessionId(String token);

    boolean validarToken(String token, Usuario usuario);

    boolean esTokenExpirado(String token);
}
