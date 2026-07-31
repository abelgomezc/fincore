package com.fincore.ledger.exception;

/**
 * Excepción lanzada cuando un asiento no es encontrado.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class AsientoNoEncontradoException extends FinCoreException {

    public AsientoNoEncontradoException(String message) {
        super(message);
    }

    public AsientoNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
