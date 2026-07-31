package com.fincore.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Server — Service Discovery para FinCore.
 *
 * Coordina el registro y descubrimiento de todos los microservicios
 * del ecosistema bancario FinCore.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
