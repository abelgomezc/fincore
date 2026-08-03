package com.fincore.transfer.saga;

import com.fincore.transfer.entity.Transferencia;
import com.fincore.transfer.entity.SagaLog;
import com.fincore.transfer.enums.EstadoTransferencia;
import com.fincore.transfer.enums.PasoSaga;
import com.fincore.transfer.repository.SagaLogRepository;
import com.fincore.transfer.repository.TransferenciaRepository;
import com.fincore.transfer.repository.TransferenciaEstadoRepository;
import com.fincore.transfer.saga.CompensationStep;
import com.fincore.transfer.saga.SagaStep;
import com.fincore.transfer.kafka.TransferenciaEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Saga Orchestrator — orquestador central de la transferencia.
 *
 * Ejecuta los 12 pasos de la saga secuencialmente. Si un paso falla,
 * ejecuta compensaciones en orden inverso.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class SagaOrchestrator {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;
    private final SagaLogRepository sagaLogRepository;
    private final TransferenciaEventProducer eventProducer;

    private final List<SagaStep> steps;
    private final Map<PasoSaga, CompensationStep> compensations;

    public SagaOrchestrator(
            TransferenciaRepository transferenciaRepository,
            TransferenciaEstadoRepository estadoRepository,
            SagaLogRepository sagaLogRepository,
            TransferenciaEventProducer eventProducer,
            List<SagaStep> steps,
            List<CompensationStep> compensations) {
        this.transferenciaRepository = transferenciaRepository;
        this.estadoRepository = estadoRepository;
        this.sagaLogRepository = sagaLogRepository;
        this.eventProducer = eventProducer;
        this.steps = new ArrayList<>(steps);
        this.compensations = new EnumMap<>(PasoSaga.class);

        // Ordenar steps por orden definido en PasoSaga
        this.steps.sort(java.util.Comparator.comparing(s -> s.getPaso().getOrden()));

        // Mapear compensaciones a pasos
        for (CompensationStep comp : compensations) {
            this.compensations.put(comp.getPasoCompensado(), comp);
        }

        log.info("SagaOrchestrator inicializado: {} steps, {} compensations",
                this.steps.size(), this.compensations.size());
    }

    /**
     * Ejecuta la saga completa para una transferencia.
     *
     * @param transferencia La transferencia a procesar
     * @return SagaResult con el resultado de la ejecución
     */
    public SagaResult ejecutarSaga(Transferencia transferencia) {
        SagaContext context = new SagaContext(transferencia);
        transferencia.setPasoSagaActual(PasoSaga.VALIDAR_DATOS.getCodigo());

        log.info("INICIO SAGA: transferencia={} ({} pasos)",
                transferencia.getNumeroTransferencia(), steps.size());

        List<PasoSaga> pasosEjecutados = new ArrayList<>();

        try {
            for (SagaStep step : steps) {
                PasoSaga pasoActual = step.getPaso();
                long inicio = System.currentTimeMillis();

                try {
                    context.setPasoActual(pasoActual);
                    step.execute(context);
                    pasosEjecutados.add(pasoActual);

                    // Registrar log de éxito
                    registrarSagaLog(transferencia, pasoActual, "EXITOSO",
                            "Paso ejecutado exitosamente", null, inicio);

                    log.info("PASO {} COMPLETADO: {}", pasoActual.getCodigo(),
                            transferencia.getNumeroTransferencia());

                } catch (SagaStepException e) {
                    pasosEjecutados.add(pasoActual);

                    registrarSagaLog(transferencia, pasoActual, "FALLIDO",
                            e.getMessage(), e.getClass().getSimpleName() + ": " + e.getMessage(), inicio);

                    log.error("PASO {} FALLÓ: {} — {}", pasoActual.getCodigo(),
                            transferencia.getNumeroTransferencia(), e.getMessage());

                    // Si requiere compensación, ejecutar compensaciones
                    if (e.isRequiereCompensacion()) {
                        return ejecutarCompensaciones(context, pasosEjecutados, pasoActual, e);
                    }

                    // No requiere compensación — marcar como error
                    transferencia.setEstado(EstadoTransferencia.ERROR);
                    transferencia.setMotivoRechazo(e.getMessage());
                    transferenciaRepository.save(transferencia);

                    // Publicar evento de error
                    eventProducer.publicarTransferenciaFallida(transferencia, e.getMessage());

                    return SagaResult.error(
                            "Saga falló en paso " + pasoActual.getCodigo() + ": " + e.getMessage(),
                            EstadoTransferencia.ERROR,
                            e
                    );
                }
            }

            // Saga completada exitosamente
            transferencia.setEstado(EstadoTransferencia.COMPLETADA);
            transferencia.setFechaCompletada(LocalDateTime.now());
            transferenciaRepository.save(transferencia);

            // Publicar evento de completada
            eventProducer.publicarTransferenciaCompletada(transferencia);

            log.info("SAGA COMPLETADA: {}", transferencia.getNumeroTransferencia());
            return SagaResult.success();

        } catch (Exception e) {
            log.error("ERROR INESPERADO EN SAGA: {}", e.getMessage(), e);
            transferencia.setEstado(EstadoTransferencia.ERROR);
            transferencia.setMotivoRechazo("Error inesperado: " + e.getMessage());
            transferenciaRepository.save(transferencia);

            // Intentar compensaciones
            return ejecutarCompensaciones(context, pasosEjecutados,
                    pasosEjecutados.isEmpty() ? null : pasosEjecutados.get(pasosEjecutados.size() - 1), e);
        }
    }

    /**
     * Ejecuta compensaciones en orden inverso.
     */
    private SagaResult ejecutarCompensaciones(SagaContext context, List<PasoSaga> pasosEjecutados,
                                               PasoSaga pasoFallido, Exception excepcion) {
        log.info("INICIO COMPENSACIONES: transferencia={} ({} pasos a compensar)",
                context.getTransferencia().getNumeroTransferencia(), pasosEjecutados.size());

        context.setCompensando(true);

        List<String> compensacionesEjecutadas = new ArrayList<>();

        // Iterar en orden inverso
        for (int i = pasosEjecutados.size() - 1; i >= 0; i--) {
            PasoSaga paso = pasosEjecutados.get(i);
            CompensationStep compensation = compensations.get(paso);

            if (compensation != null) {
                long inicio = System.currentTimeMillis();
                try {
                    compensation.compensate(context);
                    compensacionesEjecutadas.add(compensation.getStepName());
                    registrarSagaLog(context.getTransferencia(), paso, "COMPENSADO",
                            "Compensación ejecutada", null, inicio);
                    log.info("COMPENSACIÓN {}: {}", compensation.getStepName(),
                            context.getTransferencia().getNumeroTransferencia());
                } catch (Exception ce) {
                    registrarSagaLog(context.getTransferencia(), paso, "COMPENSACION_FALLIDA",
                            "Compensación fallida: " + ce.getMessage(),
                            ce.getClass().getSimpleName() + ": " + ce.getMessage(), inicio);
                    log.error("COMPENSACIÓN {} FALLÓ: {}", compensation.getStepName(), ce.getMessage(), ce);
                    compensacionesEjecutadas.add(compensation.getStepName() + "[FALLIDA]");
                }
            }
        }

        // Marcar transferencia como revertida
        Transferencia transferencia = context.getTransferencia();
        transferencia.setEstado(EstadoTransferencia.REVERTIDA);
        transferencia.setFechaRevertida(LocalDateTime.now());
        transferencia.setMotivoRechazo(excepcion instanceof SagaStepException
                ? excepcion.getMessage()
                : "Transferencia revertida por compensación. Compensaciones: " + compensacionesEjecutadas);
        transferenciaRepository.save(transferencia);

        // Publicar evento de revertida
        eventProducer.publicarTransferenciaFallida(transferencia,
                "Transferencia revertida. Compensaciones: " + compensacionesEjecutadas);

        log.info("COMPENSACIONES COMPLETADAS: transferencia={}",
                transferencia.getNumeroTransferencia());

        return SagaResult.error(
                "Saga revertida. Compensaciones: " + compensacionesEjecutadas,
                EstadoTransferencia.REVERTIDA,
                excepcion instanceof SagaStepException ? (SagaStepException) excepcion : null
        );
    }

    private void registrarSagaLog(Transferencia transferencia, PasoSaga paso,
                                   String estadoEjecucion, String descripcion,
                                   String errorDetalle, long inicioMs) {
        SagaLog log = new SagaLog();
        log.setIdTransferencia(transferencia.getId());
        log.setPasoSaga(paso.getCodigo());
        log.setOrden(paso.getOrden());
        log.setEstadoEjecucion(estadoEjecucion);
        log.setDetalle(descripcion);
        log.setErrorDetalle(errorDetalle);
        log.setTiempoEjecucionMs((int) (System.currentTimeMillis() - inicioMs));
        log.setFechaEjecucion(LocalDateTime.now());
        sagaLogRepository.save(log);
    }
}
