package com.fincore.batch.exception;

/**
 * Excepción base de batch-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class BatchException extends RuntimeException {
    public BatchException(String message) {
        super(message);
    }

    public BatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
