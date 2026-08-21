package com.fincore.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación principal del document-service.
 *
 * Microservicio de generación de documentos y firma electrónica.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@SpringBootApplication
public class DocumentoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentoServiceApplication.class, args);
    }
}
