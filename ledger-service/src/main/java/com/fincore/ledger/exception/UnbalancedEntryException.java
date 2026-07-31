package com.fincore.ledger.exception;

/**
 * Excepción lanzada cuando el ledger no está en equilibrio.
 * Los débitos y créditos deben ser exactamente iguales.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class UnbalancedEntryException extends FinCoreException {

    public UnbalancedEntryException(String message) {
        super(message);
    }

    public UnbalancedEntryException(String message, Throwable cause) {
        super(message, cause);
    }
}
