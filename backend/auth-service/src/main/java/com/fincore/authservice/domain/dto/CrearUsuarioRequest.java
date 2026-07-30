package com.fincore.authservice.domain.dto;

import com.fincore.authservice.domain.enums.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record CrearUsuarioRequest(
        @NotBlank(message = "username es obligatorio") String username,
        @NotBlank(message = "password es obligatorio") String password,
        String nombres,
        String apellidos,
        @Email(message = "email debe ser valido") String email,
        @NotNull(message = "roles es obligatorio") Set<RolUsuario> roles) {
}
