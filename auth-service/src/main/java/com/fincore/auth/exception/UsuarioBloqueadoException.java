package com.fincore.auth.exception;

/**
 * Excepción lanzada cuando un usuario está bloqueado.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class UsuarioBloqueadoException extends BankingBusinessException {

    public UsuarioBloqueadoException(String message) {
        super(message);
    }

    public UsuarioBloqueadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
