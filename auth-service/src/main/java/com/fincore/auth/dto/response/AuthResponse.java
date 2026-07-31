package com.fincore.auth.dto.response;

import com.fincore.auth.enums.EstadoUsuario;
import com.fincore.auth.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta de autenticación.
 *
 * Contiene el JWT (access token), refresh token y metadatos
 * de la sesión.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String tokenType = "Bearer";

    private Long userId;
    private String email;
    private String nombreCompleto;
    private Rol rol;
    private EstadoUsuario estado;
    private String sessionId;
    private String deviceId;
}
