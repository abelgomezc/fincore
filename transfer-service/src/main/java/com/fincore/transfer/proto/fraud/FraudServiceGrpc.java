package com.fincore.transfer.proto.fraud;

/**
 * Stub simplificado para FraudService (reemplaza código generado por proto).
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class FraudServiceGrpc {

    public static FraudServiceBlockingStub newBlockingStub(Object channel) {
        return new FraudServiceBlockingStub();
    }

    public static class FraudServiceBlockingStub {

        public EvaluacionFraudeResponse evaluarTransferencia(EvaluacionFraudeRequest request) {
            return EvaluacionFraudeResponse.newBuilder().build();
        }
    }
}
