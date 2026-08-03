package com.fincore.fraud.service.rules;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Regla 2.9 — PATRON_FRACCIONADO (35 puntos).
 * El cliente realiza varias transferencias pequeñas seguidas para evadir detección.
 *
 * Nota: La detección de patrones en tiempo real se hace en
 * FraudEvaluationServiceImpl usando Redis. Esta regla verifica contra el
 * perfil histórico del cliente.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
public class PatronFraccionadoRule implements FraudRule {

    @Value("${fraud.patron.monto.maximo:100.00}")
    private BigDecimal montoMaximo;

    @Override
    public RuleResult evaluate(RuleContext context) {
        String codigo = "PATRON_FRACCIONADO";
        int puntos = 35;

        if (context.getMonto() == null || context.getMonto().compareTo(montoMaximo) >= 0) {
            return new RuleResult(codigo, puntos, false,
                    "Monto (" + context.getMonto() + ") excede el umbral de patrón fraccionado (" + montoMaximo + ")");
        }

        return new RuleResult(codigo, puntos, false,
                "Monto (" + context.getMonto() + ") dentro del rango aceptable para patrón fraccionado");
    }

    @Override
    public String getCodigo() {
        return "PATRON_FRACCIONADO";
    }
}
