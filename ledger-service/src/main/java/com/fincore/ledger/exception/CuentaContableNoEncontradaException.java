package com.fincore.ledger.exception;

/**
 * Excepción lanzada cuando una cuenta contable no es encontrada.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class CuentaContableNoEncontradaException extends FinCoreException {

    public CuentaContableNoEncontradaException(String message) {
        super(message);
    }

    public CuentaContableNoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
}
