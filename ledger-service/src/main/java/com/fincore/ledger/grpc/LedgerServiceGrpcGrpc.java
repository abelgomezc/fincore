package com.fincore.ledger.grpc;

/**
 * Stub de LedgerService con server-side impl base.
 * Reemplaza código generado por proto — © 2026 Abel Gomez.
 */
public class LedgerServiceGrpcGrpc {

    public static final String SERVICE_NAME = "fincore.ledger.LedgerServiceGrpc";

    public static LedgerServiceBlockingStub newBlockingStub(io.grpc.ManagedChannel channel) {
        return new LedgerServiceBlockingStub();
    }

    public static class LedgerServiceBlockingStub {
        public LedgerProto.CrearAsientoResponse crearAsiento(LedgerProto.CrearAsientoRequest request) {
            return new LedgerProto.CrearAsientoResponse();
        }
        public LedgerProto.ObtenerSaldoCuentaResponse obtenerSaldoCuenta(LedgerProto.ObtenerSaldoCuentaRequest request) {
            return new LedgerProto.ObtenerSaldoCuentaResponse();
        }
        public LedgerProto.VerificarEquilibrioResponse verificarEquilibrio(LedgerProto.VerificarEquibradoRequest request) {
            return new LedgerProto.VerificarEquilibrioResponse();
        }
        public LedgerProto.ObtenerExtractoResponse obtenerExtracto(LedgerProto.ObtenerExtractoRequest request) {
            return new LedgerProto.ObtenerExtractoResponse();
        }
        public LedgerProto.ReversarAsientoResponse reversarAsiento(LedgerProto.ReversarAsientoRequest request) {
            return new LedgerProto.ReversarAsientoResponse();
        }
    }

    public static abstract class LedgerServiceGrpcImplBase
            implements io.grpc.BindableService {

        @Override
        public io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(SERVICE_NAME).build();
        }

        public void crearAsiento(LedgerProto.CrearAsientoRequest request,
                                 io.grpc.stub.StreamObserver<LedgerProto.CrearAsientoResponse> responseObserver) {
            responseObserver.onError(new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED));
        }

        public void obtenerSaldoCuenta(LedgerProto.ObtenerSaldoCuentaRequest request,
                                      io.grpc.stub.StreamObserver<LedgerProto.ObtenerSaldoCuentaResponse> responseObserver) {
            responseObserver.onError(new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED));
        }

        public void verificarEquilibrio(LedgerProto.VerificarEquibradoRequest request,
                                        io.grpc.stub.StreamObserver<LedgerProto.VerificarEquilibrioResponse> responseObserver) {
            responseObserver.onError(new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED));
        }

        public void obtenerExtracto(LedgerProto.ObtenerExtractoRequest request,
                                    io.grpc.stub.StreamObserver<LedgerProto.ObtenerExtractoResponse> responseObserver) {
            responseObserver.onError(new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED));
        }

        public void reversarAsiento(LedgerProto.ReversarAsientoRequest request,
                                    io.grpc.stub.StreamObserver<LedgerProto.ReversarAsientoResponse> responseObserver) {
            responseObserver.onError(new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED));
        }

        public void obtenerCuenta(LedgerProto.ObtenerCuentaRequest request,
                                  io.grpc.stub.StreamObserver<LedgerProto.ObtenerCuentaResponse> responseObserver) {
            responseObserver.onError(new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED));
        }
    }
}
