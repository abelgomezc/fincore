package com.fincore.document.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * Manejador global de excepciones del document-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DocumentoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleDocumentoNoEncontrado(DocumentoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "DOCUMENTO_NO_ENCONTRADO", "mensaje", ex.getMessage()));
    }

    @ExceptionHandler(PlantillaNoEncontradaException.class)
    public ResponseEntity<Map<String, String>> handlePlantillaNoEncontrada(PlantillaNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "PLANTILLA_NO_ENCONTRADA", "mensaje", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "ERROR_INTERNO", "mensaje", ex.getMessage()));
    }
}
