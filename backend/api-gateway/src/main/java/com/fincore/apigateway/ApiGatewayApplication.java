package com.fincore.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/v1/auth/**").uri("lb://AUTH-SERVICE"))
                .route("customer-service", r -> r.path("/api/v1/clientes/**").uri("lb://CUSTOMER-SERVICE"))
                .route("account-service", r -> r.path("/api/v1/cuentas/**").uri("lb://ACCOUNT-SERVICE"))
                .route("ledger-service", r -> r.path("/api/v1/ledger/**").uri("lb://LEDGER-SERVICE"))
                .route("transfer-service", r -> r.path("/api/v1/transferencias/**").uri("lb://TRANSFER-SERVICE"))
                .route("fraud-service", r -> r.path("/api/v1/fraude/**").uri("lb://FRAUD-SERVICE"))
                .route("notification-service", r -> r.path("/api/v1/notificaciones/**").uri("lb://NOTIFICATION-SERVICE"))
                .route("audit-service", r -> r.path("/api/v1/auditoria/**").uri("lb://AUDIT-SERVICE"))
                .route("batch-service", r -> r.path("/api/v1/batch/**").uri("lb://BATCH-SERVICE"))
                .route("reporting-service", r -> r.path("/api/v1/reportes/**").uri("lb://REPORTING-SERVICE"))
                .build();
    }
}
