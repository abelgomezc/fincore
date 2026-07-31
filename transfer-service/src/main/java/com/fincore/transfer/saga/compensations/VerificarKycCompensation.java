package com.fincore.transfer.saga.compensations;

import com.fincore.transfer.entity.Transferencia;
import com.fincore.transfer.enums.EstadoTransferencia;
import com.fincore.transfer.saga.CompensationStep;
import com.fincore.transfer.saga.SagaContext;
import com.fincore.transfer.enums.PasoSaga;
import com.fincore.transfer.repository.TransferenciaEstadoRepository;
import com.fincore.transfer.entity.TransferenciaEstado;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Compensación para VERIFICAR_KYC.
 * No requiere compensación — no se realizaron cambios en sistemas.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class VerificarKycCompensation implements CompensationStep {

    private final TransferenciaEstadoRepository estadoRepository;

    public VerificarKycCompensation(TransferenciaEstadoRepository estadoRepository) {
        this.estadoRepository = estadoRepository;
    }

    @Override
    public String getStepName() {
        return "VERIFICAR_KYC_COMPENSATION";
    }

    @Override
    public PasoSaga getPasoCompensado() {
        return PasoSaga.VERIFICAR_KYC;
    }

    @Override
    public void compensate(SagaContext context) {
        Transferencia transferencia = context.getTransferencia();
        log.info("VerificarKycCompensation: sin acción de compensación para transferencia={}",
                transferencia.getNumeroTransferencia());
    }
}
