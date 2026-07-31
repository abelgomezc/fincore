package com.fincore.auth.enums;

/**
 * Estados posibles de un usuario en el sistema.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum EstadoUsuario {

    ACTIVO("Activo", "Usuario activo y con acceso"),
    INACTIVO("Inactivo", "Usuario inactivo — no puede acceder"),
    BLOQUEADO("Bloqueado", "Usuario bloqueado por intentos fallidos o seguridad"),
    SUSPENDIDO("Suspendido", "Usuario suspendido temporalmente");

    private final String nombre;
    private final String descripcion;

    EstadoUsuario(String nombre, String descripcion) {
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
