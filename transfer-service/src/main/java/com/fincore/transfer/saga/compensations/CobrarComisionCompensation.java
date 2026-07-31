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
import com.fincore.transfer.client.LedgerServiceGrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Compensación para COBRAR_COMISION.
 * Reversa el asiento contable de la comisión si se cobró.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class CobrarComisionCompensation implements CompensationStep {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;
    private final LedgerServiceGrpcClient ledgerClient;

    public CobrarComisionCompensation(TransferenciaRepository transferenciaRepository,
                                      TransferenciaEstadoRepository estadoRepository,
                                      LedgerServiceGrpcClient ledgerClient) {
        this.transferenciaRepository = transferenciaRepository;
        this.estadoRepository = estadoRepository;
        this.ledgerClient = ledgerClient;
    }

    @Override
    public String getStepName() {
        return "COBRAR_COMISION_COMPENSATION";
    }

    @Override
    public PasoSaga getPasoCompensado() {
        return PasoSaga.COBRAR_COMISION;
    }

    @Override
    public void compensate(SagaContext context) {
        Transferencia transferencia = context.getTransferencia();
        log.info("CobrarComisionCompensation: revirtiendo comisión para transferencia={}",
                transferencia.getNumeroTransferencia());

        if (transferencia.getComision() != null && transferencia.getComision().compareTo(java.math.BigDecimal.ZERO) > 0) {
            try {
                ledgerClient.revertirAsientoComision(
                        transferencia.getIdCuentaOrigen(),
                        transferencia.getComision(),
                        transferencia.getId(),
                        transferencia.getTraceId()
                );
                log.info("Asiento de comisión revertido exitosamente");
            } catch (Exception e) {
                log.error("Error revirtiendo asiento de comisión: {}", e.getMessage(), e);
            }
        }

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(EstadoTransferencia.COMPLETADA.name());
        estado.setEstadoNuevo(EstadoTransferencia.REVERTIDA.name());
        estado.setPasoSaga("COBRAR_COMISION_COMPENSATION");
        estado.setDescripcion("Comisión revertida durante compensación");
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);
    }
}
