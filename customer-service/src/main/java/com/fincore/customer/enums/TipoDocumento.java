package com.fincore.customer.enums;

/**
 * Tipos de documentos de identidad aceptados.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum TipoDocumento {

    CEDULA("Cédula", "Documento de identidad ciudadana"),
    PASAPORTE("Pasaporte", "Documento de viaje"),
    RUC("RUC", "Registro Único de Contribuyentes"),
    LICENCIA_CONDUCIR("Licencia de conducir", "Permiso de conducir");

    private final String nombre;
    private final String descripcion;

    TipoDocumento(String nombre, String descripcion) {
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
