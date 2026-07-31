package com.fincore.ledger.exception;

/**
 * Excepción lanzada cuando se intenta modificar un asiento inmutable.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class ImmutableRecordException extends FinCoreException {

    public ImmutableRecordException(String message) {
        super(message);
    }

    public ImmutableRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}
