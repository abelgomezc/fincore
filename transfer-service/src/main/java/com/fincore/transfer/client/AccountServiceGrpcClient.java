package com.fincore.transfer.client;

import com.fincore.transfer.proto.account.AccountServiceGrpc;
import com.fincore.transfer.proto.account.CuentaRequest;
import com.fincore.transfer.proto.account.SaldoRequest;
import com.fincore.transfer.proto.account.TransferenciaRequest;
import com.fincore.transfer.proto.account.RespuestaValidacion;
import lombok.extern.slf4j.Slf4j;
import net.devh.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

/**
 * gRPC Client para account-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class AccountServiceGrpcClient {

    @GrpcClient("account-service")
    private AccountServiceGrpc.AccountServiceBlockingStub accountStub;

    /**
     * Valida que la cuenta exista y esté activa.
     */
    public boolean validarCuenta(Long idCuenta) {
        try {
            CuentaRequest request = CuentaRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .build();
            RespuestaValidacion response = accountStub.validarCuenta(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error validando cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Verifica si la cuenta tiene saldo suficiente para el monto.
     */
    public boolean validarSaldoSuficiente(Long idCuenta, java.math.BigDecimal monto) {
        try {
            SaldoRequest request = SaldoRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .setMonto(monto.toString())
                    .build();
            RespuestaValidacion response = accountStub.validarSaldoSuficiente(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error validando saldo para cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Reserva fondos en la cuenta (bloqueo).
     */
    public boolean reservarFondos(Long idCuenta, java.math.BigDecimal monto, String traceId) {
        try {
            TransferenciaRequest request = TransferenciaRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .setMonto(monto.toString())
                    .setTraceId(traceId)
                    .build();
            RespuestaValidacion response = accountStub.reservarFondos(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error reservando fondos para cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Libera una reserva de fondos previamente hecha.
     */
    public boolean liberarReserva(Long idCuenta, java.math.BigDecimal monto, String traceId) {
        try {
            TransferenciaRequest request = TransferenciaRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .setMonto(monto.toString())
                    .setTraceId(traceId)
                    .build();
            RespuestaValidacion response = accountStub.liberarReserva(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error liberando reserva para cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Aplica el débito de la transferencia en la cuenta origen.
     */
    public boolean aplicarDebito(Long idCuenta, java.math.BigDecimal monto, String traceId) {
        try {
            TransferenciaRequest request = TransferenciaRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .setMonto(monto.toString())
                    .setTraceId(traceId)
                    .build();
            RespuestaValidacion response = accountStub.aplicarDebito(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error aplicando débito para cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Revierte un débito previamente aplicado.
     */
    public boolean revertirDebito(Long idCuenta, java.math.BigDecimal monto, String traceId) {
        try {
            TransferenciaRequest request = TransferenciaRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .setMonto(monto.toString())
                    .setTraceId(traceId)
                    .build();
            RespuestaValidacion response = accountStub.revertirDebito(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error revirtiendo débito para cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Aplica el crédito de la transferencia en la cuenta destino.
     */
    public boolean aplicarCredito(Long idCuenta, java.math.BigDecimal monto, String traceId) {
        try {
            TransferenciaRequest request = TransferenciaRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .setMonto(monto.toString())
                    .setTraceId(traceId)
                    .build();
            RespuestaValidacion response = accountStub.aplicarCredito(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error aplicando crédito para cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Revierte un crédito previamente aplicado.
     */
    public boolean revertirCredito(Long idCuenta, java.math.BigDecimal monto, String traceId) {
        try {
            TransferenciaRequest request = TransferenciaRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .setMonto(monto.toString())
                    .setTraceId(traceId)
                    .build();
            RespuestaValidacion response = accountStub.revertirCredito(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error revirtiendo crédito para cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Aplica una comisión en la cuenta.
     */
    public boolean aplicarComision(Long idCuenta, java.math.BigDecimal monto, String traceId) {
        try {
            TransferenciaRequest request = TransferenciaRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .setMonto(monto.toString())
                    .setTraceId(traceId)
                    .build();
            RespuestaValidacion response = accountStub.aplicarComision(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error aplicando comisión para cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }
}
