package com.fincore.authservice.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "refreshToken es obligatorio") String refreshToken) {
}
