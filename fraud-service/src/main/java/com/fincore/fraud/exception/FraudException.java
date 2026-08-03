package com.fincore.fraud.exception;

/**
 * Excepción base del motor antifraude.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class FraudException extends RuntimeException {

    public FraudException(String message) {
        super(message);
    }

    public FraudException(String message, Throwable cause) {
        super(message, cause);
    }
}
