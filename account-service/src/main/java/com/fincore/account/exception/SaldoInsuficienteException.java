package com.fincore.account.exception;

/**
 * Excepción lanzada cuando el saldo disponible es insuficiente.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class SaldoInsuficienteException extends BankingBusinessException {

    public SaldoInsuficienteException(String message) {
        super(message);
    }

    public SaldoInsuficienteException(String message, Throwable cause) {
        super(message, cause);
    }
}
