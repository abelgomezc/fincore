package com.fincore.customer.enums;

/**
 * Estados posibles de un cliente en el sistema.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum EstadoCliente {

    ACTIVO("Activo", "Cliente activo y con acceso a servicios"),
    INACTIVO("Inactivo", "Cliente inactivo"),
    BLOQUEADO("Bloqueado", "Cliente bloqueado por sospecha de fraude"),
    SUSPENDIDO("Suspendido", "Cliente suspendido temporalmente");

    private final String nombre;
    private final String descripcion;

    EstadoCliente(String nombre, String descripcion) {
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
