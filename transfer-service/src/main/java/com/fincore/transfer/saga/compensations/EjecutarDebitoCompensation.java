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
 * Compensación para EJECUTAR_DEBITO.
 * Reversa el asiento de débito y revierte el débito en account-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class EjecutarDebitoCompensation implements CompensationStep {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;
    private final AccountServiceGrpcClient accountClient;
    private final LedgerServiceGrpcClient ledgerClient;

    public EjecutarDebitoCompensation(TransferenciaRepository transferenciaRepository,
                                      TransferenciaEstadoRepository estadoRepository,
                                      AccountServiceGrpcClient accountClient,
                                      LedgerServiceGrpcClient ledgerClient) {
        this.transferenciaRepository = transferenciaRepository;
        this.estadoRepository = estadoRepository;
        this.accountClient = accountClient;
        this.ledgerClient = ledgerClient;
    }

    @Override
    public String getStepName() {
        return "EJECUTAR_DEBITO_COMPENSATION";
    }

    @Override
    public PasoSaga getPasoCompensado() {
        return PasoSaga.EJECUTAR_DEBITO;
    }

    @Override
    public void compensate(SagaContext context) {
        Transferencia transferencia = context.getTransferencia();
        log.info("EjecutarDebitoCompensation: revirtiendo débito para transferencia={}",
                transferencia.getNumeroTransferencia());

        // Revertir asiento de débito en ledger
        try {
            ledgerClient.revertirAsientoDebito(
                    transferencia.getIdCuentaOrigen(),
                    transferencia.getMonto(),
                    transferencia.getId(),
                    transferencia.getTraceId()
            );
            log.info("Asiento de débito revertido en ledger");
        } catch (Exception e) {
            log.error("Error revirtiendo asiento de débito: {}", e.getMessage(), e);
        }

        // Revertir débito en account-service
        try {
            accountClient.revertirDebito(
                    transferencia.getIdCuentaOrigen(),
                    transferencia.getMonto(),
                    transferencia.getTraceId()
            );
            log.info("Débito revertido en account-service");
        } catch (Exception e) {
            log.error("Error revirtiendo débito en account-service: {}", e.getMessage(), e);
        }

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(EstadoTransferencia.ERROR.name());
        estado.setEstadoNuevo(EstadoTransferencia.REVERTIDA.name());
        estado.setPasoSaga("EJECUTAR_DEBITO_COMPENSATION");
        estado.setDescripcion("Débito revertido durante compensación");
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);
    }
}
