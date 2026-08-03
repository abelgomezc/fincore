package com.fincore.batch.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones para batch-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BatchException.class)
    public ResponseEntity<String> handle(BatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
    }
}
