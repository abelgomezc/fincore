package com.fincore.account.exception;

/**
 * Excepción lanzada cuando una cuenta no es encontrada.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class CuentaNoEncontradaException extends BankingBusinessException {

    public CuentaNoEncontradaException(String message) {
        super(message);
    }

    public CuentaNoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
}
