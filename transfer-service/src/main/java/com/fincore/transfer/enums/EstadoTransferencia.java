package com.fincore.transfer.enums;

/**
 * Los 10 estados de una transferencia en el sistema bancario.
 *
 * Representa el ciclo de vida completo de una transferencia,
 * desde su creación hasta su completitud o reversión.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum EstadoTransferencia {

    PENDIENTE("Pendiente", "Transformación creada, esperando inicio del saga"),
    VALIDANDO("Validando", "Validando datos, beneficiario, límites"),
    AUTORIZADA("Autorizada", "Paso validaciones, esperando antifraude"),
    EN_REVISION("En revisión", "Antifraude marcó para revisión manual"),
    RESERVANDO("Reservando", "Reservando fondos en cuenta origen"),
    PROCESANDO("Procesando", "Débito y crédito en proceso"),
    ACREDITANDO("Acreditando", "Acreditando en cuenta destino"),
    COMPLETADA("Completada", "Exitosa, ledger actualizado"),
    RECHAZADA("Rechazada", "Rechazada por validación o fraude"),
    REVERTIDA("Revertida", "Fue completada pero se revirtió después"),
    ERROR("Error", "Error técnico, compensating transaction activa");

    private final String nombre;
    private final String descripcion;

    EstadoTransferencia(String nombre, String descripcion) {
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
