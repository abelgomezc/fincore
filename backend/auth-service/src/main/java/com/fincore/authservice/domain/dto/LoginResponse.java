package com.fincore.authservice.domain.dto;

public record LoginResponse(String token, String type, Long expiresIn) {
}
