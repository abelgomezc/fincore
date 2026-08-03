package com.fincore.transfer.client;

import com.fincore.transfer.proto.ledger.LedgerServiceGrpc;
import com.fincore.transfer.proto.ledger.CrearAsientoRequest;
import com.fincore.transfer.proto.ledger.CrearAsientoResponse;
import com.fincore.transfer.proto.ledger.VerificarEquibradoRequest;
import com.fincore.transfer.proto.ledger.VerificarEquibradoResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.math.BigDecimal;

/**
 * Cliente gRPC para ledger-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class LedgerServiceGrpcClient {

    private final LedgerServiceGrpc.LedgerServiceBlockingStub stub;
    private final ManagedChannel channel;

    public LedgerServiceGrpcClient() {
        this.channel = ManagedChannelBuilder
                .forAddress("localhost", 9084)
                .usePlaintext()
                .build();
        this.stub = LedgerServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public boolean crearAsientoRetencion(Long idCuentaOrigen, BigDecimal monto,
                                         Long idTransferencia, String concepto,
                                         String idUsuario, String ipOrigen, String traceId) {
        try {
            CrearAsientoRequest request = CrearAsientoRequest.newBuilder()
                    .setIdCuentaOrigen(idCuentaOrigen)
                    .setMontoOrigen(monto.toString())
                    .setMonedaOrigen("USD")
                    .setIdCuentaDestino(idCuentaOrigen)
                    .setMontoDestino(monto.toString())
                    .setMonedaDestino("USD")
                    .setConcepto("Retención transferencia " + concepto + " - " + idTransferencia)
                    .setIdUsuario(idUsuario)
                    .setIpOrigen(ipOrigen)
                    .setTraceId(traceId)
                    .build();

            CrearAsientoResponse response = stub.crearAsiento(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error creando asiento de retención: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean crearAsientoDebito(Long idCuentaOrigen, BigDecimal monto,
                                     Long idTransferencia, String concepto,
                                     String idUsuario, String ipOrigen, String traceId) {
        try {
            CrearAsientoRequest request = CrearAsientoRequest.newBuilder()
                    .setIdCuentaOrigen(idCuentaOrigen)
                    .setMontoOrigen(monto.toString())
                    .setMonedaOrigen("USD")
                    .setIdCuentaDestino(idCuentaOrigen)
                    .setMontoDestino(monto.toString())
                    .setMonedaDestino("USD")
                    .setConcepto("Débido transferencia " + concepto + " - " + idTransferencia)
                    .setIdUsuario(idUsuario)
                    .setIpOrigen(ipOrigen)
                    .setTraceId(traceId)
                    .build();

            CrearAsientoResponse response = stub.crearAsiento(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error creando asiento de débito: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean crearAsientoCredito(Long idCuentaDestino, BigDecimal monto,
                                       Long idTransferencia, String concepto,
                                       String idUsuario, String ipOrigen, String traceId) {
        try {
            CrearAsientoRequest request = CrearAsientoRequest.newBuilder()
                    .setIdCuentaOrigen(idCuentaDestino)
                    .setMontoOrigen(monto.toString())
                    .setMonedaOrigen("USD")
                    .setIdCuentaDestino(idCuentaDestino)
                    .setMontoDestino(monto.toString())
                    .setMonedaDestino("USD")
                    .setConcepto("Acreditado transferencia " + concepto + " - " + idTransferencia)
                    .setIdUsuario(idUsuario)
                    .setIpOrigen(ipOrigen)
                    .setTraceId(traceId)
                    .build();

            CrearAsientoResponse response = stub.crearAsiento(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error creando asiento de crédito: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean crearAsientoComision(Long idCuentaOrigen, BigDecimal comision,
                                        Long idTransferencia, String concepto,
                                        String idUsuario, String ipOrigen, String traceId) {
        try {
            CrearAsientoRequest request = CrearAsientoRequest.newBuilder()
                    .setIdCuentaOrigen(idCuentaOrigen)
                    .setMontoOrigen(comision.toString())
                    .setMonedaOrigen("USD")
                    .setIdCuentaDestino(idCuentaOrigen)
                    .setMontoDestino(comision.toString())
                    .setMonedaDestino("USD")
                    .setConcepto("Comisión " + concepto + " - transferencia " + idTransferencia)
                    .setIdUsuario(idUsuario)
                    .setIpOrigen(ipOrigen)
                    .setTraceId(traceId)
                    .build();

            CrearAsientoResponse response = stub.crearAsiento(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error creando asiento de comisión: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean revertirAsientoRetencion(Long idCuentaOrigen, BigDecimal monto,
                                           Long idTransferencia, String traceId) {
        return crearAsientoReversa(idCuentaOrigen, idCuentaOrigen, monto, "Reversa retención transferencia - " + idTransferencia, traceId);
    }

    public boolean revertirAsientoDebito(Long idCuentaOrigen, BigDecimal monto,
                                         Long idTransferencia, String traceId) {
        return crearAsientoReversa(idCuentaOrigen, idCuentaOrigen, monto, "Reversa débido transferencia - " + idTransferencia, traceId);
    }

    public boolean revertirAsientoCredito(Long idCuentaDestino, BigDecimal monto,
                                          Long idTransferencia, String traceId) {
        return crearAsientoReversa(idCuentaDestino, idCuentaDestino, monto, "Reversa acreditado transferencia - " + idTransferencia, traceId);
    }

    public boolean revertirAsientoComision(Long idCuentaOrigen, BigDecimal comision,
                                           Long idTransferencia, String traceId) {
        return crearAsientoReversa(idCuentaOrigen, idCuentaOrigen, comision, "Reversa comisión transferencia - " + idTransferencia, traceId);
    }

    private boolean crearAsientoReversa(Long idCuentaOrigen, Long idCuentaDestino,
                                        BigDecimal monto, String concepto, String traceId) {
        try {
            CrearAsientoRequest request = CrearAsientoRequest.newBuilder()
                    .setIdCuentaOrigen(idCuentaOrigen)
                    .setMontoOrigen(monto.toString())
                    .setMonedaOrigen("USD")
                    .setIdCuentaDestino(idCuentaDestino)
                    .setMontoDestino(monto.toString())
                    .setMonedaDestino("USD")
                    .setConcepto(concepto)
                    .setIdUsuario("system")
                    .setIpOrigen("internal")
                    .setTraceId(traceId)
                    .build();

            CrearAsientoResponse response = stub.crearAsiento(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error creando asiento de reversa: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean verificarEquilibrio(Long idAsiento) {
        try {
            VerificarEquibradoRequest request = VerificarEquibradoRequest.newBuilder()
                    .setIdAsiento(idAsiento)
                    .build();

            VerificarEquibradoResponse response = stub.verificarEquilibrio(request);
            return response.getEstaEquilibrado();
        } catch (Exception e) {
            log.error("Error verificando equilibrio del asiento {}: {}", idAsiento, e.getMessage(), e);
            return false;
        }
    }
}
