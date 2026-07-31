package com.fincore.customer.exception;

/**
 * Excepción lanzada cuando un cliente no es encontrado.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class ClienteNoEncontradoException extends BankingBusinessException {

    public ClienteNoEncontradoException(String message) {
        super(message);
    }

    public ClienteNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
