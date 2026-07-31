package com.fincore.ledger.grpc;

import com.fincore.ledger.dto.AsientoDTO;
import com.fincore.ledger.dto.LineaAsientoDTO;
import com.fincore.ledger.dto.response.AsientoResponse;
import com.fincore.ledger.grpc.LedgerProto.CrearAsientoRequest;
import com.fincore.ledger.grpc.LedgerProto.CrearAsientoResponse;
import com.fincore.ledger.grpc.LedgerProto.LiberarReservaRequest;
import com.fincore.ledger.grpc.LedgerProto.LiberarReservaResponse;
import com.fincore.ledger.grpc.LedgerProto.ObtenerCuentaRequest;
import com.fincore.ledger.grpc.LedgerProto.ObtenerCuentaResponse;
import com.fincore.ledger.grpc.LedgerProto.ObtenerSaldoCuentaRequest;
import com.fincore.ledger.grpc.LedgerProto.ObtenerSaldoCuentaResponse;
import com.fincore.ledger.grpc.LedgerProto.VerificarEquilibrioRequest;
import com.fincore.ledger.grpc.LedgerProto.VerificarEquilibrioResponse;
import com.fincore.ledger.grpc.LedgerProto.ReversarAsientoRequest;
import com.fincore.ledger.grpc.LedgerProto.ReversarAsientoResponse;
import com.fincore.ledger.grpc.LedgerProto.ObtenerExtractoRequest;
import com.fincore.ledger.grpc.LedgerProto.ObtenerExtractoResponse;
import com.fincore.ledger.service.LedgerService;
import com.fincore.ledger.service.AsientoFactory;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.springboot.autoconfigure.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio gRPC para Ledger Service.
 *
 * Implementa los métodos que consume transfer-service:
 * - CrearAsiento — crea un asiento contable de doble partida
 * - ObtenerSaldoCuenta — consulta saldo de una cuenta contable
 * - VerificarEquilibrio — verifica que el ledger esté balanceado
 * - ObtenerExtracto — obtiene movimientos de una cuenta
 * - ReversarAsiento — revierte un asiento existente
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@GrpcService
@Slf4j
public class LedgerGrpcService extends LedgerServiceGrpcGrpc.LedgerServiceGrpcImplBase {

    private final LedgerService ledgerService;
    private final AsientoFactory asientoFactory;

    public LedgerGrpcService(LedgerService ledgerService, AsientoFactory asientoFactory) {
        this.ledgerService = ledgerService;
        this.asientoFactory = asientoFactory;
    }

