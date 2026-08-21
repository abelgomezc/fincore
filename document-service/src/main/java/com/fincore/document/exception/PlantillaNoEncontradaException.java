package com.fincore.document.exception;

/**
 * Excepción de negocio para plantillas de documentos.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class PlantillaNoEncontradaException extends RuntimeException {
    public PlantillaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
