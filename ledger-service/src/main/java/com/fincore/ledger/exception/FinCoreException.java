package com.fincore.ledger.exception;

/**
 * Excepción base de FinCore para Ledger Service.
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
