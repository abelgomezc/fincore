package com.fincore.fraud.service.rules;

import org.springframework.stereotype.Component;

/**
 * Regla 2.4 — BENEFICIARIO_NUEVO (15 puntos).
 * La cuenta destino no está en la lista de beneficiarios habituales del cliente.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
public class BeneficiarioNuevoRule implements FraudRule {

    @Override
    public RuleResult evaluate(RuleContext context) {
        String codigo = "BENEFICIARIO_NUEVO";
        int puntos = 15;

        if (context.getNumeroCuentaDestino() == null || context.getNumeroCuentaDestino().isBlank()) {
            return new RuleResult(codigo, puntos, false, "Sin número de cuenta destino para comparar");
        }

        if (context.getPerfilOpt().isEmpty()) {
            return new RuleResult(codigo, puntos, true,
                    "Sin perfil transaccional — beneficiario no evaluado");
        }

        String paisesHabituales = context.getPerfilOpt().get().getPaisesHabituales();
        if (paisesHabituales == null || paisesHabituales.isBlank()) {
            return new RuleResult(codigo, puntos, true,
                    "Sin beneficiarios habituales registrados");
        }

        if (paisesHabituales.contains(context.getNumeroCuentaDestino())) {
            return new RuleResult(codigo, puntos, false, "Beneficiario conocido en perfil");
        }

        return new RuleResult(codigo, puntos, true,
                "Beneficiario '" + context.getNumeroCuentaDestino() + "' no reconocido en perfil");
    }

    @Override
    public String getCodigo() {
        return "BENEFICIARIO_NUEVO";
    }
}
