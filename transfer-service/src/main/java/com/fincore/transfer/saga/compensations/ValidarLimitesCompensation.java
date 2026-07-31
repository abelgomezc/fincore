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
 * Compensación para VALIDAR_LIMITES.
 * No requiere compensación — no se realizaron cambios en sistemas.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class ValidarLimitesCompensation implements CompensationStep {

    private final TransferenciaEstadoRepository estadoRepository;

    public ValidarLimitesCompensation(TransferenciaEstadoRepository estadoRepository) {
        this.estadoRepository = estadoRepository;
    }

    @Override
    public String getStepName() {
        return "VALIDAR_LIMITES_COMPENSATION";
    }

    @Override
    public PasoSaga getPasoCompensado() {
        return PasoSaga.VALIDAR_LIMITES;
    }

    @Override
    public void compensate(SagaContext context) {
        Transferencia transferencia = context.getTransferencia();
        log.info("ValidarLimitesCompensation: sin acción de compensación para transferencia={}",
                transferencia.getNumeroTransferencia());
    }
}
