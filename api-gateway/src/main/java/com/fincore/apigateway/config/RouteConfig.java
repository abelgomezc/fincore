package com.fincore.apigateway.config;

import com.fincore.apigateway.filters.AuditLoggingFilter;
import com.fincore.apigateway.filters.JwtAuthenticationFilter;
import com.fincore.apigateway.filters.RateLimitingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de rutas del API Gateway.
 *
 * Define las rutas hacia todos los microservicios de FinCore.
 * Cada ruta está protegida por el filtro de JWT.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Configuration
public class RouteConfig {

    @Value("${gateway.rate-limit.requests-per-minute:100}")
    private int rateLimitPerMinute;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           JwtAuthenticationFilter jwtFilter) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("http://localhost:8081"))

                .route("customer-service", r -> r
                        .path("/api/clientes/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("http://localhost:8082"))

                .route("account-service", r -> r
                        .path("/api/cuentas/**", "/api/saldos/**", "/api/beneficiarios/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("http://localhost:8083"))

                .route("ledger-service", r -> r
                        .path("/api/ledger/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("http://localhost:8084"))

                .route("transfer-service", r -> r
                        .path("/api/transferencias/**", "/ws/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("http://localhost:8092"))

                .route("fraud-service", r -> r
                        .path("/api/fraude/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("http://localhost:8090"))

                .route("notification-service", r -> r
                        .path("/api/notificaciones/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("http://localhost:8085"))

                .route("audit-service", r -> r
                        .path("/api/audit/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("http://localhost:8091"))

                .route("batch-service", r -> r
                        .path("/api/batch/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("http://localhost:8094"))

                .route("backoffice-service", r -> r
                        .path("/api/backoffice/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("http://localhost:8093"))

                .build();
    }
}
