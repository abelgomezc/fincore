package com.fincore.apigateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtro de rate limiting por IP.
 *
 * Limita el número de peticiones por minuto por dirección IP usando memoria concurrente.
 * Configuración: 100 req/min con burst capacity de 20.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class RateLimitingFilter implements GatewayFilter, Ordered {

    private final int maxRequestsPerMinute;
    private final int burstCapacity;

    private final ConcurrentHashMap<String, Integer> requestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> resetTimes = new ConcurrentHashMap<>();

    private static final String REDIS_KEY_PREFIX = "rate_limit:";

    @Autowired
    public RateLimitingFilter(@Value("${gateway.rate-limit.requests-per-minute:100}") int maxRequestsPerMinute,
                              @Value("${gateway.rate-limit.burst-capacity:20}") int burstCapacity) {
        this.maxRequestsPerMinute = maxRequestsPerMinute;
        this.burstCapacity = burstCapacity;
    }

    public RateLimitingFilter(int maxRequestsPerMinute) {
        this.maxRequestsPerMinute = maxRequestsPerMinute;
        this.burstCapacity = 20;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String clientIp = getClientIp(request);

        if (clientIp == null) {
            return chain.filter(exchange);
        }

        long now = Instant.now().getEpochSecond();
        long minute = now / 60;
        String key = REDIS_KEY_PREFIX + clientIp + ":" + minute;

        Long resetTime = resetTimes.get(key);
        if (resetTime == null || resetTime <= now) {
            requestCounts.put(key, 0);
            resetTimes.put(key, now + 60);
        }

        int count = requestCounts.compute(key, (k, v) -> v == null ? 1 : v + 1);

        if (count > maxRequestsPerMinute + burstCapacity) {
            log.warn("Rate limit excedido para IP: {} ({} requests)", clientIp, count);
            return tooManyRequests(exchange, clientIp, count);
        }

        return chain.filter(exchange);
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
