package com.fincore.apigateway.config;

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
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service — login, register, refresh
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f
                                .filter(new com.fincore.apigateway.filters.AuditLoggingFilter())
                                .filter(new com.fincore.apigateway.filters.JwtAuthenticationFilter())
                                .filter(new com.fincore.apigateway.filters.RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://AUTH-SERVICE"))

                // Customer Service — clientes, KYC, AML
                .route("customer-service", r -> r
                        .path("/api/clientes/**")
                        .filters(f -> f
                                .filter(new com.fincore.apigateway.filters.AuditLoggingFilter())
                                .filter(new com.fincore.apigateway.filters.JwtAuthenticationFilter())
                                .filter(new com.fincore.apigateway.filters.RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://CUSTOMER-SERVICE"))

                // Account Service — cuentas, saldos, beneficiarios
                .route("account-service", r -> r
                        .path("/api/cuentas/**", "/api/saldos/**", "/api/beneficiarios/**")
                        .filters(f -> f
                                .filter(new com.fincore.apigateway.filters.AuditLoggingFilter())
                                .filter(new com.fincore.apigateway.filters.JwtAuthenticationFilter())
                                .filter(new com.fincore.apigateway.filters.RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://ACCOUNT-SERVICE"))

                // Ledger Service — asientos contables, extractos
                .route("ledger-service", r -> r
                        .path("/api/ledger/**")
                        .filters(f -> f
                                .filter(new com.fincore.apigateway.filters.AuditLoggingFilter())
                                .filter(new com.fincore.apigateway.filters.JwtAuthenticationFilter())
                                .filter(new com.fincore.apigateway.filters.RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://LEDGER-SERVICE"))

                // Transfer Service — transferencias, sagas, WebSocket
                .route("transfer-service", r -> r
                        .path("/api/transferencias/**", "/ws/**")
                        .filters(f -> f
                                .filter(new com.fincore.apigateway.filters.AuditLoggingFilter())
                                .filter(new com.fincore.apigateway.filters.JwtAuthenticationFilter())
                                .filter(new com.fincore.apigateway.filters.RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://TRANSFER-SERVICE"))

                // Fraud Service — evaluación, lista negra, perfiles
                .route("fraud-service", r -> r
                        .path("/api/fraude/**")
                        .filters(f -> f
                                .filter(new com.fincore.apigateway.filters.AuditLoggingFilter())
                                .filter(new com.fincore.apigateway.filters.JwtAuthenticationFilter())
                                .filter(new com.fincore.apigateway.filters.RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://FRAUD-SERVICE"))

                // Notification Service — email, push
                .route("notification-service", r -> r
                        .path("/api/notificaciones/**")
                        .filters(f -> f
                                .filter(new com.fincore.apigateway.filters.AuditLoggingFilter())
                                .filter(new com.fincore.apigateway.filters.JwtAuthenticationFilter())
                                .filter(new com.fincore.apigateway.filters.RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://NOTIFICATION-SERVICE"))

                // Audit Service — auditoría, trazabilidad
                .route("audit-service", r -> r
                        .path("/api/audit/**")
                        .filters(f -> f
                                .filter(new com.fincore.apigateway.filters.AuditLoggingFilter())
                                .filter(new com.fincore.apigateway.filters.JwtAuthenticationFilter())
                                .filter(new com.fincore.apigateway.filters.RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://AUDIT-SERVICE"))

                // Batch Service — conciliación, intereses, reportes
                .route("batch-service", r -> r
                        .path("/api/batch/**")
                        .filters(f -> f
                                .filter(new com.fincore.apigateway.filters.AuditLoggingFilter())
                                .filter(new com.fincore.apigateway.filters.JwtAuthenticationFilter())
                                .filter(new com.fincore.apigateway.filters.RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://BATCH-SERVICE"))

                // Backoffice Service — portal de empleados
                .route("backoffice-service", r -> r
                        .path("/api/backoffice/**")
                        .filters(f -> f
                                .filter(new com.fincore.apigateway.filters.AuditLoggingFilter())
                                .filter(new com.fincore.apigateway.filters.JwtAuthenticationFilter())
                                .filter(new com.fincore.apigateway.filters.RateLimitingFilter(rateLimitPerMinute))
                        )
                        .uri("lb://BACKOFFICE-SERVICE"))

                .build();
    }
}
