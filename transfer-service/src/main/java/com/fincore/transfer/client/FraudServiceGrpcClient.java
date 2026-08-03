package com.fincore.transfer.client;

import com.fincore.transfer.proto.fraud.FraudServiceGrpc;
import com.fincore.transfer.proto.fraud.EvaluacionFraudeRequest;
import com.fincore.transfer.proto.fraud.EvaluacionFraudeResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.math.BigDecimal;

/**
 * Cliente gRPC para fraud-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class FraudServiceGrpcClient {

    private final FraudServiceGrpc.FraudServiceBlockingStub stub;
    private final ManagedChannel channel;

    public FraudServiceGrpcClient() {
        this.channel = ManagedChannelBuilder
                .forAddress("localhost", 9086)
                .usePlaintext()
                .build();
        this.stub = FraudServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

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

            EvaluacionFraudeResponse response = stub.evaluarTransferencia(request);

            return new FraudEvaluationResult(
                    response.getScore(),
                    response.getDecision(),
                    response.getMensaje()
            );

        } catch (Exception e) {
            log.error("Error evaluando fraude para transferencia {}: {}", idTransferencia, e.getMessage(), e);
            return new FraudEvaluationResult(0, "APROBADO", "Error en motor de fraude — aprobado por fallback");
        }
    }
}
