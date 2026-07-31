package com.fincore.transfer.enums;

/**
 * Los 12 pasos del Saga Orquestado para transferencias.
 *
 * Cada paso es ejecutado secuencialmente. Si un paso falla,
 * se ejecutan compensating transactions en orden inverso.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum PasoSaga {

    VALIDAR_DATOS("VALIDAR_DATOS", 1, "Verificar formato, cuentas, montos"),
    VERIFICAR_KYC("VERIFICAR_KYC", 2, "Verificar documentos vigentes y KYC aprobado"),
    VALIDAR_LIMITES("VALIDAR_LIMITES", 3, "Verificar límites diarios, mensuales, por transacción"),
    EVALUAR_FRAUDE("EVALUAR_FRAUDE", 4, "Evaluar score de riesgo con motor antifraude"),
    RESERVAR_FONDOS("RESERVAR_FONDOS", 5, "Reservar fondos en cuenta origen"),
    CREAR_EVENTO_KAFKA("CREAR_EVENTO_KAFKA", 6, "Publicar evento transferencia.iniciada"),
    EJECUTAR_DEBITO("EJECUTAR_DEBITO", 7, "Crear asiento débito en ledger"),
    EJECUTAR_CREDITO("EJECUTAR_CREDITO", 8, "Crear asiento crédito en ledger"),
    LIBERAR_RETENCION("LIBERAR_RETENCION", 9, "Liberar fondos reservados"),
    REGISTRAR_AUDITORIA("REGISTRAR_AUDITORIA", 10, "Guardar registro completo en audit-service"),
    COBRAR_COMISION("COBRAR_COMISION", 11, "Cobrar comisión si aplica"),
    NOTIFICAR("NOTIFICAR", 12, "Notificar a ambas partes (email + push + Kafka)");

    private final String codigo;
    private final int orden;
    private final String descripcion;

    PasoSaga(String codigo, int orden, String descripcion) {
        this.codigo = codigo;
        this.orden = orden;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getOrden() {
        return orden;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
