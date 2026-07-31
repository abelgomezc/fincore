package com.fincore.transfer.saga.compensations;

import com.fincore.transfer.entity.Transferencia;
import com.fincore.transfer.enums.EstadoTransferencia;
import com.fincore.transfer.saga.CompensationStep;
import com.fincore.transfer.saga.SagaContext;
import com.fincore.transfer.enums.PasoSaga;
import com.fincore.transfer.repository.TransferenciaEstadoRepository;
import com.fincore.transfer.entity.TransferenciaEstado;
import com.fincore.transfer.client.AccountServiceGrpcClient;
import com.fincore.transfer.client.LedgerServiceGrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Compensación para RESERVAR_FONDOS.
 * Libera la retención y revierte el asiento de retención.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class ReservarFondosCompensation implements CompensationStep {

    private final TransferenciaEstadoRepository estadoRepository;
    private final AccountServiceGrpcClient accountClient;
    private final LedgerServiceGrpcClient ledgerClient;

    public ReservarFondosCompensation(TransferenciaEstadoRepository estadoRepository,
                                      AccountServiceGrpcClient accountClient,
                                      LedgerServiceGrpcClient ledgerClient) {
        this.estadoRepository = estadoRepository;
        this.accountClient = accountClient;
        this.ledgerClient = ledgerClient;
    }

    @Override
    public String getStepName() {
        return "RESERVAR_FONDOS_COMPENSATION";
    }

    @Override
    public PasoSaga getPasoCompensado() {
        return PasoSaga.RESERVAR_FONDOS;
    }

    @Override
    public void compensate(SagaContext context) {
        Transferencia transferencia = context.getTransferencia();
        log.info("ReservarFondosCompensation: liberando reserva para transferencia={}",
                transferencia.getNumeroTransferencia());

        // Liberar fondos reservados en account-service
        try {
            accountClient.liberarReserva(
                    transferencia.getIdCuentaOrigen(),
                    transferencia.getMonto(),
                    transferencia.getTraceId()
            );
            log.info("Reserva liberada en account-service");
        } catch (Exception e) {
            log.error("Error liberando reserva en account-service: {}", e.getMessage(), e);
        }

        // Revertir asiento de retención en ledger
        try {
            ledgerClient.revertirAsientoRetencion(
                    transferencia.getIdCuentaOrigen(),
                    transferencia.getMonto(),
                    transferencia.getId(),
                    transferencia.getTraceId()
            );
            log.info("Asiento de retención revertido en ledger");
        } catch (Exception e) {
            log.error("Error revirtiendo asiento de retención: {}", e.getMessage(), e);
        }

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(EstadoTransferencia.ERROR.name());
        estado.setEstadoNuevo(EstadoTransferencia.REVERTIDA.name());
        estado.setPasoSaga("RESERVAR_FONDOS_COMPENSATION");
        estado.setDescripcion("Reserva liberada y asiento de retención revertido");
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);
    }
}
