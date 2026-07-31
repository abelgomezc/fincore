package com.fincore.auth.exception;

/**
 * Excepción lanzada cuando las credenciales de autenticación son inválidas.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class CredencialesInvalidasException extends BankingBusinessException {

    public CredencialesInvalidasException(String message) {
        super(message);
    }

    public CredencialesInvalidasException(String message, Throwable cause) {
        super(message, cause);
    }
}
