package com.fincore.transfer.exception;

/**
 * Excepción para cuando el saldo es insuficiente.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class SaldoInsuficienteException extends TransferenciaException {

    public SaldoInsuficienteException(Long idCuenta, java.math.BigDecimal monto) {
        super("Saldo insuficiente en cuenta " + idCuenta + " para monto: " + monto,
                "SALDO_INSUFICIENTE");
    }
}
