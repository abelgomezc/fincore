package com.fincore.ledger.enums;

/**
 * Naturaleza de las cuentas contables.
 *
 * - DEUDORA: aumenta con débitos, disminuye con créditos
 * - ACREEDORA: aumenta con créditos, disminuye con débitos
 *
 * La ecuación contable ACTIVOS = PASIVOS + PATRIMONIO siempre debe cumplirse.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum NaturalezaCuenta {

    DEUDORA("Deudora", "Aumenta con débitos, disminuye con créditos"),
    ACREEDORA("Acreedora", "Aumenta con créditos, disminuye con débitos");

    private final String nombre;
    private final String descripcion;

    NaturalezaCuenta(String nombre, String descripcion) {
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
