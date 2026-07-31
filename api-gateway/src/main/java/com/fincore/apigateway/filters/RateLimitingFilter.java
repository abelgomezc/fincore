package com.fincore.apigateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Filtro de rate limiting por IP.
 *
 * Limita el número de peticiones por minuto por dirección IP usando Redis.
 * Configuración: 100 req/min con burst capacity de 20.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class RateLimitingFilter implements GatewayFilter, Ordered {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @Value("${gateway.rate-limit.requests-per-minute:100}")
    private int maxRequestsPerMinute;

    @Value("${gateway.rate-limit.burst-capacity:20}")
    private int burstCapacity;

    private static final String REDIS_KEY_PREFIX = "rate_limit:";

    public RateLimitingFilter(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String clientIp = getClientIp(request);

        if (clientIp == null) {
            return chain.filter(exchange);
        }

        String redisKey = REDIS_KEY_PREFIX + clientIp;
        String now = String.valueOf(Instant.now().getEpochSecond());

        return redisTemplate.opsForValue().increment(redisKey)
                .flatMap(count -> {
                    if (count == 1L) {
                        // Primera petición — establecer TTL de 1 minuto
                        return redisTemplate.expire(redisKey, 60, java.util.concurrent.TimeUnit.SECONDS)
                                .then(Mono.just(count));
                    }
                    return Mono.just(count);
                })
                .flatMap(count -> {
                    if (count > maxRequestsPerMinute + burstCapacity) {
                        log.warn("Rate limit excedido para IP: {} ({} requests)", clientIp, count);
                        return tooManyRequests(exchange, clientIp, (int) count);
                    }
                    return chain.filter(exchange);
                });
    }

    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddress() != null
                ? request.getRemoteAddress().getAddress().getHostAddress()
                : null;
    }

    private Mono<Void> tooManyRequests(ServerWebExchange exchange, String clientIp, int count) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add("Content-Type", "application/json");
        response.getHeaders().add("Retry-After", "60");

        RateLimitResponse error = RateLimitResponse.builder()
                .error("RATE_LIMIT_EXCEEDED")
                .message("Demasiadas peticiones. Límite: " + maxRequestsPerMinute + " req/min para IP: " + clientIp)
                .requests(count)
                .limit(maxRequestsPerMinute + burstCapacity)
                .retryAfterSeconds(60)
                .timestamp(System.currentTimeMillis())
                .build();

        byte[] bytes;
        try {
            bytes = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsBytes(error);
        } catch (Exception e) {
            bytes = ("{\"error\":\"RATE_LIMIT_EXCEEDED\"}").getBytes();
        }

        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 200;
    }

    @lombok.Getter
    @lombok.Setter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class RateLimitResponse {
        private String error;
        private String message;
        private int requests;
        private int limit;
        private int retryAfterSeconds;
        private long timestamp;
    }
}
