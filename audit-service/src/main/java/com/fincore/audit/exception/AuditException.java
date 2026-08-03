package com.fincore.audit.exception;

/**
 * Excepción base de audit-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class AuditException extends RuntimeException {
    public AuditException(String message) {
        super(message);
    }

    public AuditException(String message, Throwable cause) {
        super(message, cause);
    }
}
