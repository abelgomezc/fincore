package com.fincore.fraud.service.rules;

import com.fincore.fraud.entity.ListaNegra;
import com.fincore.fraud.enums.TipoEntradaListaNegra;
import com.fincore.fraud.repository.ListaNegraRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Regla 2.7 — LISTA_NEGRA (100 puntos).
 * Verifica si la cuenta origen, destino, documento o IP está en la lista negra.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
public class ListaNegraRule implements FraudRule {

    private final ListaNegraRepository listaNegraRepository;

    public ListaNegraRule(ListaNegraRepository listaNegraRepository) {
        this.listaNegraRepository = listaNegraRepository;
    }

    @Override
    public RuleResult evaluate(RuleContext context) {
        String codigo = "LISTA_NEGRA";
        int puntos = 100;

        if (context.getNumeroCuentaDestino() != null) {
            if (listaNegraRepository.existsByTipoAndValorAndEsActivoTrue(
                    TipoEntradaListaNegra.CUENTA.name(), context.getNumeroCuentaDestino())) {
                return new RuleResult(codigo, puntos, true,
                        "Cuenta destino en lista negra: " + context.getNumeroCuentaDestino());
            }
        }

        if (context.getIpOrigen() != null) {
            if (listaNegraRepository.existsByTipoAndValorAndEsActivoTrue(
                    TipoEntradaListaNegra.IP.name(), context.getIpOrigen())) {
                return new RuleResult(codigo, puntos, true,
                        "IP origen en lista negra: " + context.getIpOrigen());
            }
        }

        if (context.getDispositivo() != null) {
            if (listaNegraRepository.existsByTipoAndValorAndEsActivoTrue(
                    TipoEntradaListaNegra.DISPOSITIVO.name(), context.getDispositivo())) {
                return new RuleResult(codigo, puntos, true,
                        "Dispositivo en lista negra: " + context.getDispositivo());
            }
        }

        return new RuleResult(codigo, puntos, false, "No hay coincidencias en lista negra");
    }

    @Override
    public String getCodigo() {
        return "LISTA_NEGRA";
    }
}
