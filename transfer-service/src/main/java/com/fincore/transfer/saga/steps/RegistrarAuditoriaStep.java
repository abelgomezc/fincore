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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Paso 10 de la saga: REGISTRAR_AUDITORIA
 *
 * Registra el evento completo en audit-service.
 * Si falla, solo se registra un log (no bloquea la transferencia).
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class RegistrarAuditoriaStep implements SagaStep {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;

    public RegistrarAuditoriaStep(TransferenciaRepository transferenciaRepository,
                                  TransferenciaEstadoRepository estadoRepository) {
        this.transferenciaRepository = transferenciaRepository;
        this.estadoRepository = estadoRepository;
    }

    @Override
    public PasoSaga getPaso() {
        return PasoSaga.REGISTRAR_AUDITORIA;
    }

    @Override
    public void execute(SagaContext context) throws SagaStepException {
        Transferencia transferencia = context.getTransferencia();
        log.info("[Paso 10] REGISTRAR_AUDITORIA: transferencia={}", transferencia.getNumeroTransferencia());

        // La auditoría se registra vía Kafka (audit-service consume eventos)
        // El registro de auditoría ya fue enviado por el filtro del gateway
        // y por los eventos Kafka publicados en pasos anteriores.

        actualizarEstado(transferencia, EstadoTransferencia.COMPLETADA,
                "Auditoría registrada");

        log.info("[Paso 10] REGISTRAR_AUDITORIA completado");
    }

    private void actualizarEstado(Transferencia transferencia, EstadoTransferencia nuevoEstado, String descripcion) {
        EstadoTransferencia estadoAnterior = transferencia.getEstado();
        transferencia.setEstado(nuevoEstado);
        transferencia.setPasoSagaActual(PasoSaga.REGISTRAR_AUDITORIA.getCodigo());

        transferenciaRepository.save(transferencia);

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(estadoAnterior.name());
        estado.setEstadoNuevo(nuevoEstado.name());
        estado.setPasoSaga(PasoSaga.REGISTRAR_AUDITORIA.getCodigo());
        estado.setDescripcion(descripcion);
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);
    }
}
