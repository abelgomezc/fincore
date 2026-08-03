package com.fincore.fraud.service.rules;

import com.fincore.fraud.entity.PerfilTransaccional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Regla 2.1 — MONTO_INUSUAL (15 puntos).
 * El monto es inusualmente alto comparado con el promedio histórico del cliente.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
public class MontoInusualRule implements FraudRule {

    @Value("${fraud.monto.multiplo.umbral:3.0}")
    private double umbral;

    public void setUmbral(double umbral) {
        this.umbral = umbral;
    }

    @Override
    public RuleResult evaluate(RuleContext context) {
        String codigo = "MONTO_INUSUAL";
        int puntos = 15;

        Optional<PerfilTransaccional> perfilOpt = context.getPerfilOpt();
        if (perfilOpt.isEmpty() || perfilOpt.get().getPromedioMonto30d() == null) {
            return new RuleResult(codigo, puntos, false, "Sin historial suficiente");
        }

        BigDecimal promedio = perfilOpt.get().getPromedioMonto30d();
        if (promedio.compareTo(BigDecimal.ZERO) <= 0) {
            return new RuleResult(codigo, puntos, false, "Promedio histórico es cero");
        }

        BigDecimal ratio = context.getMonto().divide(promedio, 2, BigDecimal.ROUND_HALF_UP);
        if (ratio.compareTo(BigDecimal.valueOf(umbral)) > 0) {
            return new RuleResult(codigo, puntos, true,
                    "Monto " + context.getMonto() + " es " + ratio + "x el promedio histórico " + promedio);
        }

        return new RuleResult(codigo, puntos, false,
                "Monto dentro del rango histórico (ratio=" + ratio + ")");
    }

    @Override
    public String getCodigo() {
        return "MONTO_INUSUAL";
    }
}
