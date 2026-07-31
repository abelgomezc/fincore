package com.fincore.account.enums;

/**
 * Estados posibles de una cuenta bancaria.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum EstadoCuenta {

    ACTIVA("Activa", "Cuenta activa y operativa"),
    BLOQUEADA("Bloqueada", "Cuenta bloqueada por seguridad"),
    CONGELADA("Congelada", "Cuenta congelada por orden judicial"),
    CERRADA("Cerrada", "Cuenta cerrada definitivamente");

    private final String nombre;
    private final String descripcion;

    EstadoCuenta(String nombre, String descripcion) {
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
