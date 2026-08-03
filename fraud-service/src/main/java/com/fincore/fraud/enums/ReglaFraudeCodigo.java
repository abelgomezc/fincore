package com.fincore.fraud.enums;

/**
 * Códigos de las 10 reglas de fraude.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum ReglaFraudeCodigo {

    MONTO_INUSUAL("MONTO_INUSUAL", 15),
    HORARIO_INUSUAL("HORARIO_INUSUAL", 10),
    DISPOSITIVO_NUEVO("DISPOSITIVO_NUEVO", 20),
    BENEFICIARIO_NUEVO("BENEFICIARIO_NUEVO", 15),
    PAIS_DIFERENTE("PAIS_DIFERENTE", 25),
    VELOCIDAD_ALTA("VELOCIDAD_ALTA", 30),
    LISTA_NEGRA("LISTA_NEGRA", 100),
    IP_SOSPECHOSA("IP_SOSPECHOSA", 40),
    PATRON_FRACCIONADO("PATRON_FRACCIONADO", 35),
    PRIMER_TRANSFER_GRANDE("PRIMER_TRANSFER_GRANDE", 20);

    private final String codigo;
    private final int puntos;

    ReglaFraudeCodigo(String codigo, int puntos) {
        this.codigo = codigo;
        this.puntos = puntos;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getPuntos() {
        return puntos;
    }
}
