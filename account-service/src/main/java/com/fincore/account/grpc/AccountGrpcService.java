package com.fincore.account.grpc;

import com.fincore.account.entity.Cuenta;
import com.fincore.account.grpc.AccountProto.AplicarCreditoRequest;
import com.fincore.account.grpc.AccountProto.AplicarCreditoResponse;
import com.fincore.account.grpc.AccountProto.AplicarDebitoRequest;
import com.fincore.account.grpc.AccountProto.AplicarDebitoResponse;
import com.fincore.account.grpc.AccountProto.LiberarReservaRequest;
import com.fincore.account.grpc.AccountProto.LiberarReservaResponse;
import com.fincore.account.grpc.AccountProto.ObtenerCuentaRequest;
import com.fincore.account.grpc.AccountProto.ObtenerCuentaResponse;
import com.fincore.account.grpc.AccountProto.ObtenerSaldoRequest;
import com.fincore.account.grpc.AccountProto.ObtenerSaldoResponse;
import com.fincore.account.grpc.AccountProto.ReservarFondosRequest;
import com.fincore.account.grpc.AccountProto.ReservarFondosResponse;
import com.fincore.account.grpc.AccountProto.ValidarCuentaRequest;
import com.fincore.account.grpc.AccountProto.ValidarCuentaResponse;
import com.fincore.account.repository.CuentaRepository;
import com.fincore.account.service.SaldoService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Servicio gRPC para Account Service.
 *
 * Implementa los métodos que consume transfer-service:
 * - ReservarFondos — reserva fondos en cuenta origen
 * - LiberarReserva — libera retención (compensating)
 * - ObtenerSaldo — consulta saldo rápido (read-only)
 * - ValidarCuenta — verifica existencia y estado
 * - ObtenerCuenta — datos completos (read-only)
 * - AplicarDebito — aplica débito contable
 * - AplicarCredito — aplica crédito contable
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@GRpcService
@Slf4j
public class AccountGrpcService extends AccountServiceGrpcGrpc.AccountServiceGrpcImplBase {

    private final SaldoService saldoService;
    private final CuentaRepository cuentaRepository;

    public AccountGrpcService(SaldoService saldoService, CuentaRepository cuentaRepository) {
        this.saldoService = saldoService;
        this.cuentaRepository = cuentaRepository;
    }

