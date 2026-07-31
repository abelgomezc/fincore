package com.fincore.ledger.exception;

import lombok.extern.slf4j.Slf4j;
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
 * Manejador global de excepciones para Ledger Service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UnbalancedEntryException.class)
    public ResponseEntity<ErrorResponse> handleUnbalancedEntry(UnbalancedEntryException e) {
        log.error("Asiento no balanceado: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "ASIENTO_NO_BALANCEADO", e.getMessage());
    }

    @ExceptionHandler(ImmutableRecordException.class)
    public ResponseEntity<ErrorResponse> handleImmutableRecord(ImmutableRecordException e) {
        log.warn("Intento de modificar registro inmutable: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, "REGISTRO_INMUTABLE", e.getMessage());
    }

    @ExceptionHandler(CuentaContableNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleCuentaContableNoEncontrada(CuentaContableNoEncontradaException e) {
        log.warn("Cuenta contable no encontrada: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "CUENTA_CONTABLE_NO_ENCONTRADA", e.getMessage());
    }

    @ExceptionHandler(AsientoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleAsientoNoEncontrado(AsientoNoEncontradoException e) {
        log.warn("Asiento no encontrado: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "ASIENTO_NO_ENCONTRADO", e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
        log.error("Error de estado: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, "ERROR_ESTADO", e.getMessage());
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
