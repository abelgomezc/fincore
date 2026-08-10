package com.fincore.backoffice.enums;

/**
 * Estados posibles de un usuario del sistema backoffice.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum EstadoUsuarioSistema {

    ACTIVO("Activo", "Usuario activo y con acceso"),
    INACTIVO("Inactivo", "Usuario inactivo — no puede acceder"),
    SUSPENDIDO("Suspendido", "Usuario suspendido temporalmente"),
    ELIMINADO("Eliminado", "Usuario eliminado del sistema");

    private final String nombre;
    private final String descripcion;

    EstadoUsuarioSistema(String nombre, String descripcion) {
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
