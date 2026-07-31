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
 * Paso 12 de la saga: NOTIFICAR
 *
 * Notifica a ambas partes:
 * - Publica evento transferencia.completada en Kafka (consumido por notification-service)
 * - Notificación push opcional
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class NotificarStep implements SagaStep {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;
    private final TransferenciaEventProducer eventProducer;

    public NotificarStep(TransferenciaRepository transferenciaRepository,
                         TransferenciaEstadoRepository estadoRepository,
                         TransferenciaEventProducer eventProducer) {
        this.transferenciaRepository = transferenciaRepository;
        this.estadoRepository = estadoRepository;
        this.eventProducer = eventProducer;
    }

    @Override
    public PasoSaga getPaso() {
        return PasoSaga.NOTIFICAR;
    }

    @Override
    public void execute(SagaContext context) throws SagaStepException {
        Transferencia transferencia = context.getTransferencia();
        log.info("[Paso 12] NOTIFICAR: transferencia={}", transferencia.getNumeroTransferencia());

        try {
            // Publicar evento transferencia.completada
            eventProducer.publicarTransferenciaCompletada(transferencia);
            log.info("[Paso 12] Evento transferencia.completada publicado");

            actualizarEstado(transferencia, EstadoTransferencia.COMPLETADA,
                    "Notificaciones enviadas");

            transferencia.setFechaCompletada(LocalDateTime.now());
            transferenciaRepository.save(transferencia);

        } catch (Exception e) {
            log.error("[Paso 12] Error notificando: {}", e.getMessage(), e);
            throw new SagaStepException(PasoSaga.NOTIFICAR,
                    "Error enviando notificaciones", false);
        }

        log.info("[Paso 12] NOTIFICAR completado exitosamente");
    }

    private void actualizarEstado(Transferencia transferencia, EstadoTransferencia nuevoEstado, String descripcion) {
        EstadoTransferencia estadoAnterior = transferencia.getEstado();
        transferencia.setEstado(nuevoEstado);
        transferencia.setPasoSagaActual(PasoSaga.NOTIFICAR.getCodigo());

        transferenciaRepository.save(transferencia);

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(estadoAnterior.name());
        estado.setEstadoNuevo(nuevoEstado.name());
        estado.setPasoSaga(PasoSaga.NOTIFICAR.getCodigo());
        estado.setDescripcion(descripcion);
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);
    }
}
