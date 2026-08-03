package com.fincore.fraud.service.impl;

import com.fincore.fraud.entity.EvaluacionFraude;
import com.fincore.fraud.entity.PerfilTransaccional;
import com.fincore.fraud.enums.DecisionFraude;
import com.fincore.fraud.repository.EvaluacionFraudeRepository;
import com.fincore.fraud.repository.PerfilTransaccionalRepository;
import com.fincore.fraud.service.FraudEvaluationService;
import com.fincore.fraud.service.rules.FraudRule;
import com.fincore.fraud.service.rules.RuleContext;
import com.fincore.fraud.service.rules.RuleResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Implementación del motor de evaluación de fraude.
 *
 * Orquesta las 10 reglas de scoring y determina la decisión final.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Service
@Slf4j
public class FraudEvaluationServiceImpl implements FraudEvaluationService {

    private final List<FraudRule> rules;
    private final PerfilTransaccionalRepository perfilRepository;
    private final EvaluacionFraudeRepository evaluacionRepository;
    private final ObjectMapper objectMapper;

    @Value("${fraud.score.threshold.auto.approve:30}")
    private int thresholdAutoApprove;

    @Value("${fraud.score.threshold.review:70}")
    private int thresholdReview;

    @Value("${fraud.score.threshold.auto.reject:70}")
    private int thresholdAutoReject;

    public FraudEvaluationServiceImpl(List<FraudRule> rules,
                                      PerfilTransaccionalRepository perfilRepository,
                                      EvaluacionFraudeRepository evaluacionRepository,
                                      ObjectMapper objectMapper) {
        this.rules = rules;
        this.perfilRepository = perfilRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public EvaluacionResultado evaluarTransferencia(
            Long idTransferencia,
            Long idCuentaOrigen,
            java.math.BigDecimal monto,
            String ipOrigen,
            String dispositivo,
            String traceId,
            Long idCliente,
            String numeroCuentaDestino) {

        long inicio = System.currentTimeMillis();
        log.info("Iniciando evaluación de fraude: transferencia={}, cliente={}, monto={}",
                idTransferencia, idCliente, monto);

        Optional<PerfilTransaccional> perfilOpt = Optional.empty();
        if (idCliente != null) {
            perfilOpt = perfilRepository.findByIdCliente(idCliente);
        }

        RuleContext context = new RuleContext(
                idTransferencia, idCuentaOrigen, idCliente, monto,
                ipOrigen, dispositivo, numeroCuentaDestino, perfilOpt);

        List<RuleResult> resultados = rules.stream()
                .map(rule -> {
                    try {
                        return rule.evaluate(context);
                    } catch (Exception e) {
                        log.warn("Error evaluando regla {}: {}", rule.getCodigo(), e.getMessage(), e);
                        return new RuleResult(rule.getCodigo(), 0, false, "Error en evaluación: " + e.getMessage());
                    }
                })
                .collect(Collectors.toList());

        List<String> reglasActivadas = resultados.stream()
                .filter(RuleResult::isActivada)
                .map(RuleResult::getCodigo)
                .collect(Collectors.toList());

        int scoreTotal = (int) resultados.stream()
                .filter(RuleResult::isActivada)
                .mapToInt(RuleResult::getPuntos)
                .sum();

        DecisionFraude decision = determinarDecision(scoreTotal);
        int tiempoEvaluacionMs = (int) (System.currentTimeMillis() - inicio);

        String mensaje = construirMensaje(decision, reglasActivadas, scoreTotal);

        log.info("Evaluación completada: transferencia={}, score={}, decision={}, tiempo={}ms, reglas={}",
                idTransferencia, scoreTotal, decision, tiempoEvaluacionMs, reglasActivadas);

        EvaluacionResultado resultado = new EvaluacionResultado(
                scoreTotal, decision, mensaje, reglasActivadas, tiempoEvaluacionMs);

        persistirEvaluacion(idTransferencia, idCliente, scoreTotal, decision,
                reglasActivadas, ipOrigen, dispositivo, tiempoEvaluacionMs);

        return resultado;
    }

    private DecisionFraude determinarDecision(int score) {
        if (score < thresholdAutoApprove) {
            return DecisionFraude.APROBADO;
        }
        if (score < thresholdReview) {
            return DecisionFraude.EN_REVISION;
        }
        return DecisionFraude.RECHAZADO;
    }

    private String construirMensaje(DecisionFraude decision, List<String> reglasActivadas, int score) {
        return switch (decision) {
            case APROBADO -> "Transferencia aprobada. Score=" + score + " (reglas: " + reglasActivadas + ")";
            case EN_REVISION -> "Transferencia en revisión manual. Score=" + score + " (reglas: " + reglasActivadas + ")";
            case RECHAZADO -> "Transferencia rechazada. Score=" + score + " (reglas: " + reglasActivadas + ")";
        };
    }

    private void persistirEvaluacion(Long idTransferencia, Long idCliente, int score,
                                     DecisionFraude decision, List<String> reglasActivadas,
                                     String ipOrigen, String dispositivo, int tiempoMs) {
        try {
            EvaluacionFraude evaluacion = new EvaluacionFraude();
            evaluacion.setIdTransferencia(idTransferencia);
            evaluacion.setIdCliente(idCliente);
            evaluacion.setScoreTotal(score);
            evaluacion.setDecision(decision.name());
            evaluacion.setReglasActivadas(objectMapper.writeValueAsString(reglasActivadas));
            evaluacion.setIpOrigen(ipOrigen);
            evaluacion.setDispositivo(dispositivo);
            evaluacion.setTiempoEvaluacionMs(tiempoMs);
            evaluacion.setFechaCreacion(LocalDateTime.now());
            evaluacionRepository.save(evaluacion);
        } catch (Exception e) {
            log.error("Error persistiendo evaluación de fraude: {}", e.getMessage(), e);
        }
    }
}
