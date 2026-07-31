package com.fincore.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de login.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 255, message = "El email no puede excedar 255 caracteres")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @Size(max = 255, message = "El deviceId no puede exceder 255 caracteres")
    private String deviceId;

    private String userAgent;

    private String ipOrigen;
}
