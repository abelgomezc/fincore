package com.fincore.transfer.exception;

/**
 * Excepción para cuando la cuenta origen o destino no existe o está inactiva.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class CuentaNoEncontradaException extends TransferenciaException {

    public CuentaNoEncontradaException(Long idCuenta) {
        super("Cuenta no encontrada o inactiva: " + idCuenta, "CUENTA_NO_ENCONTRADA");
    }
}
