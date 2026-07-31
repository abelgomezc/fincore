package com.fincore.account.exception;

/**
 * Excepción lanzada cuando una cuenta está bloqueada o no transferible.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class CuentaBloqueadaException extends BankingBusinessException {

    public CuentaBloqueadaException(String message) {
        super(message);
    }

    public CuentaBloqueadaException(String message, Throwable cause) {
        super(message, cause);
    }
}
