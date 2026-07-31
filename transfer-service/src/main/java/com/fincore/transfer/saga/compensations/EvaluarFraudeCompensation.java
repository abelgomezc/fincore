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
 * Compensación para EVALUAR_FRAUDE.
 * No requiere compensación — no se modificó nada en los sistemas.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class EvaluarFraudeCompensation implements CompensationStep {

    private final TransferenciaEstadoRepository estadoRepository;

    public EvaluarFraudeCompensation(TransferenciaEstadoRepository estadoRepository) {
        this.estadoRepository = estadoRepository;
    }

    @Override
    public String getStepName() {
        return "EVALUAR_FRAUDE_COMPENSATION";
    }

    @Override
    public PasoSaga getPasoCompensado() {
        return PasoSaga.EVALUAR_FRAUDE;
    }

    @Override
    public void compensate(SagaContext context) {
        Transferencia transferencia = context.getTransferencia();
        log.info("EvaluarFraudeCompensation: sin acción de compensación para transferencia={}",
                transferencia.getNumeroTransferencia());

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(transferencia.getEstado().name());
        estado.setEstadoNuevo(EstadoTransferencia.REVERTIDA.name());
        estado.setPasoSaga("EVALUAR_FRAUDE_COMPENSATION");
        estado.setDescripcion("Fraude rechazado — sin compensación requerida");
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);
    }
}
