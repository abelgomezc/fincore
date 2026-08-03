package com.fincore.backoffice.exception;

/**
 * Excepción base de backoffice-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class BackofficeException extends RuntimeException {
    public BackofficeException(String message) {
        super(message);
    }

    public BackofficeException(String message, Throwable cause) {
        super(message, cause);
    }
}
