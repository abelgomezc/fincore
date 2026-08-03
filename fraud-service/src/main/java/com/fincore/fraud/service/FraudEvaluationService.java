package com.fincore.fraud.service;

import com.fincore.fraud.enums.DecisionFraude;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de evaluación de fraude.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface FraudEvaluationService {

    /**
     * Evalúa una transferencia contra el motor de reglas de fraude.
     *
     * @param idTransferencia  ID de la transferencia
     * @param idCuentaOrigen   ID de la cuenta origen
     * @param monto            Monto de la transferencia
     * @param ipOrigen         IP desde donde se inicia la transferencia
     * @param dispositivo      ID del dispositivo
     * @param traceId          Trace ID de la transferencia
     * @param idCliente        ID del cliente (opcional)
     * @param numeroCuentaDestino Número de cuenta destino (opcional)
     * @return resultado de la evaluación con score, decision y reglas activadas
     */
    EvaluacionResultado evaluarTransferencia(
            Long idTransferencia,
            Long idCuentaOrigen,
            BigDecimal monto,
            String ipOrigen,
            String dispositivo,
            String traceId,
            Long idCliente,
            String numeroCuentaDestino
    );

    class EvaluacionResultado {
        private final int score;
        private final DecisionFraude decision;
        private final String mensaje;
        private final List<String> reglasActivadas;
        private final int tiempoEvaluacionMs;

        public EvaluacionResultado(int score, DecisionFraude decision, String mensaje,
                                   List<String> reglasActivadas, int tiempoEvaluacionMs) {
            this.score = score;
            this.decision = decision;
            this.mensaje = mensaje;
            this.reglasActivadas = reglasActivadas;
            this.tiempoEvaluacionMs = tiempoEvaluacionMs;
        }

        public int getScore() {
            return score;
        }

        public DecisionFraude getDecision() {
            return decision;
        }

        public String getMensaje() {
            return mensaje;
        }

        public List<String> getReglasActivadas() {
            return reglasActivadas;
        }

        public int getTiempoEvaluacionMs() {
            return tiempoEvaluacionMs;
        }
    }
}
