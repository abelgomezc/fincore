package com.fincore.transfer.saga.steps;

import com.fincore.transfer.entity.Transferencia;
import com.fincore.transfer.enums.EstadoTransferencia;
import com.fincore.transfer.enums.PasoSaga;
import com.fincore.transfer.saga.SagaContext;
import com.fincore.transfer.saga.SagaStep;
import com.fincore.transfer.saga.SagaStepException;
import com.fincore.transfer.repository.TransferenciaEstadoRepository;
import com.fincore.transfer.repository.TransferenciaRepository;
import com.fincore.transfer.entity.TransferenciaEstado;
import com.fincore.transfer.client.AccountServiceGrpcClient;
import com.fincore.transfer.client.LedgerServiceGrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Paso 11 de la saga: COBRAR_COMISION
 *
 * Cobra la comisión por transferencia (si aplica).
 * Si falla, solo se registra un log (la comisión se cobra en batch nocturno).
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class CobrarComisionStep implements SagaStep {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;
    private final AccountServiceGrpcClient accountClient;
    private final LedgerServiceGrpcClient ledgerClient;

    public CobrarComisionStep(TransferenciaRepository transferenciaRepository,
                              TransferenciaEstadoRepository estadoRepository,
                              AccountServiceGrpcClient accountClient,
                              LedgerServiceGrpcClient ledgerClient) {
        this.transferenciaRepository = transferenciaRepository;
        this.estadoRepository = estadoRepository;
        this.accountClient = accountClient;
        this.ledgerClient = ledgerClient;
    }

    @Override
    public PasoSaga getPaso() {
        return PasoSaga.COBRAR_COMISION;
    }

    @Override
    public void execute(SagaContext context) throws SagaStepException {
        Transferencia transferencia = context.getTransferencia();
        log.info("[Paso 11] COBRAR_COMISION: transferencia={}, comision={}",
                transferencia.getNumeroTransferencia(), transferencia.getComision());

        if (transferencia.getComision() == null || transferencia.getComision().compareTo(BigDecimal.ZERO) <= 0) {
            actualizarEstado(transferencia, EstadoTransferencia.COMPLETADA,
                    "Sin comisión que cobrar");
            log.info("[Paso 11] Sin comisión aplicable");
            return;
        }

        try {
            // Aplicar débito de comisión en account-service
            accountClient.aplicarComision(
                    transferencia.getIdCuentaOrigen(),
                    transferencia.getComision(),
                    transferencia.getTraceId()
            );

            // Crear asiento contable de comisión en ledger-service
            ledgerClient.crearAsientoComision(
                    transferencia.getIdCuentaOrigen(),
                    transferencia.getComision(),
                    transferencia.getId(),
                    "COMISION",
                    transferencia.getIdUsuario(),
                    transferencia.getIpOrigen(),
                    transferencia.getTraceId()
            );

            log.info("[Paso 11] Comisión cobrada: {}", transferencia.getComision());
        } catch (Exception e) {
            log.error("[Paso 11] Error cobrando comisión: {}", e.getMessage(), e);
            // No bloquea la transferencia — se cobra en batch nocturno
        }

        actualizarEstado(transferencia, EstadoTransferencia.COMPLETADA,
                "Comisión procesada: " + transferencia.getComision());

        log.info("[Paso 11] COBRAR_COMISION completado");
    }

    private void actualizarEstado(Transferencia transferencia, EstadoTransferencia nuevoEstado, String descripcion) {
        EstadoTransferencia estadoAnterior = transferencia.getEstado();
        transferencia.setEstado(nuevoEstado);
        transferencia.setPasoSagaActual(PasoSaga.COBRAR_COMISION.getCodigo());

        transferenciaRepository.save(transferencia);

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(estadoAnterior.name());
        estado.setEstadoNuevo(nuevoEstado.name());
        estado.setPasoSaga(PasoSaga.COBRAR_COMISION.getCodigo());
        estado.setDescripcion(descripcion);
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);
    }
}
