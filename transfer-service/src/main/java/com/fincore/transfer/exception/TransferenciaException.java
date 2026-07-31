package com.fincore.transfer.exception;

/**
 * Excepción base del servicio de transferencias.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class TransferenciaException extends RuntimeException {

    private final String codigoError;

    public TransferenciaException(String mensaje) {
        super(mensaje);
        this.codigoError = "TRANSF_ERROR";
    }

    public TransferenciaException(String mensaje, String codigoError) {
        super(mensaje);
        this.codigoError = codigoError;
    }

    public TransferenciaException(String mensaje, Throwable cause) {
        super(mensaje, cause);
        this.codigoError = "TRANSF_ERROR";
    }

    public TransferenciaException(String mensaje, String codigoError, Throwable cause) {
        super(mensaje, cause);
        this.codigoError = codigoError;
    }

    public String getCodigoError() {
        return codigoError;
    }
}
