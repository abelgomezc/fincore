package com.fincore.notification.kafka;

import com.fincore.notification.enums.TipoNotificacion;
import com.fincore.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumidor de eventos Kafka para notificaciones.
 *
 * Escucha eventos de transferencia en tiempo real y dispara notificaciones.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class NotificationEventConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public NotificationEventConsumer(NotificationService notificationService,
                                     ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "transferencia.iniciada", groupId = "notification-service-group")
    public void handleTransferenciaIniciada(@Payload Map<String, Object> evento,
                                            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Evento transferencia.iniciada recibido: topic={}", topic);
        TipoNotificacion tipo = TipoNotificacion.fromTopico(topic);
        if (tipo != null) {
            notificationService.procesarEvento(tipo, evento);
        }
    }

    @KafkaListener(topics = "transferencia.completada", groupId = "notification-service-group")
    public void handleTransferenciaCompletada(@Payload Map<String, Object> evento,
                                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Evento transferencia.completada recibido: topic={}", topic);
        TipoNotificacion tipo = TipoNotificacion.fromTopico(topic);
        if (tipo != null) {
            notificationService.procesarEvento(tipo, evento);
        }
    }

    @KafkaListener(topics = "transferencia.fallida", groupId = "notification-service-group")
    public void handleTransferenciaFallida(@Payload Map<String, Object> evento,
                                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Evento transferencia.fallida recibido: topic={}", topic);
        TipoNotificacion tipo = TipoNotificacion.fromTopico(topic);
        if (tipo != null) {
            notificationService.procesarEvento(tipo, evento);
        }
    }
}
