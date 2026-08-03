package com.fincore.fraud.service.rules;

import com.fincore.fraud.entity.PerfilTransaccional;

/**
 * Interface para una regla de fraude individual.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface FraudRule {

    /**
     * Evalúa la regla contra el contexto de la transferencia.
     *
     * @param context Contexto con los datos de la transferencia
     * @return resultado de la regla (activada o no, con puntos y descripción)
     */
    RuleResult evaluate(RuleContext context);

    /**
     * Devuelve el código único de la regla.
     */
    String getCodigo();
}
