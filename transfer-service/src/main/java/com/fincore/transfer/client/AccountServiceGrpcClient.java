package com.fincore.transfer.client;

import com.fincore.transfer.proto.account.AccountServiceGrpc;
import com.fincore.transfer.proto.account.CuentaRequest;
import com.fincore.transfer.proto.account.SaldoRequest;
import com.fincore.transfer.proto.account.TransferenciaRequest;
import com.fincore.transfer.proto.account.RespuestaValidacion;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.math.BigDecimal;

/**
 * Cliente gRPC para account-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class AccountServiceGrpcClient {

    private final AccountServiceGrpc.AccountServiceBlockingStub stub;
    private final ManagedChannel channel;

    public AccountServiceGrpcClient() {
        this.channel = ManagedChannelBuilder
                .forAddress("localhost", 9083)
                .usePlaintext()
                .build();
        this.stub = AccountServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public boolean validarCuenta(Long idCuenta) {
        try {
            CuentaRequest request = CuentaRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .build();
            RespuestaValidacion response = stub.validarCuenta(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error validando cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }

    public boolean validarSaldoSuficiente(Long idCuenta, BigDecimal monto) {
        try {
            SaldoRequest request = SaldoRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .setMonto(monto.toString())
                    .build();
            RespuestaValidacion response = stub.validarSaldoSuficiente(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error validando saldo para cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }

    public boolean reservarFondos(Long idCuenta, BigDecimal monto, String traceId) {
        try {
            TransferenciaRequest request = TransferenciaRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .setMonto(monto.toString())
                    .setTraceId(traceId)
                    .build();
            RespuestaValidacion response = stub.reservarFondos(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error reservando fondos para cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }

    public boolean liberarReserva(Long idCuenta, BigDecimal monto, String traceId) {
        try {
            TransferenciaRequest request = TransferenciaRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .setMonto(monto.toString())
                    .setTraceId(traceId)
                    .build();
            RespuestaValidacion response = stub.liberarReserva(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error liberando reserva para cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }

    public boolean aplicarDebito(Long idCuenta, BigDecimal monto, String traceId) {
        try {
            TransferenciaRequest request = TransferenciaRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .setMonto(monto.toString())
                    .setTraceId(traceId)
                    .build();
            RespuestaValidacion response = stub.aplicarDebito(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error aplicando débito para cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }

    public boolean revertirDebito(Long idCuenta, BigDecimal monto, String traceId) {
        try {
            TransferenciaRequest request = TransferenciaRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .setMonto(monto.toString())
                    .setTraceId(traceId)
                    .build();
            RespuestaValidacion response = stub.revertirDebito(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error revirtiendo débito para cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }

    public boolean aplicarCredito(Long idCuenta, BigDecimal monto, String traceId) {
        try {
            TransferenciaRequest request = TransferenciaRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .setMonto(monto.toString())
                    .setTraceId(traceId)
                    .build();
            RespuestaValidacion response = stub.aplicarCredito(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error aplicando crédito para cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }

    public boolean revertirCredito(Long idCuenta, BigDecimal monto, String traceId) {
        try {
            TransferenciaRequest request = TransferenciaRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .setMonto(monto.toString())
                    .setTraceId(traceId)
                    .build();
            RespuestaValidacion response = stub.revertirCredito(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error revirtiendo crédito para cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }

    public boolean aplicarComision(Long idCuenta, BigDecimal monto, String traceId) {
        try {
            TransferenciaRequest request = TransferenciaRequest.newBuilder()
                    .setIdCuenta(idCuenta)
                    .setMonto(monto.toString())
                    .setTraceId(traceId)
                    .build();
            RespuestaValidacion response = stub.aplicarComision(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error aplicando comisión para cuenta {}: {}", idCuenta, e.getMessage(), e);
            return false;
        }
    }
}
