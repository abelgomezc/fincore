package com.fincore.customer.enums;

/**
 * Estados del proceso de verificación KYC.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum EstadoKyc {

    PENDIENTE("Pendiente", "Verificación pendiente de iniciar"),
    EN_REVISION("En revisión", "Documentos en proceso de verificación manual"),
    APROBADO("Aprobado", "Cliente verificado correctamente"),
    RECHAZADO("Rechazado", "Verificación rechazada — documentos insuficientes");

    private final String nombre;
    private final String descripcion;

    EstadoKyc(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
