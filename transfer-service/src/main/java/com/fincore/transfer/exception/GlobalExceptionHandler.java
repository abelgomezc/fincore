package com.fincore.transfer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para Transfer Service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TransferenciaException.class)
    public ResponseEntity<Map<String, Object>> handleTransferenciaException(TransferenciaException ex) {
        log.error("TransferenciaException: {}", ex.getMessage(), ex);

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("codigo", ex.getCodigoError());
        error.put("mensaje", ex.getMessage());
        error.put("status", HttpStatus.BAD_REQUEST.value());

        HttpStatus status = HttpStatus.BAD_REQUEST;
        if ("CUENTA_NO_ENCONTRADA".equals(ex.getCodigoError())) {
            status = HttpStatus.NOT_FOUND;
        }

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        log.error("SaldoInsuficienteException: {}", ex.getMessage(), ex);

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("codigo", ex.getCodigoError());
        error.put("mensaje", ex.getMessage());
        error.put("status", HttpStatus.PAYMENT_REQUIRED.value());

        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(error);
    }

    @ExceptionHandler(TransferenciaFraudeException.class)
    public ResponseEntity<Map<String, Object>> handleFraude(TransferenciaFraudeException ex) {
        log.error("TransferenciaFraudeException: {}", ex.getMessage(), ex);

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("codigo", ex.getCodigoError());
        error.put("mensaje", ex.getMessage());
        error.put("status", HttpStatus.FORBIDDEN.value());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(LimiteExcedidoException.class)
    public ResponseEntity<Map<String, Object>> handleLimiteExcedido(LimiteExcedidoException ex) {
        log.error("LimiteExcedidoException: {}", ex.getMessage(), ex);

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("codigo", ex.getCodigoError());
        error.put("mensaje", ex.getMessage());
        error.put("status", HttpStatus.FORBIDDEN.value());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(TransferenciaNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleNoEncontrada(TransferenciaNoEncontradaException ex) {
        log.error("TransferenciaNoEncontradaException: {}", ex.getMessage(), ex);

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("codigo", ex.getCodigoError());
        error.put("mensaje", ex.getMessage());
        error.put("status", HttpStatus.NOT_FOUND.value());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage(), ex);

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("codigo", "VALIDATION_ERROR");
        error.put("mensaje", "Error de validación");
        error.put("status", HttpStatus.BAD_REQUEST.value());

        java.util.List<String> detalles = new java.util.ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> {
            detalles.add(fe.getField() + ": " + fe.getDefaultMessage());
        });
        error.put("detalles", detalles);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("codigo", "ERROR_INTERNO");
        error.put("mensaje", "Error interno del servidor");
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
