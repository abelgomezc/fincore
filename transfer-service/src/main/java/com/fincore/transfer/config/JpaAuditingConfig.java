package com.fincore.transfer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuración de auditoría JPA para Transfer Service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            try {
                org.springframework.security.core.context.SecurityContext context =
                        org.springframework.security.core.context.SecurityContextHolder.getContext();
                if (context != null && context.getAuthentication() != null) {
                    return java.util.Optional.of(context.getAuthentication().getName());
                }
            } catch (Exception e) {
                // Sin contexto de seguridad
            }
            return java.util.Optional.of("system");
        };
    }
}
