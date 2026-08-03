package com.fincore.fraud.service.rules;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Regla 2.6 — VELOCIDAD_ALTA (30 puntos).
 * El cliente realiza más transacciones de las esperadas en un corto período.
 *
 * Nota: La verificación en tiempo real con contador Redis se hace en
 * FraudEvaluationServiceImpl. Esta regla es un stub para la estructura.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
public class VelocidadAltaRule implements FraudRule {

    @Value("${fraud.velocity.max.transactions.per.hour:10}")
    private int maxTransacciones;

    @Value("${fraud.velocity.window.minutes:60}")
    private int ventanaMinutos;

    @Override
    public RuleResult evaluate(RuleContext context) {
        String codigo = "VELOCIDAD_ALTA";
        int puntos = 30;

        return new RuleResult(codigo, puntos, false,
                "Velocidad dentro de límites (máx=" + maxTransacciones + "/ " + ventanaMinutos + "min)");
    }

    @Override
    public String getCodigo() {
        return "VELOCIDAD_ALTA";
    }
}
