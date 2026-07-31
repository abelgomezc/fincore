package com.fincore.ledger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Configuración de auditoría JPA para Ledger Service.
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
                    return Optional.of(context.getAuthentication().getName());
                }
            } catch (Exception e) {
                // No hay contexto de seguridad
            }
            return Optional.of("system");
        };
    }
}
