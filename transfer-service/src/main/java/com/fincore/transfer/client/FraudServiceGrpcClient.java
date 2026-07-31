package com.fincore.transfer.client;

import com.fincore.transfer.proto.fraud.FraudServiceGrpc;
import com.fincore.transfer.proto.fraud.EvaluacionFraudeRequest;
import com.fincore.transfer.proto.fraud.EvaluacionFraudeResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * gRPC Client para fraud-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class FraudServiceGrpcClient {

    @GrpcClient("fraud-service")
    private FraudServiceGrpc.FraudServiceBlockingStub fraudStub;

    /**
     * Resultado de la evaluación de fraude.
     */
    public static class FraudEvaluationResult {
        private final int score;
        private final String decision;
        private final String mensaje;

        public FraudEvaluationResult(int score, String decision, String mensaje) {
            this.score = score;
            this.decision = decision;
            this.mensaje = mensaje;
        }

        public int getScore() {
            return score;
        }

        public String getDecision() {
            return decision;
        }

        public String getMensaje() {
            return mensaje;
        }
    }

    /**
     * Evalúa una transferencia en el motor antifraude.
     */
    public FraudEvaluationResult evaluarTransferencia(
            Long idTransferencia,
            Long idCuentaOrigen,
            BigDecimal monto,
            String ipOrigen,
            String dispositivo,
            String traceId) {

        try {
            EvaluacionFraudeRequest request = EvaluacionFraudeRequest.newBuilder()
                    .setIdTransferencia(idTransferencia)
                    .setIdCuentaOrigen(idCuentaOrigen)
                    .setMonto(monto.toString())
                    .setIpOrigen(ipOrigen)
                    .setDispositivo(dispositivo)
                    .setTraceId(traceId)
                    .build();

            EvaluacionFraudeResponse response = fraudStub.evaluarTransferencia(request);

            return new FraudEvaluationResult(
                    response.getScore(),
                    response.getDecision(),
                    response.getMensaje()
            );

        } catch (Exception e) {
            log.error("Error evaluando fraude para transferencia {}: {}", idTransferencia, e.getMessage(), e);
            // En caso de error, permitir la transferencia con score bajo
            return new FraudEvaluationResult(0, "APROBADO", "Error en motor de fraude — aprobado por fallback");
        }
    }
}
