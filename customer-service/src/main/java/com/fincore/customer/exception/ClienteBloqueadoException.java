package com.fincore.customer.exception;

/**
 * Excepción lanzada cuando un cliente está bloqueado.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class ClienteBloqueadoException extends BankingBusinessException {

    public ClienteBloqueadoException(String message) {
        super(message);
    }

    public ClienteBloqueadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
