package com.fincore.transfer.exception;

/**
 * Excepción para cuando la transferencia no se encuentra.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class TransferenciaNoEncontradaException extends TransferenciaException {

    public TransferenciaNoEncontradaException(Long id) {
        super("Transferencia no encontrada con ID: " + id, "TRANSF_NO_ENCONTRADA");
    }

    public TransferenciaNoEncontradaException(String numeroTransferencia) {
        super("Transferencia no encontrada con número: " + numeroTransferencia, "TRANSF_NO_ENCONTRADA");
    }
}
