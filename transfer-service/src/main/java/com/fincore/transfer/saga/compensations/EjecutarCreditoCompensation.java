package com.fincore.transfer.saga.compensations;

import com.fincore.transfer.entity.Transferencia;
import com.fincore.transfer.enums.EstadoTransferencia;
import com.fincore.transfer.enums.PasoSaga;
import com.fincore.transfer.saga.CompensationStep;
import com.fincore.transfer.saga.SagaContext;
import com.fincore.transfer.enums.PasoSaga;
import com.fincore.transfer.repository.TransferenciaEstadoRepository;
import com.fincore.transfer.repository.TransferenciaRepository;
import com.fincore.transfer.entity.TransferenciaEstado;
import com.fincore.transfer.client.AccountServiceGrpcClient;
import com.fincore.transfer.client.LedgerServiceGrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Compensación para EJECUTAR_CREDITO.
 * Reversa el asiento de crédito y revierte el crédito en account-service,
 * luego ejecuta EjecutarDebitoCompensation para revertir todo.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class EjecutarCreditoCompensation implements CompensationStep {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;
    private final AccountServiceGrpcClient accountClient;
    private final LedgerServiceGrpcClient ledgerClient;
    private final EjecutarDebitoCompensation debitoCompensation;

    public EjecutarCreditoCompensation(TransferenciaRepository transferenciaRepository,
                                       TransferenciaEstadoRepository estadoRepository,
                                       AccountServiceGrpcClient accountClient,
                                       LedgerServiceGrpcClient ledgerClient,
                                       EjecutarDebitoCompensation debitoCompensation) {
        this.transferenciaRepository = transferenciaRepository;
        this.estadoRepository = estadoRepository;
        this.accountClient = accountClient;
        this.ledgerClient = ledgerClient;
        this.debitoCompensation = debitoCompensation;
    }

    @Override
    public String getStepName() {
        return "EJECUTAR_CREDITO_COMPENSATION";
    }

    @Override
    public PasoSaga getPasoCompensado() {
        return PasoSaga.EJECUTAR_CREDITO;
    }

    @Override
    public void compensate(SagaContext context) {
        Transferencia transferencia = context.getTransferencia();
        log.info("EjecutarCreditoCompensation: revirtiendo crédito para transferencia={}",
                transferencia.getNumeroTransferencia());

        // Revertir asiento de crédito en ledger
        try {
            ledgerClient.revertirAsientoCredito(
                    transferencia.getIdCuentaDestino(),
                    transferencia.getMonto(),
                    transferencia.getId(),
                    transferencia.getTraceId()
            );
            log.info("Asiento de crédito revertido en ledger");
        } catch (Exception e) {
            log.error("Error revirtiendo asiento de crédito: {}", e.getMessage(), e);
        }

        // Revertir crédito en account-service
        try {
            accountClient.revertirCredito(
                    transferencia.getIdCuentaDestino(),
                    transferencia.getMonto(),
                    transferencia.getTraceId()
            );
            log.info("Crédito revertido en account-service");
        } catch (Exception e) {
            log.error("Error revirtiendo crédito en account-service: {}", e.getMessage(), e);
        }

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(EstadoTransferencia.ERROR.name());
        estado.setEstadoNuevo(EstadoTransferencia.REVERTIDA.name());
        estado.setPasoSaga("EJECUTAR_CREDITO_COMPENSATION");
        estado.setDescripcion("Crédito revertido durante compensación");
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);

        // Ejecutar compensación del débito
        debitoCompensation.compensate(context);
    }
}
