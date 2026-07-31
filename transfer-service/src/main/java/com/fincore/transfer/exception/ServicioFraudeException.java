package com.fincore.transfer.exception;

/**
 * Excepción para fallos en el motor de fraude (timeout, error de conexión).
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class ServicioFraudeException extends TransferenciaException {

    public ServicioFraudeException(String mensaje, Throwable cause) {
        super("Error en servicio de fraude: " + mensaje, "SERVICIO_FRAUDE_ERROR", cause);
    }
}
