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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Compensación para CREAR_EVENTO_KAFKA.
 * No requiere acción de compensación — el evento ya fue publicado
 * y audit/tracking lo consumirán de todas formas.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class CrearEventoKafkaCompensation implements CompensationStep {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;

    public CrearEventoKafkaCompensation(TransferenciaRepository transferenciaRepository,
                                        TransferenciaEstadoRepository estadoRepository) {
        this.transferenciaRepository = transferenciaRepository;
        this.estadoRepository = estadoRepository;
    }

    @Override
    public String getStepName() {
        return "CREAR_EVENTO_KAFKA_COMPENSATION";
    }

    @Override
    public PasoSaga getPasoCompensado() {
        return PasoSaga.CREAR_EVENTO_KAFKA;
    }

    @Override
    public void compensate(SagaContext context) {
        Transferencia transferencia = context.getTransferencia();
        log.info("CrearEventoKafkaCompensation: sin acción de compensación requerida para transferencia={}",
                transferencia.getNumeroTransferencia());

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(transferencia.getEstado().name());
        estado.setEstadoNuevo(EstadoTransferencia.REVERTIDA.name());
        estado.setPasoSaga("CREAR_EVENTO_KAFKA_COMPENSATION");
        estado.setDescripcion("Evento Kafka publicado — sin compensación requerida");
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);
    }
}
