package com.fincore.transfer.client;

import com.fincore.transfer.proto.ledger.LedgerServiceGrpc;
import com.fincore.transfer.proto.ledger.CrearAsientoRequest;
import com.fincore.transfer.proto.ledger.CrearAsientoResponse;
import com.fincore.transfer.proto.ledger.VerificarEquibradoRequest;
import com.fincore.transfer.proto.ledger.VerificarEquibradoResponse;
import com.fincore.transfer.proto.ledger.CuentaContableRequest;
import com.fincore.transfer.proto.ledger.CuentaContableResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * gRPC Client para ledger-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class LedgerServiceGrpcClient {

    @GrpcClient("ledger-service")
    private LedgerServiceGrpc.LedgerServiceBlockingStub ledgerStub;

    /**
     * Crea un asiento contable de retención.
     * Débito: Fondos en tránsito (por transferencia)
     * Crédito: Banco correspondiente (libera retención)
     */
    public boolean crearAsientoRetencion(Long idCuentaOrigen, BigDecimal monto,
                                         Long idTransferencia, String concepto,
                                         String idUsuario, String ipOrigen, String traceId) {
        try {
            CrearAsientoRequest request = CrearAsientoRequest.newBuilder()
                    .setIdCuentaOrigen(idCuentaOrigen)
                    .setMontoOrigen(monto.toString())
                    .setMonedaOrigen("USD")
                    .setIdCuentaDestino(idCuentaOrigen) // misma cuenta para retención
                    .setMontoDestino(monto.toString())
                    .setMonedaDestino("USD")
                    .setConcepto("Retención transferencia " + concepto + " - " + idTransferencia)
                    .setIdUsuario(idUsuario)
                    .setIpOrigen(ipOrigen)
                    .setTraceId(traceId)
                    .build();

            CrearAsientoResponse response = ledgerStub.crearAsiento(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error creando asiento de retención: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Crea un asiento contable de débito.
     * Débito: Cuenta por pagar / pasivo
     * Crédito: Fondos en tránsito (libera retención)
     */
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

            CrearAsientoResponse response = ledgerStub.crearAsiento(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error creando asiento de débito: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Crea un asiento contable de crédito.
     * Débito: Fondos en tránsito (libera retención)
     * Crédito: Banco correspondiente (acredita destino)
     */
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

            CrearAsientoResponse response = ledgerStub.crearAsiento(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error creando asiento de crédito: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Crea un asiento contable de comisión.
     * Débito: Comisiones por cobrar
     * Crédito: Caja / bancos
     */
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

            CrearAsientoResponse response = ledgerStub.crearAsiento(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error creando asiento de comisión: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Revierte un asiento de retención.
     */
    public boolean revertirAsientoRetencion(Long idCuentaOrigen, BigDecimal monto,
                                           Long idTransferencia, String traceId) {
        try {
            CrearAsientoRequest request = CrearAsientoRequest.newBuilder()
                    .setIdCuentaOrigen(idCuentaOrigen)
                    .setMontoOrigen(monto.toString())
                    .setMonedaOrigen("USD")
                    .setIdCuentaDestino(idCuentaOrigen)
                    .setMontoDestino(monto.toString())
                    .setMonedaDestino("USD")
                    .setConcepto("Reversa retención transferencia - " + idTransferencia)
                    .setIdUsuario("system")
                    .setIpOrigen("internal")
                    .setTraceId(traceId)
                    .build();

            CrearAsientoResponse response = ledgerStub.crearAsiento(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error revirtiendo asiento de retención: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Revierte un asiento de débito.
     */
    public boolean revertirAsientoDebito(Long idCuentaOrigen, BigDecimal monto,
                                         Long idTransferencia, String traceId) {
        try {
            CrearAsientoRequest request = CrearAsientoRequest.newBuilder()
                    .setIdCuentaOrigen(idCuentaOrigen)
                    .setMontoOrigen(monto.toString())
                    .setMonedaOrigen("USD")
                    .setIdCuentaDestino(idCuentaOrigen)
                    .setMontoDestino(monto.toString())
                    .setMonedaDestino("USD")
                    .setConcepto("Reversa débido transferencia - " + idTransferencia)
                    .setIdUsuario("system")
                    .setIpOrigen("internal")
                    .setTraceId(traceId)
                    .build();

            CrearAsientoResponse response = ledgerStub.crearAsiento(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error revirtiendo asiento de débito: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Revierte un asiento de crédito.
     */
    public boolean revertirAsientoCredito(Long idCuentaDestino, BigDecimal monto,
                                          Long idTransferencia, String traceId) {
        try {
            CrearAsientoRequest request = CrearAsientoRequest.newBuilder()
                    .setIdCuentaOrigen(idCuentaDestino)
                    .setMontoOrigen(monto.toString())
                    .setMonedaOrigen("USD")
                    .setIdCuentaDestino(idCuentaDestino)
                    .setMontoDestino(monto.toString())
                    .setMonedaDestino("USD")
                    .setConcepto("Reversa acreditado transferencia - " + idTransferencia)
                    .setIdUsuario("system")
                    .setIpOrigen("internal")
                    .setTraceId(traceId)
                    .build();

            CrearAsientoResponse response = ledgerStub.crearAsiento(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error revirtiendo asiento de crédito: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Revierte un asiento de comisión.
     */
    public boolean revertirAsientoComision(Long idCuentaOrigen, BigDecimal comision,
                                           Long idTransferencia, String traceId) {
        try {
            CrearAsientoRequest request = CrearAsientoRequest.newBuilder()
                    .setIdCuentaOrigen(idCuentaOrigen)
                    .setMontoOrigen(comision.toString())
                    .setMonedaOrigen("USD")
                    .setIdCuentaDestino(idCuentaOrigen)
                    .setMontoDestino(comision.toString())
                    .setMonedaDestino("USD")
                    .setConcepto("Reversa comisión transferencia - " + idTransferencia)
                    .setIdUsuario("system")
                    .setIpOrigen("internal")
                    .setTraceId(traceId)
                    .build();

            CrearAsientoResponse response = ledgerStub.crearAsiento(request);
            return response.getExito();
        } catch (Exception e) {
            log.error("Error revirtiendo asiento de comisión: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Verifica el balance del asiento (débitos = créditos).
     */
    public boolean verificarEquilibrio(Long idAsiento) {
        try {
            VerificarEquibradoRequest request = VerificarEquibradoRequest.newBuilder()
                    .setIdAsiento(idAsiento)
                    .build();

            VerificarEquibradoResponse response = ledgerStub.verificarEquilibrio(request);
            return response.getEstaEquilibrado();
        } catch (Exception e) {
            log.error("Error verificando equilibrio del asiento {}: {}", idAsiento, e.getMessage(), e);
            return false;
        }
    }
}
