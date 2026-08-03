package com.fincore.customer.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;

/**
 * Productor de eventos Kafka para Customer Service.
 *
 * Publica eventos de dominio relacionados con clientes y KYC:
 * - cliente.creado
 * - cliente.actualizado
 * - cliente.bloqueado
 * - cliente.desbloqueado
 * - cliente.desactivado
 * - cliente.kyc.aprobado
 * - cliente.kyc.rechazado
 * - cliente.kyc.en_revision
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class ClienteEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ClienteEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarClienteCreado(Long idCliente, String nombreCompleto, String email) {
        ClienteCreadoEvent event = ClienteCreadoEvent.builder()
                .idCliente(idCliente)
                .nombreCompleto(nombreCompleto)
                .email(email)
                .timestamp(Instant.now().toEpochMilli())
                .build();

        kafkaTemplate.send("cliente.creado", idCliente.toString(), event)
                .thenAccept(result -> {
                    log.info("Evento cliente.creado publicado: ID={}", idCliente);
                })
                .exceptionally(exception -> {
                    log.error("Error publicando evento cliente.creado: ID={}", idCliente, exception);
                    return null;
                });
    }

    public void publicarClienteActualizado(Long idCliente, String nombreCompleto, String email) {
        ClienteActualizadoEvent event = ClienteActualizadoEvent.builder()
                .idCliente(idCliente)
                .nombreCompleto(nombreCompleto)
                .email(email)
                .timestamp(Instant.now().toEpochMilli())
                .build();

        kafkaTemplate.send("cliente.actualizado", idCliente.toString(), event);
        log.info("Evento cliente.actualizado publicado: ID={}", idCliente);
    }

    public void publicarClienteBloqueado(Long idCliente, String motivo) {
        ClienteBloqueadoEvent event = ClienteBloqueadoEvent.builder()
                .idCliente(idCliente)
                .motivo(motivo)
                .timestamp(Instant.now().toEpochMilli())
                .build();

        kafkaTemplate.send("cliente.bloqueado", idCliente.toString(), event);
        log.info("Evento cliente.bloqueado publicado: ID={}", idCliente);
    }

    public void publicarClienteDesbloqueado(Long idCliente) {
        ClienteDesbloqueadoEvent event = ClienteDesbloqueadoEvent.builder()
                .idCliente(idCliente)
                .timestamp(Instant.now().toEpochMilli())
                .build();

        kafkaTemplate.send("cliente.desbloqueado", idCliente.toString(), event);
        log.info("Evento cliente.desbloqueado publicado: ID={}", idCliente);
    }

    public void publicarClienteDesactivado(Long idCliente) {
        ClienteDesactivadoEvent event = ClienteDesactivadoEvent.builder()
                .idCliente(idCliente)
                .timestamp(Instant.now().toEpochMilli())
                .build();

        kafkaTemplate.send("cliente.desactivado", idCliente.toString(), event);
        log.info("Evento cliente.desactivado publicado: ID={}", idCliente);
    }

    public void publicarClienteKycAprobado(Long idCliente) {
        ClienteKycAprobadoEvent event = ClienteKycAprobadoEvent.builder()
                .idCliente(idCliente)
                .timestamp(Instant.now().toEpochMilli())
                .build();

        kafkaTemplate.send("cliente.kyc.aprobado", idCliente.toString(), event);
        log.info("Evento cliente.kyc.aprobado publicado: ID={}", idCliente);
    }

    public void publicarClienteKycRechazado(Long idCliente) {
        ClienteKycRechazadoEvent event = ClienteKycRechazadoEvent.builder()
                .idCliente(idCliente)
                .timestamp(Instant.now().toEpochMilli())
                .build();

        kafkaTemplate.send("cliente.kyc.rechazado", idCliente.toString(), event);
        log.info("Evento cliente.kyc.rechazado publicado: ID={}", idCliente);
    }

    public void publicarClienteKycEnRevision(Long idCliente) {
        ClienteKycEnReviewEvent event = ClienteKycEnReviewEvent.builder()
                .idCliente(idCliente)
                .timestamp(Instant.now().toEpochMilli())
                .build();

        kafkaTemplate.send("cliente.kyc.en_revision", idCliente.toString(), event);
        log.info("Evento cliente.kyc.en_revision publicado: ID={}", idCliente);
    }

    @lombok.Getter
    @lombok.Setter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ClienteCreadoEvent {
        private Long idCliente;
        private String nombreCompleto;
        private String email;
        private Long timestamp;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class ClienteActualizadoEvent {
        private Long idCliente;
        private String nombreCompleto;
        private String email;
        private Long timestamp;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class ClienteBloqueadoEvent {
        private Long idCliente;
        private String motivo;
        private Long timestamp;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class ClienteDesbloqueadoEvent {
        private Long idCliente;
        private Long timestamp;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class ClienteDesactivadoEvent {
        private Long idCliente;
        private Long timestamp;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class ClienteKycAprobadoEvent {
        private Long idCliente;
        private Long timestamp;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class ClienteKycRechazadoEvent {
        private Long idCliente;
        private Long timestamp;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class ClienteKycEnReviewEvent {
        private Long idCliente;
        private Long timestamp;
    }
}
