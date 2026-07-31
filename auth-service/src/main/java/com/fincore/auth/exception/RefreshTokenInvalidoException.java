package com.fincore.auth.exception;

/**
 * Excepción lanzada cuando un refresh token es inválido, expirado o revocado.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class RefreshTokenInvalidoException extends BankingBusinessException {

    public RefreshTokenInvalidoException(String message) {
        super(message);
    }

    public RefreshTokenInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
