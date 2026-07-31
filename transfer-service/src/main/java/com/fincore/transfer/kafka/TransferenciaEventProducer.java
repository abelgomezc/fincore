package com.fincore.transfer.kafka;

import com.fincore.transfer.entity.Transferencia;
import com.fincore.transfer.enums.EstadoTransferencia;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaSendCallback;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Kafka Producer para eventos de transferencia.
 *
 * Publica eventos en los topics:
 * - transferencia.iniciada
 * - transferencia.completada
 * - transferencia.fallida
 * - transferencia.revertida
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class TransferenciaEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TransferenciaEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Publica evento transferencia.iniciada.
     * Consumido por: audit-service, fraud-service, notification-service
     */
    public CompletableFuture<Void> publicarTransferenciaIniciada(Transferencia transferencia) {
        Map<String, Object> evento = new HashMap<>();
        evento.put("eventType", "transferencia.iniciada");
        evento.put("transferenciaId", transferencia.getId());
        evento.put("numeroTransferencia", transferencia.getNumeroTransferencia());
        evento.put("idCuentaOrigen", transferencia.getIdCuentaOrigen());
        evento.put("numeroCuentaOrigen", transferencia.getNumeroCuentaOrigen());
        evento.put("idCuentaDestino", transferencia.getIdCuentaDestino());
        evento.put("numeroCuentaDestino", transferencia.getNumeroCuentaDestino());
        evento.put("nombreBeneficiario", transferencia.getNombreBeneficiario());
        evento.put("monto", transferencia.getMonto());
        evento.put("moneda", transferencia.getMoneda());
        evento.put("comision", transferencia.getComision());
        evento.put("concepto", transferencia.getConcepto());
        evento.put("ipOrigen", transferencia.getIpOrigen());
        evento.put("dispositivo", transferencia.getDispositivo());
        evento.put("traceId", transferencia.getTraceId());
        evento.put("idUsuario", transferencia.getIdUsuario());
        evento.put("timestamp", LocalDateTime.now().toString());

        log.info("Publicando evento transferencia.iniciada: transferencia={}",
                transferencia.getNumeroTransferencia());

        String key = String.valueOf(transferencia.getId());
        return CompletableFuture.supplyAsync(() -> {
            kafkaTemplate.send("transferencia.iniciada", key, evento)
                    .addCallback(new KafkaSendCallback<String, Object>() {
                        @Override
                        public void onSuccess(org.springframework.kafka.support.SendResult<String, Object> result) {
                            log.info("Evento transferencia.iniciada enviado: topic={}, offset={}",
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().offset());
                        }

                        @Override
                        public void onFailure(Throwable ex) {
                            log.error("Error enviando evento transferencia.iniciada: {}", ex.getMessage(), ex);
                        }
                    });
            return null;
        });
    }

    /**
     * Publica evento transferencia.completada.
     * Consumido por: notification-service, audit-service
     */
    public CompletableFuture<Void> publicarTransferenciaCompletada(Transferencia transferencia) {
        Map<String, Object> evento = new HashMap<>();
        evento.put("eventType", "transferencia.completada");
        evento.put("transferenciaId", transferencia.getId());
        evento.put("numeroTransferencia", transferencia.getNumeroTransferencia());
        evento.put("idCuentaOrigen", transferencia.getIdCuentaOrigen());
        evento.put("idCuentaDestino", transferencia.getIdCuentaDestino());
        evento.put("monto", transferencia.getMonto());
        evento.put("moneda", transferencia.getMoneda());
        evento.put("traceId", transferencia.getTraceId());
        evento.put("estado", EstadoTransferencia.COMPLETADA.name());
        evento.put("fechaCompletada", LocalDateTime.now().toString());

        log.info("Publicando evento transferencia.completada: transferencia={}",
                transferencia.getNumeroTransferencia());

        String key = String.valueOf(transferencia.getId());
        CompletableFuture<Void> future = new CompletableFuture<>();
        kafkaTemplate.send("transferencia.completada", key, evento)
                .addCallback(new KafkaSendCallback<String, Object>() {
                    @Override
                    public void onSuccess(org.springframework.kafka.support.SendResult<String, Object> result) {
                        log.info("Evento transferencia.completada enviado: offset={}",
                                result.getRecordMetadata().offset());
                        future.complete(null);
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        log.error("Error enviando evento transferencia.completada: {}", ex.getMessage(), ex);
                        future.completeExceptionally(ex);
                    }
                });
        return future;
    }

    /**
     * Publica evento transferencia.fallida / transferencia.revertida.
     * Consumido por: notification-service, audit-service
     */
    public CompletableFuture<Void> publicarTransferenciaFallida(Transferencia transferencia, String motivo) {
        Map<String, Object> evento = new HashMap<>();
        evento.put("eventType", "transferencia.fallida");
        evento.put("transferenciaId", transferencia.getId());
        evento.put("numeroTransferencia", transferencia.getNumeroTransferencia());
        evento.put("idCuentaOrigen", transferencia.getIdCuentaOrigen());
        evento.put("idCuentaDestino", transferencia.getIdCuentaDestino());
        evento.put("monto", transferencia.getMonto());
        evento.put("moneda", transferencia.getMoneda());
        evento.put("traceId", transferencia.getTraceId());
        evento.put("estado", transferencia.getEstado().name());
        evento.put("motivo", motivo);
        evento.put("timestamp", LocalDateTime.now().toString());

        log.info("Publicando evento transferencia.fallida: transferencia={}, estado={}",
                transferencia.getNumeroTransferencia(), transferencia.getEstado());

        String key = String.valueOf(transferencia.getId());
        CompletableFuture<Void> future = new CompletableFuture<>();
        kafkaTemplate.send("transferencia.fallida", key, evento)
                .addCallback(new KafkaSendCallback<String, Object>() {
                    @Override
                    public void onSuccess(org.springframework.kafka.support.SendResult<String, Object> result) {
                        log.info("Evento transferencia.fallida enviado: offset={}",
                                result.getRecordMetadata().offset());
                        future.complete(null);
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        log.error("Error enviando evento transferencia.fallida: {}", ex.getMessage(), ex);
                        future.completeExceptionally(ex);
                    }
                });
        return future;
    }
}
