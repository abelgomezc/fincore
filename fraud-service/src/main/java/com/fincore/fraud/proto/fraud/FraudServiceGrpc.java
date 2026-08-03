package com.fincore.fraud.proto.fraud;

/**
 * Stub de FraudService con server-side impl base.
 * Reemplaza código generado por proto — © 2026 Abel Gomez.
 */
public class FraudServiceGrpc {

    public static final String SERVICE_NAME = "fraud.FraudService";

    public static FraudServiceBlockingStub newBlockingStub(io.grpc.ManagedChannel channel) {
        return new FraudServiceBlockingStub();
    }

    public static class FraudServiceBlockingStub {
        public EvaluacionFraudeResponse evaluarTransferencia(EvaluacionFraudeRequest request) {
            return EvaluacionFraudeResponse.getDefaultInstance();
        }
    }

    public static abstract class FraudServiceImplBase
            implements io.grpc.BindableService {

        @Override
        public io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(SERVICE_NAME).build();
        }

        public void evaluarTransferencia(EvaluacionFraudeRequest request,
                                        io.grpc.stub.StreamObserver<EvaluacionFraudeResponse> responseObserver) {
            responseObserver.onError(new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED));
        }
    }
}
