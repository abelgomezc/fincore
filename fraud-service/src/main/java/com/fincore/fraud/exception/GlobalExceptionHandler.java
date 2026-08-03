package com.fincore.fraud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones para fraud-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FraudException.class)
    public ResponseEntity<String> handleFraudException(FraudException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(GeolocalizacionException.class)
    public ResponseEntity<String> handleGeolocalizacionException(GeolocalizacionException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
    }
}
