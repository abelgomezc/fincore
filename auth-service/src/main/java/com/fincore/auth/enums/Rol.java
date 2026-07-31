package com.fincore.auth.enums;

/**
 * Roles de usuario en el sistema bancario FinCore.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum Rol {

    CLIENTE("Cliente", "Usuario cliente del banco"),
    OPERADOR("Operador", "Operador de backoffice"),
    SUPERVISOR("Supervisor", "Supervisor que puede aprobar/rechazar operaciones"),
    AUDITOR("Auditor", "Auditor que puede consultar registros"),
    ADMIN("Administrador", "Administrador del sistema");

    private final String nombre;
    private final String descripcion;

    Rol(String nombre, String descripcion) {
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
