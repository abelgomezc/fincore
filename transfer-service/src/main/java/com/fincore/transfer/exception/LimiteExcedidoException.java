package com.fincore.transfer.exception;

/**
 * Excepción para cuando se exceden los límites de transferencia.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class LimiteExcedidoException extends TransferenciaException {

    public LimiteExcedidoException(String tipoLimite, java.math.BigDecimal monto, java.math.BigDecimal limite) {
        super("El monto " + monto + " excede el límite " + tipoLimite + ": " + limite,
                "LIMITE_EXCEDIDO");
    }
}
