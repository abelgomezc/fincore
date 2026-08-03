package com.fincore.transfer.proto.account;

import io.grpc.ManagedChannel;

/**
 * Stub simplificado para AccountService (sinonistro del código generado por proto).
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class AccountServiceGrpc {

    public static AccountServiceBlockingStub newBlockingStub(ManagedChannel channel) {
        return new AccountServiceBlockingStub();
    }

    public static class AccountServiceBlockingStub {

        public RespuestaValidacion validarCuenta(CuentaRequest request) {
            return RespuestaValidacion.getDefaultInstance();
        }

        public RespuestaValidacion validarSaldoSuficiente(SaldoRequest request) {
            return RespuestaValidacion.getDefaultInstance();
        }

        public RespuestaValidacion reservarFondos(TransferenciaRequest request) {
            return RespuestaValidacion.getDefaultInstance();
        }

        public RespuestaValidacion liberarReserva(TransferenciaRequest request) {
            return RespuestaValidacion.getDefaultInstance();
        }

        public RespuestaValidacion aplicarDebito(TransferenciaRequest request) {
            return RespuestaValidacion.getDefaultInstance();
        }

        public RespuestaValidacion revertirDebito(TransferenciaRequest request) {
            return RespuestaValidacion.getDefaultInstance();
        }

        public RespuestaValidacion aplicarCredito(TransferenciaRequest request) {
            return RespuestaValidacion.getDefaultInstance();
        }

        public RespuestaValidacion revertirCredito(TransferenciaRequest request) {
            return RespuestaValidacion.getDefaultInstance();
        }

        public RespuestaValidacion aplicarComision(TransferenciaRequest request) {
            return RespuestaValidacion.getDefaultInstance();
        }
    }
}
