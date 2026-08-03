package com.fincore.fraud.exception;

/**
 * Excepción lanzada cuando falla la geolocalización de IP.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class GeolocalizacionException extends FraudException {

    public GeolocalizacionException(String message) {
        super(message);
    }

    public GeolocalizacionException(String message, Throwable cause) {
        super(message, cause);
    }
}
