package com.fincore.ledger.enums;

/**
 * Tipos de cuentas del plan contable.
 *
 *ACTIVOS (1xxx) — lo que el banco TIENE
 * PASIVOS (2xxx) — lo que el banco DEBE a clientes
 * PATRIMONIO (3xxx) — el capital del banco
 * INGRESOS (4xxx) — ingresos operativos
 * GASTOS (5xxx) — gastos operativos
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum TipoCuentaContable {

    ACTIVO("Activo", "Lo que el banco tiene", "DEUDORA", "1xxx"),
    PASIVO("Pasivo", "Lo que el banco debe a clientes", "ACREEDORA", "2xxx"),
    PATRIMONIO("Patrimonio", "Capital y reservas del banco", "ACREEDORA", "3xxx"),
    INGRESO("Ingreso", "Ingresos operativos", "DEUDORA", "4xxx"),
    GASTO("Gasto", "Gastos operativos", "ACREEDORA", "5xxx");

    private final String nombre;
    private final String descripcion;
    private final String naturalezaDefault;
    private final String prefijoCodigo;

    TipoCuentaContable(String nombre, String descripcion, String naturalezaDefault, String prefijoCodigo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.naturalezaDefault = naturalezaDefault;
        this.prefijoCodigo = prefijoCodigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public NaturalezaCuenta getNaturalezaDefault() {
        return NaturalezaCuenta.valueOf(naturalezaDefault);
    }

    public String getPrefijoCodigo() {
        return prefijoCodigo;
    }
}
