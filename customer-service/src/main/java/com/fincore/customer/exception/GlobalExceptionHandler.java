package com.fincore.customer.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para Customer Service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleClienteNoEncontrado(ClienteNoEncontradoException e) {
        log.warn("Cliente no encontrado: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "CLIENTE_NO_ENCONTRADO", e.getMessage());
    }

    @ExceptionHandler(ClienteBloqueadoException.class)
    public ResponseEntity<ErrorResponse> handleClienteBloqueado(ClienteBloqueadoException e) {
        log.warn("Cliente bloqueado: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.LOCKED, "CLIENTE_BLOQUEADO", e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException e) {
        log.warn("Violación de integridad: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, "CONFLICTO_DATOS",
                "Ya existe un registro con los mismos datos");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Argumento inválido: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "ARGUMENTO_INVALIDO", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("Error de validación: {}", errors);
        return ResponseEntity.badRequest().body(ValidationErrorResponse.builder()
                .error("VALIDATION_ERROR")
                .message("Error de validación en los campos")
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        log.error("Error inesperado: {}", e.getMessage(), e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO",
                "Error interno del servidor");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String error, String message) {
        ErrorResponse response = ErrorResponse.builder()
                .error(error)
                .message(message)
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, status);
    }

    @lombok.Getter
    @lombok.Setter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ErrorResponse {
        private String error;
        private String message;
        private int status;
        private LocalDateTime timestamp;
    }

    @lombok.Getter
    @lombok.Setter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ValidationErrorResponse {
        private String error;
        private String message;
        private Map<String, String> errors;
        private LocalDateTime timestamp;
    }
}
