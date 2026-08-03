package com.fincore.notification.enums;

/**
 * Tipos de eventos de transferencia notificables.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum TipoNotificacion {
    TRANSFERENCIA_INICIADA("transferencia.iniciada", "Transferencia iniciada"),
    TRANSFERENCIA_COMPLETADA("transferencia.completada", "Transferencia completada"),
    TRANSFERENCIA_FALLIDA("transferencia.fallida", "Transferencia fallida"),
    TRANSFERENCIA_REVERTIDA("transferencia.revertida", "Transferencia revertida");

    private final String topicoKafka;
    private final String titulo;

    TipoNotificacion(String topicoKafka, String titulo) {
        this.topicoKafka = topicoKafka;
        this.titulo = titulo;
    }

    public String getTopicoKafka() {
        return topicoKafka;
    }

    public String getTitulo() {
        return titulo;
    }

    public static TipoNotificacion fromTopico(String topico) {
        for (TipoNotificacion tipo : values()) {
            if (tipo.getTopicoKafka().equals(topico)) {
                return tipo;
            }
        }
        return null;
    }
}
