package com.fincore.ledger.enums;

/**
 * Tipos de movimiento contable.
 *
 * En contabilidad de doble partida, cada línea de asiento es
 * o un DÉBITO o un CRÉDITO.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum TipoMovimientoContable {

    DEBITO("Débito", "Aumento de cuentas deudoras, disminución de cuentas acreedoras"),
    CREDITO("Crédito", "Aumento de cuentas acreedoras, disminución de cuentas deudoras");

    private final String nombre;
    private final String descripcion;

    TipoMovimientoContable(String nombre, String descripcion) {
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
