package com.fincore.account.enums;

/**
 * Tipos de cuenta bancaria.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum TipoCuentaEnum {

    CORRIENTE("Cuenta Corriente", "CA", "Cuenta de uso diario con chequera"),
    AHORROS("Cuenta Ahorros", "CC", "Cuenta de ahorros con rendimiento mensual"),
    PLAZO_FIJO("Plazo Fijo", "PF", "Cuenta con plazo fijo y tasa fija");

    private final String nombre;
    private final String codigo;
    private final String descripcion;

    TipoCuentaEnum(String nombre, String codigo, String descripcion) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
