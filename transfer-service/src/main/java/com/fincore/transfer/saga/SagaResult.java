package com.fincore.transfer.saga;

/**
 * Resultado de la ejecución de la saga.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class SagaResult {

    private final boolean exito;
    private final String mensaje;
    private final com.fincore.transfer.enums.EstadoTransferencia estadoFinal;
    private final Exception exception;

    private SagaResult(boolean exito, String mensaje, com.fincore.transfer.enums.EstadoTransferencia estadoFinal, Exception exception) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.estadoFinal = estadoFinal;
        this.exception = exception;
    }

    public static SagaResult success() {
        return new SagaResult(true, "Saga completada exitosamente",
                com.fincore.transfer.enums.EstadoTransferencia.COMPLETADA, null);
    }

    public static SagaResult error(String mensaje, com.fincore.transfer.enums.EstadoTransferencia estado, Exception ex) {
        return new SagaResult(false, mensaje, estado, ex);
    }

    public boolean isExito() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public com.fincore.transfer.enums.EstadoTransferencia getEstadoFinal() {
        return estadoFinal;
    }

    public Exception getException() {
        return exception;
    }
}
