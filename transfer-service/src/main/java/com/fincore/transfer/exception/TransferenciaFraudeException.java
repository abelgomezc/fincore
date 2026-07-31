package com.fincore.transfer.exception;

/**
 * Excepción para cuando la transferencia es rechazada por el motor de fraude.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class TransferenciaFraudeException extends TransferenciaException {

    public TransferenciaFraudeException(Long idTransferencia, int score, String decision) {
        super("Transferencia " + idTransferencia + " rechazada por fraude. Score: " + score + ", decisión: " + decision,
                "TRANSF_FRAUDE_RECHAZADA");
    }
}
