package com.fincore.audit.kafka;

import com.fincore.audit.entity.EventoSaga;
import com.fincore.audit.enums.AccionAuditoria;
import com.fincore.audit.enums.ResultadoAuditoria;
import com.fincore.audit.repository.EventoSagaRepository;
import com.fincore.audit.service.AuditService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Consumidor de eventos Kafka para auditoría.
 *
 * Escucha eventos del transfer-service y registra cada paso de la saga
 * en la tabla eventos_saga para auditoría completa.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class AuditEventConsumer {

    private final AuditService auditService;
    private final EventoSagaRepository eventoSagaRepository;
    private final ObjectMapper objectMapper;

    public AuditEventConsumer(AuditService auditService,
                              EventoSagaRepository eventoSagaRepository,
                              ObjectMapper objectMapper) {
        this.auditService = auditService;
        this.eventoSagaRepository = eventoSagaRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "transferencia.iniciada", groupId = "audit-service-group")
    public void handleTransferenciaIniciada(@Payload Map<String, Object> evento,
                                            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Evento transferencia.iniciada recibido: topic={}", topic);

        try {
            String requestBody = objectMapper.writeValueAsString(evento);
            auditService.registrarEvento(
                    (String) evento.get("traceId"),
                    "TRANSFER-SERVICE",
                    "/api/transferencias",
                    "POST",
                    (String) evento.get("idUsuario"),
                    "CLIENTE",
                    (String) evento.get("ipOrigen"),
                    evento.get("transferenciaId") != null ? evento.get("transferenciaId").toString() : null,
                    "TRANSFERENCIA",
                    AccionAuditoria.INICIAR_TRANSFERENCIA,
                    ResultadoAuditoria.EXITOSO,
                    requestBody,
                    202,
                    null,
                    "Transferencia iniciada correctamente"
            );
        } catch (JsonProcessingException e) {
            log.error("Error serializando evento: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "transferencia.completada", groupId = "audit-service-group")
    public void handleTransferenciaCompletada(@Payload Map<String, Object> evento,
                                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Evento transferencia.completada recibido: topic={}", topic);

        auditService.registrarEvento(
                (String) evento.get("traceId"),
                "TRANSFER-SERVICE",
                "/api/transferencias",
                "POST",
                (String) evento.get("idUsuario"),
                "CLIENTE",
                (String) evento.get("ipOrigen"),
                evento.get("transferenciaId") != null ? evento.get("transferenciaId").toString() : null,
                "TRANSFERENCIA",
                AccionAuditoria.COMPLETAR_TRANSFERENCIA,
                ResultadoAuditoria.EXITOSO,
                null,
                200,
                null,
                "Transferencia completada"
        );
    }

    @KafkaListener(topics = "transferencia.fallida", groupId = "audit-service-group")
    public void handleTransferenciaFallida(@Payload Map<String, Object> evento,
                                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Evento transferencia.fallida recibido: topic={}", topic);

        String estado = (String) evento.get("estado");
        ResultadoAuditoria resultado;
        AccionAuditoria accion;
        String detalle;

        if (estado != null && estado.contains("REVERTIDA")) {
            resultado = ResultadoAuditoria.RECHAZADO;
            accion = AccionAuditoria.REVERTIR_TRANSFERENCIA;
            detalle = "Transferencia revertida. Motivo: " + evento.get("motivo");
        } else {
            resultado = ResultadoAuditoria.FALLIDO;
            accion = AccionAuditoria.INICIAR_TRANSFERENCIA;
            detalle = "Transferencia fallida. Motivo: " + evento.get("motivo");
        }

        auditService.registrarEvento(
                (String) evento.get("traceId"),
                "TRANSFER-SERVICE",
                "/api/transferencias",
                "POST",
                (String) evento.get("idUsuario"),
                "CLIENTE",
                (String) evento.get("ipOrigen"),
                evento.get("transferenciaId") != null ? evento.get("transferenciaId").toString() : null,
                "TRANSFERENCIA",
                accion,
                resultado,
                null,
                null,
                null,
                detalle
        );

        persistirEventoSaga(evento, "FALLIDO");
    }

    @KafkaListener(topics = "audit.transferencia.completada", groupId = "audit-service-group")
    public void handleAuditTransferenciaCompletada(@Payload Map<String, Object> evento,
                                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Evento audit.transferencia.completada recibido: topic={}", topic);
        persistirEventoSaga(evento, "COMPLETADO");
    }

    private void persistirEventoSaga(Map<String, Object> evento, String estadoEjecucion) {
        try {
            EventoSaga eventoSaga = new EventoSaga();
            eventoSaga.setIdTransferencia(evento.get("transferenciaId") != null
                    ? Long.valueOf(evento.get("transferenciaId").toString()) : null);
            eventoSaga.setNumeroTransferencia((String) evento.get("numeroTransferencia"));

            String paso = (String) evento.get("pasoSagaActual");
            if (paso == null) {
                paso = (String) evento.get("paso_saga");
            }
            eventoSaga.setPasoSaga(paso);
            eventoSaga.setEstadoEjecucion(estadoEjecucion);
            eventoSaga.setDetalle((String) evento.get("detalle"));
            eventoSaga.setFechaCreacion(LocalDateTime.now());

            eventoSagaRepository.save(eventoSaga);
        } catch (Exception e) {
            log.error("Error persistiendo evento de saga: {}", e.getMessage(), e);
        }
    }
}
