package com.fincore.account.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaSendCallback;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Productor de eventos Kafka para Account Service.
 *
 * Publica eventos cuando cambian los saldos de las cuentas:
 * - cuenta.creada
 * - cuenta.bloqueada
 * - cuenta.saldo.actualizado
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class AccountEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AccountEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarCuentaCreada(Long idCuenta, String numeroCuenta, Long idCliente) {
        CuentaCreadaEvent event = CuentaCreadaEvent.builder()
                .idCuenta(idCuenta)
                .numeroCuenta(numeroCuenta)
                .idCliente(idCliente)
                .timestamp(Instant.now().toEpochMilli())
                .build();

        kafkaTemplate.send("cuenta.creada", idCuenta.toString(), event)
                .addCallback(new KafkaSendCallback<>() {
                    @Override
                    public void onSuccess(org.springframework.kafka.support.SendResult<String, Object> result) {
                        log.info("Evento cuenta.creada publicado: ID={}", idCuenta);
                    }

                    @Override
                    public void onFailure(org.springframework.kafka.support.KafkaException exception) {
                        log.error("Error publicando evento cuenta.creada: ID={}", idCuenta, exception);
                    }
                });
    }

    public void publicarCuentaBloqueada(Long idCuenta, String numeroCuenta, String motivo) {
        CuentaBloqueadaEvent event = CuentaBloqueadaEvent.builder()
                .idCuenta(idCuenta)
                .numeroCuenta(numeroCuenta)
                .motivo(motivo)
                .timestamp(Instant.now().toEpochMilli())
                .build();

        kafkaTemplate.send("cuenta.bloqueada", idCuenta.toString(), event);
        log.info("Evento cuenta.bloqueada publicado: ID={}", idCuenta);
    }

    public void publicarSaldoActualizado(Long idCuenta, String numeroCuenta,
                                          BigDecimal saldoContable, BigDecimal saldoDisponible,
                                          BigDecimal saldoRetenido) {
        SaldoActualizadoEvent event = SaldoActualizadoEvent.builder()
                .idCuenta(idCuenta)
                .numeroCuenta(numeroCuenta)
                .saldoContable(saldoContable)
                .saldoDisponible(saldoDisponible)
                .saldoRetenido(saldoRetenido)
                .timestamp(Instant.now().toEpochMilli())
                .build();

        kafkaTemplate.send("cuenta.saldo.actualizado", idCuenta.toString(), event);
        log.debug("Evento cuenta.saldo.actualizado publicado: ID={}, disponible={}", idCuenta, saldoDisponible);
    }

    @lombok.Getter
    @lombok.Setter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CuentaCreadaEvent {
        private Long idCuenta;
        private String numeroCuenta;
        private Long idCliente;
        private Long timestamp;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class CuentaBloqueadaEvent {
        private Long idCuenta;
        private String numeroCuenta;
        private String motivo;
        private Long timestamp;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class SaldoActualizadoEvent {
        private Long idCuenta;
        private String numeroCuenta;
        private BigDecimal saldoContable;
        private BigDecimal saldoDisponible;
        private BigDecimal saldoRetenido;
        private Long timestamp;
    }
}
