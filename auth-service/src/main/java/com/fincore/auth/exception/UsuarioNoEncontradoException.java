package com.fincore.auth.exception;

/**
 * Excepción lanzada cuando un usuario no es encontrado.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class UsuarioNoEncontradoException extends BankingBusinessException {

    public UsuarioNoEncontradoException(String message) {
        super(message);
    }

    public UsuarioNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
