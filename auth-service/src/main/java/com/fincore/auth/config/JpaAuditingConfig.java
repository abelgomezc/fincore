package com.fincore.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Configuración de auditoría JPA.
 *
 * Habilita @CreatedDate, @LastModifiedDate, @CreatedBy, @LastModifiedBy.
 * El auditor actual se obtiene del contexto de seguridad o del header
 * X-User-Email enviado por el API Gateway.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            // El usuario actual viene del header X-User-Email
            // o del contexto de seguridad durante login/register
            try {
                org.springframework.security.core.context.SecurityContext context =
                        org.springframework.security.core.context.SecurityContextHolder.getContext();
                if (context != null && context.getAuthentication() != null
                        && context.getAuthentication().isAuthenticated()
                        && !"anonymousUser".equals(context.getAuthentication().getName())) {
                    return Optional.of(context.getAuthentication().getName());
                }
            } catch (Exception e) {
                // No hay contexto de seguridad — usar "system"
            }
            return Optional.of("system");
        };
    }
}
