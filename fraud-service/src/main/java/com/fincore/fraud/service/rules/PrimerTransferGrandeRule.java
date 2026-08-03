package com.fincore.fraud.service.rules;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Regla 2.10 — PRIMER_TRANSFER_GRANDE (20 puntos).
 * El cliente realiza su primera transferencia y es de alto monto.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
public class PrimerTransferGrandeRule implements FraudRule {

    @Value("${fraud.primer.transfer.grande.umbral:1000.00}")
    private BigDecimal umbral;

    @Override
    public RuleResult evaluate(RuleContext context) {
        String codigo = "PRIMER_TRANSFER_GRANDE";
        int puntos = 20;

        if (context.getPerfilOpt().isPresent() && context.getPerfilOpt().get().getTotalTransferencias30d() != null
                && context.getPerfilOpt().get().getTotalTransferencias30d() > 0) {
            return new RuleResult(codigo, puntos, false,
                    "Cliente con historial — no es primera transferencia");
        }

        if (context.getMonto() == null || context.getMonto().compareTo(umbral) <= 0) {
            return new RuleResult(codigo, puntos, false,
                    "Monto (" + context.getMonto() + ") no supera el umbral de primer transfer (" + umbral + ")");
        }

        return new RuleResult(codigo, puntos, true,
                "Primera transferencia del cliente con monto elevado: " + context.getMonto());
    }

    @Override
    public String getCodigo() {
        return "PRIMER_TRANSFER_GRANDE";
    }
}
