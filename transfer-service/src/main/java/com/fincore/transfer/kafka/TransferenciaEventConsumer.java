package com.fincore.transfer.kafka;

import com.fincore.transfer.entity.Transferencia;
import com.fincore.transfer.enums.EstadoTransferencia;
import com.fincore.transfer.repository.TransferenciaRepository;
import com.fincore.transfer.websocket.WebSocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Kafka Consumer para eventos de transferencia.
 *
 * Escucha eventos de otros servicios (audit, notification)
 * para actualizar el estado de la transferencia y notificar a clientes.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class TransferenciaEventConsumer {

    private final TransferenciaRepository transferenciaRepository;
    private final WebSocketService webSocketService;
    private final ObjectMapper objectMapper;

    public TransferenciaEventConsumer(TransferenciaRepository transferenciaRepository,
                                      WebSocketService webSocketService) {
        this.transferenciaRepository = transferenciaRepository;
        this.webSocketService = webSocketService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Escucha eventos de auditoría completados.
     */
    @KafkaListener(topics = "audit.transferencia.completada", groupId = "transfer-service-group")
    public void handleAuditCompletada(@Payload Map<String, Object> evento,
                                      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                      @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Evento audit.transferencia.completada recibido: topic={}, offset={}",
                topic, offset);

        try {
            Long idTransferencia = Long.valueOf(evento.get("transferenciaId").toString());

            transferenciaRepository.findById(idTransferencia).ifPresentOrElse(
                    transferencia -> {
                        // La transferencia ya está completada — solo notificar vía WebSocket
                        webSocketService.notificarTransferenciaCompletada(transferencia);
                    },
                    () -> log.warn("Transferencia no encontrada: id={}", idTransferencia)
            );

        } catch (Exception e) {
            log.error("Error procesando evento audit.transferencia.completada: {}", e.getMessage(), e);
        }
    }

    /**
     * Escucha eventos de notificación externos.
     */
    @KafkaListener(topics = "notification.transferencia", groupId = "transfer-service-group")
    public void handleNotification(@Payload Map<String, Object> evento,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.debug("Evento notification.transferencia recibido: topic={}", topic);

        try {
            Long idTransferencia = Long.valueOf(evento.get("transferenciaId").toString());
            String estadoStr = (String) evento.get("estado");

            transferenciaRepository.findById(idTransferencia).ifPresent(transferencia -> {
                if (estadoStr != null) {
                    EstadoTransferencia estado = EstadoTransferencia.valueOf(estadoStr);
                    if (transferencia.getEstado() != estado) {
                        transferencia.setEstado(estado);
                        transferenciaRepository.save(transferencia);
                        webSocketService.notificarCambioEstado(transferencia);
                    }
                }
            });

        } catch (Exception e) {
            log.error("Error procesando evento notification.transferencia: {}", e.getMessage(), e);
        }
    }
}
