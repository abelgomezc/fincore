package com.fincore.customer.enums;

/**
 * Tipos de cliente en el sistema bancario.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum TipoCliente {

    NATURAL("Persona natural", "Cliente persona física"),
    JURIDICA("Persona jurídica", "Cliente persona moral");

    private final String nombre;
    private final String descripcion;

    TipoCliente(String nombre, String descripcion) {
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
