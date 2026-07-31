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
 * Compensación para LIBERAR_RETENCION.
 * Si la liberación falló, la compensación no puede hacer nada.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class LiberarRetencionCompensation implements CompensationStep {

    private final TransferenciaEstadoRepository estadoRepository;

    public LiberarRetencionCompensation(TransferenciaEstadoRepository estadoRepository) {
        this.estadoRepository = estadoRepository;
    }

    @Override
    public String getStepName() {
        return "LIBERAR_RETENCION_COMPENSATION";
    }

    @Override
    public PasoSaga getPasoCompensado() {
        return PasoSaga.LIBERAR_RETENCION;
    }

    @Override
    public void compensate(SagaContext context) {
        Transferencia transferencia = context.getTransferencia();
        log.info("LiberarRetencionCompensation: sin acción de compensación para transferencia={}",
                transferencia.getNumeroTransferencia());
    }
}