    @Override
    public void crearAsiento(CrearAsientoRequest request, StreamObserver<CrearAsientoResponse> responseObserver) {
        log.info("gRPC crearAsiento: descripción={}, referencia={}",
                request.getDescripcion(), request.getIdReferencia());

        try {
            List<LineaAsientoDTO> lineas = new ArrayList<>();
            for (var lineaProto : request.getLineasList()) {
                lineas.add(LineaAsientoDTO.builder()
                        .codigoCuenta(lineaProto.getCodigoCuenta())
                        .idCuentaBancaria(lineaProto.getIdCuentaBancaria() != 0 ? lineaProto.getIdCuentaBancaria() : null)
                        .tipoMovimiento(lineaProto.getTipoMovimiento())
                        .monto(BigDecimal.valueOf(lineaProto.getMonto()))
                        .descripcion(lineaProto.getDescripcion())
                        .build());
            }

            AsientoDTO dto = AsientoDTO.builder()
                    .descripcion(request.getDescripcion())
                    .lineas(lineas)
                    .tipoReferencia(request.getTipoReferencia())
                    .idReferencia(request.getIdReferencia() != 0 ? request.getIdReferencia() : null)
                    .idUsuario(request.getIdUsuario())
                    .ipOrigen(request.getIpOrigen())
                    .traceId(request.getTraceId())
                    .build();

            AsientoResponse result = ledgerService.crearAsiento(dto);

            responseObserver.onNext(CrearAsientoResponse.newBuilder()
                    .setExito(result.isExito())
                    .setNumeroAsiento(result.getNumeroAsiento() != null ? result.getNumeroAsiento() : "")
                    .setIdAsiento(result.getIdAsiento() != null ? result.getIdAsiento() : 0)
                    .setMensajeError(result.getMensajeError() != null ? result.getMensajeError() : "")
                    .setTraceId(request.getTraceId())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error en crearAsiento: {}", e.getMessage(), e);
            responseObserver.onNext(CrearAsientoResponse.newBuilder()
                    .setExito(false)
                    .setMensajeError(e.getMessage())
                    .setTraceId(request.getTraceId())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void obtenerSaldoCuenta(ObtenerSaldoCuentaRequest request,
                                   StreamObserver<ObtenerSaldoCuentaResponse> responseObserver) {
        log.info("gRPC obtenerSaldoCuenta: codigo={}, idCuenta={}",
                request.getCodigoCuenta(), request.getIdCuentaBancaria());

        try {
            var estado = ledgerService.obtenerEstadoCuenta(request.getCodigoCuenta());

            responseObserver.onNext(ObtenerSaldoCuentaResponse.newBuilder()
                    .setExito(true)
                    .setCodigoCuenta(estado.getCodigoCuenta() != null ? estado.getCodigoCuenta() : "")
                    .setSaldoNeto(estado.getSaldoNeto() != null ? estado.getSaldoNeto().doubleValue() : 0.0)
                    .setTotalDebitos(estado.getTotalDebitos() != null ? estado.getTotalDebitos().doubleValue() : 0.0)
                    .setTotalCreditos(estado.getTotalCreditos() != null ? estado.getTotalCreditos().doubleValue() : 0.0)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error en obtenerSaldoCuenta: {}", e.getMessage(), e);
            responseObserver.onNext(ObtenerSaldoCuentaResponse.newBuilder()
                    .setExito(false)
                    .setMensajeError(e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void verificarEquilibrio(VerificarEquibradoRequest request,
                                    StreamObserver<VerificarEquilibrioResponse> responseObserver) {
        log.info("gRPC verificarEquilibrio");

        try {
            var balance = ledgerService.verificarEquilibrio();

            responseObserver.onNext(VerificarEquilibrioResponse.newBuilder()
                    .setExito(balance.isEquilibrioOk())
                    .setDiferencia(balance.getDiferencia() != null ? balance.getDiferencia().doubleValue() : 0.0)
                    .setTotalAsientos(balance.getTotalAsientos())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error en verificarEquilibrio: {}", e.getMessage(), e);
            responseObserver.onNext(VerificarEquilibrioResponse.newBuilder()
                    .setExito(false)
                    .setMensajeError(e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void obtenerExtracto(ObtenerExtractoRequest request,
                                StreamObserver<ObtenerExtractoResponse> responseObserver) {
        // Placeholder — se implementa con datos reales
        responseObserver.onNext(ObtenerExtractoResponse.newBuilder()
                .setExito(true)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void reversarAsiento(ReversarAsientoRequest request,
                                StreamObserver<ReversarAsientoResponse> responseObserver) {
        log.info("gRPC reversarAsiento: numero={}, traceId={}", request.getNumeroAsiento(), request.getTraceId());

        try {
            AsientoResponse result = ledgerService.reversarAsiento(
                    request.getNumeroAsiento(),
                    request.getDescripcionRevision(),
                    request.getIdUsuario(),
                    request.getTraceId()
            );

            responseObserver.onNext(ReversarAsientoResponse.newBuilder()
                    .setExito(result.isExito())
                    .setNumeroAsientoReversado(result.getNumeroAsiento() != null ? result.getNumeroAsiento() : "")
                    .setNuevoNumeroAsiento(result.getNumeroAsiento() != null ? result.getNumeroAsiento() : "")
                    .setMensajeError(result.getMensajeError() != null ? result.getMensajeError() : "")
                    .setTraceId(request.getTraceId())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error en reversarAsiento: {}", e.getMessage(), e);
            responseObserver.onNext(ReversarAsientoResponse.newBuilder()
                    .setExito(false)
                    .setMensajeError(e.getMessage())
                    .setTraceId(request.getTraceId())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void obtenerCuenta(ObtenerCuentaRequest request,
                              StreamObserver<ObtenerCuentaResponse> responseObserver) {
        // Este método no está en el proto original del ledger, pero el grpc lo requiere
        responseObserver.onCompleted();
    }
}
