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
                        .uri("lb://AUTH-SERVICE"))

                .route("customer-service", r -> r
                        .path("/api/clientes/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://CUSTOMER-SERVICE"))

                .route("account-service", r -> r
                        .path("/api/cuentas/**", "/api/saldos/**", "/api/beneficiarios/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://ACCOUNT-SERVICE"))

                .route("ledger-service", r -> r
                        .path("/api/ledger/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://LEDGER-SERVICE"))

                .route("transfer-service", r -> r
                        .path("/api/transferencias/**", "/ws/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://TRANSFER-SERVICE"))

                .route("fraud-service", r -> r
                        .path("/api/fraude/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://FRAUD-SERVICE"))

                .route("notification-service", r -> r
                        .path("/api/notificaciones/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://NOTIFICATION-SERVICE"))

                .route("audit-service", r -> r
                        .path("/api/audit/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://AUDIT-SERVICE"))

                .route("batch-service", r -> r
                        .path("/api/batch/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://BATCH-SERVICE"))

                .route("backoffice-service", r -> r
                        .path("/api/backoffice/**")
                        .filters(f -> f
                                .filter(new AuditLoggingFilter())
                                .filter(jwtFilter)
                                .filter(new RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://BACKOFFICE-SERVICE"))

                .build();
    }
}