    @Override
    public void reservarFondos(ReservarFondosRequest request, StreamObserver<ReservarFondosResponse> responseObserver) {
        log.info("gRPC reservarFondos: cuenta={}, monto={}", request.getIdCuenta(), request.getMonto());
        try {
            Cuenta cuenta = saldoService.reservarFondos(
                    request.getIdCuenta(),
                    BigDecimal.valueOf(request.getMonto()),
                    request.getTraceId()
            );

            responseObserver.onNext(ReservarFondosResponse.newBuilder()
                    .setExito(true)
                    .setIdCuenta(cuenta.getId())
                    .setSaldoContable(cuenta.getSaldoContable().doubleValue())
                    .setSaldoDisponible(cuenta.getSaldoDisponible().doubleValue())
                    .setSaldoRetenido(cuenta.getSaldoRetenido().doubleValue())
                    .setSaldoProyectado(cuenta.getSaldoProyectado().doubleValue())
                    .setTraceId(request.getTraceId())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error en reservarFondos: {}", e.getMessage(), e);
            responseObserver.onNext(ReservarFondosResponse.newBuilder()
                    .setExito(false)
                    .setMensajeError(e.getMessage())
                    .setTraceId(request.getTraceId())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void liberarReserva(LiberarReservaRequest request, StreamObserver<LiberarReservaResponse> responseObserver) {
        log.info("gRPC liberarReserva: cuenta={}, monto={}", request.getIdCuenta(), request.getMonto());
        try {
            Cuenta cuenta = saldoService.liberarRetencion(
                    request.getIdCuenta(),
                    BigDecimal.valueOf(request.getMonto()),
                    request.getTraceId()
            );

            responseObserver.onNext(LiberarReservaResponse.newBuilder()
                    .setExito(true)
                    .setIdCuenta(cuenta.getId())
                    .setSaldoContable(cuenta.getSaldoContable().doubleValue())
                    .setSaldoDisponible(cuenta.getSaldoDisponible().doubleValue())
                    .setSaldoRetenido(cuenta.getSaldoRetenido().doubleValue())
                    .setSaldoProyectado(cuenta.getSaldoProyectado().doubleValue())
                    .setTraceId(request.getTraceId())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error en liberarReserva: {}", e.getMessage(), e);
            responseObserver.onNext(LiberarReservaResponse.newBuilder()
                    .setExito(false)
                    .setMensajeError(e.getMessage())
                    .setTraceId(request.getTraceId())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void obtenerSaldo(ObtenerSaldoRequest request, StreamObserver<ObtenerSaldoResponse> responseObserver) {
        log.info("gRPC obtenerSaldo: cuenta={}", request.getIdCuenta());
        try {
            Optional<Cuenta> optCuenta;
            if (request.getIdCuenta() != 0) {
                optCuenta = cuentaRepository.findById(request.getIdCuenta());
            } else {
                optCuenta = cuentaRepository.findByNumeroCuenta(request.getNumeroCuenta());
            }

            if (optCuenta.isEmpty()) {
                responseObserver.onNext(ObtenerSaldoResponse.newBuilder()
                        .setExito(false)
                        .setMensajeError("Cuenta no encontrada")
                        .build());
                responseObserver.onCompleted();
                return;
            }

            Cuenta cuenta = optCuenta.get();
            responseObserver.onNext(ObtenerSaldoResponse.newBuilder()
                    .setExito(true)
                    .setIdCuenta(cuenta.getId())
                    .setNumeroCuenta(cuenta.getNumeroCuenta())
                    .setSaldoContable(cuenta.getSaldoContable().doubleValue())
                    .setSaldoDisponible(cuenta.getSaldoDisponible().doubleValue())
                    .setSaldoRetenido(cuenta.getSaldoRetenido().doubleValue())
                    .setSaldoProyectado(cuenta.getSaldoProyectado().doubleValue())
                    .setEstado(cuenta.getEstado().name())
                    .setMoneda(cuenta.getMoneda())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error en obtenerSaldo: {}", e.getMessage(), e);
            responseObserver.onNext(ObtenerSaldoResponse.newBuilder()
                    .setExito(false)
                    .setMensajeError(e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void validarCuenta(ValidarCuentaRequest request, StreamObserver<ValidarCuentaResponse> responseObserver) {
        log.info("gRPC validarCuenta: id={}, numero={}", request.getIdCuenta(), request.getNumeroCuenta());
        try {
            Optional<Cuenta> optCuenta;
            if (request.getIdCuenta() != 0) {
                optCuenta = cuentaRepository.findById(request.getIdCuenta());
            } else {
                optCuenta = cuentaRepository.findByNumeroCuenta(request.getNumeroCuenta());
            }

            if (optCuenta.isEmpty()) {
                responseObserver.onNext(ValidarCuentaResponse.newBuilder()
                        .setExito(true)
                        .setExiste(false)
                        .setEstaActiva(false)
                        .setEstado("INEXISTENTE")
                        .build());
                responseObserver.onCompleted();
                return;
            }

            Cuenta cuenta = optCuenta.get();
            responseObserver.onNext(ValidarCuentaResponse.newBuilder()
                    .setExito(true)
                    .setExiste(true)
                    .setEstaActiva(cuenta.esTransferible())
                    .setEstado(cuenta.getEstado().name())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error en validarCuenta: {}", e.getMessage(), e);
            responseObserver.onNext(ValidarCuentaResponse.newBuilder()
                    .setExito(false)
                    .setMensajeError(e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void obtenerCuenta(ObtenerCuentaRequest request, StreamObserver<ObtenerCuentaResponse> responseObserver) {
        log.info("gRPC obtenerCuenta: id={}, numero={}", request.getIdCuenta(), request.getNumeroCuenta());
        try {
            Optional<Cuenta> optCuenta;
            if (request.getIdCuenta() != 0) {
                optCuenta = cuentaRepository.findById(request.getIdCuenta());
            } else {
                optCuenta = cuentaRepository.findByNumeroCuenta(request.getNumeroCuenta());
            }

            if (optCuenta.isEmpty()) {
                responseObserver.onNext(ObtenerCuentaResponse.newBuilder()
                        .setExito(false)
                        .setMensajeError("Cuenta no encontrada")
                        .build());
                responseObserver.onCompleted();
                return;
            }

            Cuenta cuenta = optCuenta.get();
            responseObserver.onNext(ObtenerCuentaResponse.newBuilder()
                    .setExito(true)
                    .setIdCuenta(cuenta.getId())
                    .setNumeroCuenta(cuenta.getNumeroCuenta())
                    .setIdCliente(cuenta.getIdCliente())
                    .setEstado(cuenta.getEstado().name())
                    .setMoneda(cuenta.getMoneda())
                    .setSaldoContable(cuenta.getSaldoContable().doubleValue())
                    .setSaldoDisponible(cuenta.getSaldoDisponible().doubleValue())
                    .setSaldoRetenido(cuenta.getSaldoRetenido().doubleValue())
                    .setSaldoProyectado(cuenta.getSaldoProyectado().doubleValue())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error en obtenerCuenta: {}", e.getMessage(), e);
            responseObserver.onNext(ObtenerCuentaResponse.newBuilder()
                    .setExito(false)
                    .setMensajeError(e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void aplicarDebito(AplicarDebitoRequest request, StreamObserver<AplicarDebitoResponse> responseObserver) {
        log.info("gRPC aplicarDebito: cuenta={}, monto={}, tipo={}", request.getIdCuenta(), request.getMonto(), request.getTipoMovimiento());
        try {
            Cuenta cuenta;
            String traceId = request.getTraceId().isEmpty() ? "grpc-debito" : request.getTraceId();

            if ("RETENCION".equals(request.getTipoMovimiento())) {
                cuenta = saldoService.aplicarRetencion(
                        request.getIdCuenta(),
                        BigDecimal.valueOf(request.getMonto()),
                        traceId
                );
            } else {
                cuenta = saldoService.aplicarDebito(
                        request.getIdCuenta(),
                        BigDecimal.valueOf(request.getMonto()),
                        traceId
                );
            }

            responseObserver.onNext(AplicarDebitoResponse.newBuilder()
                    .setExito(true)
                    .setIdCuenta(cuenta.getId())
                    .setSaldoContable(cuenta.getSaldoContable().doubleValue())
                    .setSaldoDisponible(cuenta.getSaldoDisponible().doubleValue())
                    .setSaldoRetenido(cuenta.getSaldoRetenido().doubleValue())
                    .setSaldoProyectado(cuenta.getSaldoProyectado().doubleValue())
                    .setTraceId(traceId)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error en aplicarDebito: {}", e.getMessage(), e);
            responseObserver.onNext(AplicarDebitoResponse.newBuilder()
                    .setExito(false)
                    .setMensajeError(e.getMessage())
                    .setTraceId(request.getTraceId().isEmpty() ? "grpc-debito" : request.getTraceId())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void aplicarCredito(AplicarCreditoRequest request, StreamObserver<AplicarCreditoResponse> responseObserver) {
        log.info("gRPC aplicarCredito: cuenta={}, monto={}, tipo={}", request.getIdCuenta(), request.getMonto(), request.getTipoMovimiento());
        try {
            Cuenta cuenta;
            String traceId = request.getTraceId().isEmpty() ? "grpc-credito" : request.getTraceId();

            if ("LIBERACION".equals(request.getTipoMovimiento())) {
                cuenta = saldoService.aplicarLiberacion(
                        request.getIdCuenta(),
                        BigDecimal.valueOf(request.getMonto()),
                        traceId
                );
            } else {
                cuenta = saldoService.aplicarCredito(
                        request.getIdCuenta(),
                        BigDecimal.valueOf(request.getMonto()),
                        traceId
                );
            }

            responseObserver.onNext(AplicarCreditoResponse.newBuilder()
                    .setExito(true)
                    .setIdCuenta(cuenta.getId())
                    .setSaldoContable(cuenta.getSaldoContable().doubleValue())
                    .setSaldoDisponible(cuenta.getSaldoDisponible().doubleValue())
                    .setSaldoRetenido(cuenta.getSaldoRetenido().doubleValue())
                    .setSaldoProyectado(cuenta.getSaldoProyectado().doubleValue())
                    .setTraceId(traceId)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error en aplicarCredito: {}", e.getMessage(), e);
            responseObserver.onNext(AplicarCreditoResponse.newBuilder()
                    .setExito(false)
                    .setMensajeError(e.getMessage())
                    .setTraceId(request.getTraceId().isEmpty() ? "grpc-credito" : request.getTraceId())
                    .build());
            responseObserver.onCompleted();
        }
    }
}
