package com.fincore.auth.service.impl;

import com.fincore.auth.entity.Usuario;
import com.fincore.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación del servicio JWT.
 *
 * Genera tokens con claims: userId, email, rol, deviceId, sessionId.
 * Usa HS512 con secret de mínimo 64 caracteres.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:1800000}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration:86400000}")
    private long refreshExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generarAccessToken(Usuario usuario, String sessionId, String deviceId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuario.getId().toString());
        claims.put("rol", usuario.getRol().name());
        claims.put("deviceId", deviceId != null ? deviceId : "unknown");
        claims.put("sessionId", sessionId);
        claims.put("type", "access");

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .claims(claims)
                .subject(usuario.getEmail())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String generarRefreshToken(Usuario usuario, String sessionId, String deviceId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuario.getId().toString());
        claims.put("rol", usuario.getRol().name());
        claims.put("deviceId", deviceId != null ? deviceId : "unknown");
        claims.put("sessionId", sessionId);
        claims.put("type", "refresh");

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .claims(claims)
                .subject(usuario.getEmail())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public String extraerEmail(String token) {
        return extraerClaims(token).getSubject();
    }

    @Override
    public String extraerUserId(String token) {
        return extraerClaims(token).get("userId", String.class);
    }

    @Override
    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    @Override
    public String extraerSessionId(String token) {
        return extraerClaims(token).get("sessionId", String.class);
    }

    @Override
    public boolean validarToken(String token, Usuario usuario) {
        try {
            String email = extraerEmail(token);
            return email.equals(usuario.getEmail()) && !esTokenExpirado(token);
        } catch (Exception e) {
            log.warn("Error validando token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean esTokenExpirado(String token) {
        try {
            Claims claims = extraerClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
