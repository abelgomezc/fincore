package com.fincore.fraud.service.rules;

import com.fincore.fraud.entity.ListaNegra;
import com.fincore.fraud.enums.TipoEntradaListaNegra;
import com.fincore.fraud.repository.ListaNegraRepository;
import org.springframework.stereotype.Component;

/**
 * Regla 2.8 — IP_SOSPECHOSA (40 puntos).
 * La IP de origen ha sido reportada como maliciosa.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
public class IpSospechosaRule implements FraudRule {

    private final ListaNegraRepository listaNegraRepository;

    public IpSospechosaRule(ListaNegraRepository listaNegraRepository) {
        this.listaNegraRepository = listaNegraRepository;
    }

    @Override
    public RuleResult evaluate(RuleContext context) {
        String codigo = "IP_SOSPECHOSA";
        int puntos = 40;

        if (context.getIpOrigen() == null || context.getIpOrigen().isBlank()) {
            return new RuleResult(codigo, puntos, false, "Sin IP de origen");
        }

        if (listaNegraRepository.existsByTipoAndValorAndEsActivoTrue(
                TipoEntradaListaNegra.IP.name(), context.getIpOrigen())) {
            return new RuleResult(codigo, puntos, true,
                    "IP reportada como maliciosa: " + context.getIpOrigen());
        }

        return new RuleResult(codigo, puntos, false,
                "IP no reportada en listas de sospecha");
    }

    @Override
    public String getCodigo() {
        return "IP_SOSPECHOSA";
    }
}
