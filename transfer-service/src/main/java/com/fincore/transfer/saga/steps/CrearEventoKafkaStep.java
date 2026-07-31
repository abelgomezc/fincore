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
import com.fincore.transfer.kafka.TransferenciaEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Paso 6 de la saga: CREAR_EVENTO_KAFKA
 *
 * Publica el evento transferencia.iniciada en Kafka.
 * Este evento es consumido por: audit, fraud, notification.
 *
 * Compensación: LiberarReservaCompensation
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class CrearEventoKafkaStep implements SagaStep {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;
    private final TransferenciaEventProducer eventProducer;

    public CrearEventoKafkaStep(TransferenciaRepository transferenciaRepository,
                                TransferenciaEstadoRepository estadoRepository,
                                TransferenciaEventProducer eventProducer) {
        this.transferenciaRepository = transferenciaRepository;
        this.estadoRepository = estadoRepository;
        this.eventProducer = eventProducer;
    }

    @Override
    public PasoSaga getPaso() {
        return PasoSaga.CREAR_EVENTO_KAFKA;
    }

    @Override
    public void execute(SagaContext context) throws SagaStepException {
        Transferencia transferencia = context.getTransferencia();
        log.info("[Paso 6] CREAR_EVENTO_KAFKA: transferencia={}", transferencia.getNumeroTransferencia());

        try {
            eventProducer.publicarTransferenciaIniciada(transferencia);
            log.info("[Paso 6] Evento transferencia.iniciada publicado");

            actualizarEstado(transferencia, EstadoTransferencia.PROCESANDO,
                    "Evento Kafka publicado");

        } catch (Exception e) {
            log.error("[Paso 6] Error publicando evento: {}", e.getMessage(), e);
            actualizarEstado(transferencia, EstadoTransferencia.ERROR,
                    "Error publicando evento Kafka: " + e.getMessage());
            throw new SagaStepException(PasoSaga.CREAR_EVENTO_KAFKA,
                    "Error publicando evento transferencia.iniciada", true);
        }

        log.info("[Paso 6] CREAR_EVENTO_KAFKA completado exitosamente");
    }

    private void actualizarEstado(Transferencia transferencia, EstadoTransferencia nuevoEstado, String descripcion) {
        EstadoTransferencia estadoAnterior = transferencia.getEstado();
        transferencia.setEstado(nuevoEstado);
        transferencia.setPasoSagaActual(PasoSaga.CREAR_EVENTO_KAFKA.getCodigo());

        transferenciaRepository.save(transferencia);

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(estadoAnterior.name());
        estado.setEstadoNuevo(nuevoEstado.name());
        estado.setPasoSaga(PasoSaga.CREAR_EVENTO_KAFKA.getCodigo());
        estado.setDescripcion(descripcion);
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);
    }
}
