package com.fincore.batch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración de beans y scheduling para batch-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Configuration
@EnableScheduling
public class BatchConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
