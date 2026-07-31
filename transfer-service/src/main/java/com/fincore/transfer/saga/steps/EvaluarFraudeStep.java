package com.fincore.transfer.saga.steps;

import com.fincore.transfer.entity.Transferencia;
import com.fincore.transfer.enums.EstadoTransferencia;
import com.fincore.transfer.enums.PasoSaga;
import com.fincore.transfer.saga.SagaContext;
import com.fincore.transfer.saga.SagaStep;
import com.fincore.transfer.saga.SagaStepException;
import com.fincore.transfer.repository.TransferenciaEstadoRepository;
import com.fincore.transfer.repository.TransferenciaRepository;
import com.fincore.transfer.client.FraudServiceGrpcClient;
import com.fincore.transfer.entity.TransferenciaEstado;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Paso 4 de la saga: EVALUAR_FRAUDE
 *
 * Envía la transferencia al motor antifraude vía gRPC.
 * - Score < 30: APROBADO → continuar
 * - Score 30-69: EN_REVISION → pausar para revisión manual
 * - Score > 70: RECHAZADO
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class EvaluarFraudeStep implements SagaStep {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;
    private final FraudServiceGrpcClient fraudClient;

    @Value("${fraud.score.threshold.auto.approve:30}")
    private int scoreAutoApprove;

    @Value("${fraud.score.threshold.review:70}")
    private int scoreReview;

    @Value("${fraud.score.threshold.auto.reject:70}")
    private int scoreAutoReject;

    public EvaluarFraudeStep(TransferenciaRepository transferenciaRepository,
                             TransferenciaEstadoRepository estadoRepository,
                             FraudServiceGrpcClient fraudClient) {
        this.transferenciaRepository = transferenciaRepository;
        this.estadoRepository = estadoRepository;
        this.fraudClient = fraudClient;
    }

    @Override
    public PasoSaga getPaso() {
        return PasoSaga.EVALUAR_FRAUDE;
    }

    @Override
    public void execute(SagaContext context) throws SagaStepException {
        Transferencia transferencia = context.getTransferencia();
        log.info("[Paso 4] EVALUAR_FRAUDE: transferencia={}", transferencia.getNumeroTransferencia());

        // Llamar al motor antifraude vía gRPC
        FraudServiceGrpcClient.FraudEvaluationResult result =
                fraudClient.evaluarTransferencia(
                        transferencia.getId(),
                        transferencia.getIdCuentaOrigen(),
                        transferencia.getMonto(),
                        transferencia.getIpOrigen(),
                        transferencia.getDispositivo(),
                        transferencia.getTraceId()
                );

        transferencia.setScoreFraude(result.getScore());
        transferencia.setDecisionFraude(result.getDecision());

        // Guardar estado de la transferencia
        transferenciaRepository.save(transferencia);

        if ("RECHAZADO".equals(result.getDecision()) || result.getScore() >= scoreAutoReject) {
            actualizarEstado(transferencia, EstadoTransferencia.RECHAZADA,
                    "Fraude detectado. Score: " + result.getScore() + ", decision: " + result.getDecision());
            log.warn("[Paso 4] EVALUAR_FRAUDE — RECHAZADA (score={})", result.getScore());
            throw new SagaStepException(PasoSaga.EVALUAR_FRAUDE,
                    "Transferencia rechazada por fraude. Score: " + result.getScore(), false);
        }

        if ("EN_REVISION".equals(result.getDecision()) || result.getScore() >= scoreAutoApprove) {
            actualizarEstado(transferencia, EstadoTransferencia.EN_REVISION,
                    "En revisión manual. Score: " + result.getScore());
            log.warn("[Paso 4] EVALUAR_FRAUDE — EN_REVISION (score={})", result.getScore());
            throw new SagaStepException(PasoSaga.EVALUAR_FRAUDE,
                    "Transferencia en revisión manual. Score: " + result.getScore(), false);
        }

        actualizarEstado(transferencia, EstadoTransferencia.RESERVANDO,
                "Fraude aprobado. Score: " + result.getScore());

        log.info("[Paso 4] EVALUAR_FRAUDE completado — APROBADO (score={})", result.getScore());
    }

    private void actualizarEstado(Transferencia transferencia, EstadoTransferencia nuevoEstado, String descripcion) {
        EstadoTransferencia estadoAnterior = transferencia.getEstado();
        transferencia.setEstado(nuevoEstado);
        transferencia.setPasoSagaActual(PasoSaga.EVALUAR_FRAUDE.getCodigo());

        transferenciaRepository.save(transferencia);

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(estadoAnterior.name());
        estado.setEstadoNuevo(nuevoEstado.name());
        estado.setPasoSaga(PasoSaga.EVALUAR_FRAUDE.getCodigo());
        estado.setDescripcion(descripcion);
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);
    }
}
