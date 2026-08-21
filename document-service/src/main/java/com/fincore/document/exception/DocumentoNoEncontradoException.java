package com.fincore.document.exception;

/**
 * Excepción de negocio para documentos.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class DocumentoNoEncontradoException extends RuntimeException {
    public DocumentoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
