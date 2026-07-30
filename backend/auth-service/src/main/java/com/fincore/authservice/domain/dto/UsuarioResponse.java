package com.fincore.authservice.domain.dto;

public record UsuarioResponse(Long id, String username, String nombres, String apellidos, String email, Boolean activo) {
}
