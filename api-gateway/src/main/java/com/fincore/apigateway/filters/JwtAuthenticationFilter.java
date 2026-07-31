package com.fincore.apigateway.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

/**
 * Filtro de autenticación JWT.
 *
 * Valida el token JWT en cada petición antes de enrutar al microservicio.
 * Extrae claims: userId, email, rol, deviceId, sessionId.
 * Las rutas públicas (login, register) no requieren token.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class JwtAuthenticationFilter implements GatewayFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String[] PUBLIC_URLS = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Rutas públicas — no requieren autenticación
        for (String publicUrl : PUBLIC_URLS) {
            if (path.startsWith(publicUrl)) {
                return chain.filter(exchange);
            }
        }

        // Extraer Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            log.warn("Token JWT ausente o malformado en: {}", path);
            return unauthorized(exchange, "Token de autenticación requerido");
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = parseClaims(token);

            // Verificar expiración
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                log.warn("Token JWT expirado para usuario: {}", claims.getSubject());
                return unauthorized(exchange, "Token expirado");
            }

            // Agregar claims al header para microservicios downstream
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", claims.get("userId", String.class))
                    .header("X-User-Email", claims.getSubject())
                    .header("X-User-Rol", claims.get("rol", String.class))
                    .header("X-Device-Id", claims.get("deviceId", String.class))
                    .header("X-Session-Id", claims.get("sessionId", String.class))
                    .header("X-Trace-Id", request.getHeaders().getFirst("X-Request-Id") != null
                            ? request.getHeaders().getFirst("X-Request-Id")
                            : generateTraceId())
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            log.warn("Error validando token JWT: {}", e.getMessage());
            return unauthorized(exchange, "Token inválido");
        }
    }

    private Claims parseClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");

        ErrorResponse error = ErrorResponse.builder()
                .error("UNAUTHORIZED")
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(error);
        } catch (Exception e) {
            bytes = ("{\"error\":\"UNAUTHORIZED\",\"message\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        }

        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    private String generateTraceId() {
        return Base64.getEncoder().withoutPadding().encodeToString(
                (System.nanoTime() + "").getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }

    @lombok.Getter
    @lombok.Setter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class ErrorResponse {
        private String error;
        private String message;
        private long timestamp;
    }
}
