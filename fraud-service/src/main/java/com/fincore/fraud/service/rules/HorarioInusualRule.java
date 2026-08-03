package com.fincore.fraud.service.rules;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * Regla 2.2 — HORARIO_INUSUAL (10 puntos).
 * La transferencia ocurre en un horario de riesgo (00:00 - 05:00).
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
public class HorarioInusualRule implements FraudRule {

    @Value("${fraud.horario.inicio:0}")
    private int inicio;

    @Value("${fraud.horario.fin:5}")
    private int fin;

    @Override
    public RuleResult evaluate(RuleContext context) {
        String codigo = "HORARIO_INUSUAL";
        int puntos = 10;

        LocalTime ahora = LocalTime.now();
        int hora = ahora.getHour();

        if (hora >= inicio && hora <= fin) {
            return new RuleResult(codigo, puntos, true,
                    "Transferencia realizada en horario de riesgo (" + hora + ":00)");
        }

        return new RuleResult(codigo, puntos, false,
                "Transferencia fuera de horario de riesgo (hora=" + hora + ")");
    }

    @Override
    public String getCodigo() {
        return "HORARIO_INUSUAL";
    }
}
