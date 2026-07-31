package com.fincore.customer.exception;

/**
 * Excepción base de FinCore para Customer Service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public abstract class FinCoreException extends RuntimeException {

    protected FinCoreException(String message) {
        super(message);
    }

    protected FinCoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
