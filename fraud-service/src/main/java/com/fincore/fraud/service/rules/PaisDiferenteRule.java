package com.fincore.fraud.service.rules;

import org.springframework.stereotype.Component;

/**
 * Regla 2.5 — PAIS_DIFERENTE (25 puntos).
 * La IP de origen está en un país diferente al habitual para el cliente.
 *
 * Nota: La geolocalización real se hace en la fase de evaluación completa
 * en FraudEvaluationServiceImpl. Esta regla verifica contra el perfil.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
public class PaisDiferenteRule implements FraudRule {

    @Override
    public RuleResult evaluate(RuleContext context) {
        String codigo = "PAIS_DIFERENTE";
        int puntos = 25;

        if (context.getIpOrigen() == null || context.getIpOrigen().isBlank()) {
            return new RuleResult(codigo, puntos, false, "Sin IP de origen");
        }

        if (context.getPerfilOpt().isEmpty() || context.getPerfilOpt().get().getPaisesHabituales() == null) {
            return new RuleResult(codigo, puntos, true,
                    "Sin perfil de países habituales — IP nueva no verificable");
        }

        String paisesHabituales = context.getPerfilOpt().get().getPaisesHabituales();
        if (paisesHabituales.isBlank()) {
            return new RuleResult(codigo, puntos, true, "Lista de países habituales vacía");
        }

        return new RuleResult(codigo, puntos, false,
                "IP origen: " + context.getIpOrigen() + " — verificación de país pendiente en fase de geolocalización");
    }

    @Override
    public String getCodigo() {
        return "PAIS_DIFERENTE";
    }
}
