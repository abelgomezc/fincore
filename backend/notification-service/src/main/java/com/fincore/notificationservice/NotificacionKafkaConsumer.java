package com.fincore.notificationservice;

import com.fincore.notificationservice.domain.dto.NotificacionResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/*
 * Consumer de eventos de Kafka para notificaciones
 */
@Component
public class NotificacionKafkaConsumer {

    @KafkaListener(topics = "${KAFKA_TOPIC_NOTIFICACION_SOLICITADA:notificacion-solicitada}", groupId = "${KAFKA_CONSUMER_GROUP_ID:notification-service-group}")
    public void consumirNotificacionSolicitada(NotificacionResponse notificacion) {
        // Lógica de consumo de evento de notificación solicitada
    }
}
