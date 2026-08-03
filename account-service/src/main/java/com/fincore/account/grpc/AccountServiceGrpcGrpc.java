package com.fincore.account.grpc;

/**
 * Proto-generated stub para AccountService con server-side impl base.
 * Reemplaza código generado por proto — © 2026 Abel Gomez.
 *
 * NOTA: Este es un stub simplificado. El AccountGrpcService extiende
 * AccountServiceGrpcGrpc.AccountServiceGrpcImplBase para recibir
 * llamadas gRPC.
 */
public class AccountServiceGrpcGrpc {

    public static final String SERVICE_NAME = "fincore.account.AccountServiceGrpc";

    public static AccountServiceBlockingStub newBlockingStub(io.grpc.ManagedChannel channel) {
        return new AccountServiceBlockingStub();
    }

    public static class AccountServiceBlockingStub {
        public AccountProto.ReservarFondosResponse reservarFondos(AccountProto.ReservarFondosRequest request) {
            return AccountProto.ReservarFondosResponse.getDefaultInstance();
        }
        public AccountProto.LiberarReservaResponse liberarReserva(AccountProto.LiberarReservaRequest request) {
            return AccountProto.LiberarReservaResponse.getDefaultInstance();
        }
        public AccountProto.ObtenerSaldoResponse obtenerSaldo(AccountProto.ObtenerSaldoRequest request) {
            return AccountProto.ObtenerSaldoResponse.getDefaultInstance();
        }
        public AccountProto.ValidarCuentaResponse validarCuenta(AccountProto.ValidarCuentaRequest request) {
            return AccountProto.ValidarCuentaResponse.getDefaultInstance();
        }
        public AccountProto.ObtenerCuentaResponse obtenerCuenta(AccountProto.ObtenerCuentaRequest request) {
            return AccountProto.ObtenerCuentaResponse.getDefaultInstance();
        }
        public AccountProto.AplicarDebitoResponse aplicarDebito(AccountProto.AplicarDebitoRequest request) {
            return AccountProto.AplicarDebitoResponse.getDefaultInstance();
        }
        public AccountProto.AplicarCreditoResponse aplicarCredito(AccountProto.AplicarCreditoRequest request) {
            return AccountProto.AplicarCreditoResponse.getDefaultInstance();
        }
    }

    public static abstract class AccountServiceGrpcImplBase
            implements io.grpc.BindableService {

        @Override
        public io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(SERVICE_NAME).build();
        }

        public void reservarFondos(AccountProto.ReservarFondosRequest request,
                                   io.grpc.stub.StreamObserver<AccountProto.ReservarFondosResponse> responseObserver) {
            responseObserver.onError(new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED));
        }

        public void liberarReserva(AccountProto.LiberarReservaRequest request,
                                   io.grpc.stub.StreamObserver<AccountProto.LiberarReservaResponse> responseObserver) {
            responseObserver.onError(new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED));
        }

        public void obtenerSaldo(AccountProto.ObtenerSaldoRequest request,
                                 io.grpc.stub.StreamObserver<AccountProto.ObtenerSaldoResponse> responseObserver) {
            responseObserver.onError(new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED));
        }

        public void validarCuenta(AccountProto.ValidarCuentaRequest request,
                                  io.grpc.stub.StreamObserver<AccountProto.ValidarCuentaResponse> responseObserver) {
            responseObserver.onError(new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED));
        }

        public void obtenerCuenta(AccountProto.ObtenerCuentaRequest request,
                                  io.grpc.stub.StreamObserver<AccountProto.ObtenerCuentaResponse> responseObserver) {
            responseObserver.onError(new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED));
        }

        public void aplicarDebito(AccountProto.AplicarDebitoRequest request,
                                  io.grpc.stub.StreamObserver<AccountProto.AplicarDebitoResponse> responseObserver) {
            responseObserver.onError(new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED));
        }

        public void aplicarCredito(AccountProto.AplicarCreditoRequest request,
                                   io.grpc.stub.StreamObserver<AccountProto.AplicarCreditoResponse> responseObserver) {
            responseObserver.onError(new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED));
        }
    }
}
