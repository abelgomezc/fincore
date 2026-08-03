package com.fincore.fraud.enums;

/**
 * Decisiones posibles del motor antifraude.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum DecisionFraude {
    APROBADO,
    EN_REVISION,
    RECHAZADO;

    public static DecisionFraude fromString(String value) {
        if (value == null) {
            return APROBADO;
        }
        try {
            return DecisionFraude.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return APROBADO;
        }
    }
}
