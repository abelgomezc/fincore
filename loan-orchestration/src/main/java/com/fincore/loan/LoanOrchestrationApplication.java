package com.fincore.loan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación principal del loan-orchestration-service.
 *
 * Microservicio de orquestación de préstamos en línea.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@SpringBootApplication
public class LoanOrchestrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanOrchestrationApplication.class, args);
    }
}
