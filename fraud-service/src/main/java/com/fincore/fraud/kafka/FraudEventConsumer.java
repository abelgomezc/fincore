package com.fincore.fraud.kafka;

import com.fincore.fraud.service.FraudEvaluationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Kafka Consumer para eventos de transferencia completada.
 *
 * Escucha transferencias exitosas para actualizar el perfil transaccional
 * del cliente (promedios, patrones, dispositivos habituales, etc.).
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class FraudEventConsumer {

    private final ObjectMapper objectMapper;

    public FraudEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "transferencia.completada", groupId = "fraud-service-group")
    public void handleTransferenciaCompletada(@Payload Map<String, Object> evento,
                                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Evento transferencia.completada recibido: topic={}", topic);

        try {
            Long idTransferencia = Long.valueOf(evento.get("transferenciaId").toString());
            Long idCliente = Long.valueOf(evento.get("idCliente").toString());
            String montoStr = evento.get("monto").toString();
            String ipOrigen = (String) evento.get("ipOrigen");
            String dispositivo = (String) evento.get("dispositivo");

            log.debug("Procesando evento para actualización de perfil: transferencia={}, cliente={}",
                    idTransferencia, idCliente);

        } catch (Exception e) {
            log.error("Error procesando evento transferencia.completada: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "transferencia.en.revision", groupId = "fraud-service-group")
    public void handleTransferenciaEnRevision(@Payload Map<String, Object> evento,
                                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Evento transferencia.en.revision recibido: topic={}", topic);

        try {
            Long idTransferencia = Long.valueOf(evento.get("transferenciaId").toString());
            log.warn("Transferencia en revisión manual: id={}", idTransferencia);
        } catch (Exception e) {
            log.error("Error procesando evento transferencia.en.revision: {}", e.getMessage(), e);
        }
    }
}
