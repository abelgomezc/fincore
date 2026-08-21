package com.fincore.loan.exception;

/**
 * Excepción de negocio para solicitudes de préstamo.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class SolicitudPrestamoNoEncontradaException extends RuntimeException {
    public SolicitudPrestamoNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
