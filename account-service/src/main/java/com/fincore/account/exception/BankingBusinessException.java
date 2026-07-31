package com.fincore.account.exception;

/**
 * Excepción de negocio bancario para Account Service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public abstract class BankingBusinessException extends FinCoreException {

    protected BankingBusinessException(String message) {
        super(message);
    }

    protected BankingBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
