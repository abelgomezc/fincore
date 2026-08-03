package com.fincore.notification.exception;

/**
 * Excepción base de notification-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class NotificationException extends RuntimeException {
    public NotificationException(String message) {
        super(message);
    }

    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
