package com.fincore.apigateway.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

/**
 * Filtro de auditoría — registra TODA petición que pasa por el gateway.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class AuditLoggingFilter implements GatewayFilter, Ordered {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String AUDIT_TOPIC = "audit.events";

    public AuditLoggingFilter() {
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();

        String traceId = request.getHeaders().getFirst("X-Trace-Id");
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        String traceIdFinal = traceId;

        String clientIp = request.getHeaders().getFirst("X-Forwarded-For");
        if (clientIp == null) {
            clientIp = request.getRemoteAddress() != null
                    ? request.getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
        }

        String userId = request.getHeaders().getFirst("X-User-Id");
        String userRol = request.getHeaders().getFirst("X-User-Rol");
        String deviceId = request.getHeaders().getFirst("X-Device-Id");
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        String clientIpFinal = clientIp;

        String service = extractServiceFromPath(path);

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    long durationMs = System.currentTimeMillis() - startTime;
                    int statusCode = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : 200;

                    String result = isSuccess(statusCode) ? "EXITOSO" : "FALLIDO";

                    log.debug("Auditoría: {} {} -> {} ({}ms) traceId={} IP={}",
                            method, path, statusCode, durationMs, traceIdFinal, clientIpFinal);
                }));
    }

    private String extractServiceFromPath(String path) {
        if (path.startsWith("/api/auth")) return "AUTH-SERVICE";
        if (path.startsWith("/api/clientes")) return "CUSTOMER-SERVICE";
        if (path.startsWith("/api/cuentas") || path.startsWith("/api/saldos") || path.startsWith("/api/beneficiarios")) return "ACCOUNT-SERVICE";
        if (path.startsWith("/api/ledger")) return "LEDGER-SERVICE";
        if (path.startsWith("/api/transferencias") || path.startsWith("/ws")) return "TRANSFER-SERVICE";
        if (path.startsWith("/api/fraude")) return "FRAUD-SERVICE";
        if (path.startsWith("/api/notificaciones")) return "NOTIFICATION-SERVICE";
        if (path.startsWith("/api/audit")) return "AUDIT-SERVICE";
        if (path.startsWith("/api/batch")) return "BATCH-SERVICE";
        if (path.startsWith("/api/backoffice")) return "BACKOFFICE-SERVICE";
        return "UNKNOWN";
    }

    private boolean isSuccess(int statusCode) {
        return statusCode >= 200 && statusCode < 400;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 300;
    }
}
