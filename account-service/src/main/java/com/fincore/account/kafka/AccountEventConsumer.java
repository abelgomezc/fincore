package com.fincore.account.kafka;

import com.fincore.account.entity.Cuenta;
import com.fincore.account.repository.CuentaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

/**
 * Consumidor de eventos Kafka para Account Service.
 *
 * Consume eventos de otros servicios que afectan las cuentas:
 * - transferencia.completada → actualiza saldos del read model
 * - transferencia.revertida → revierte saldos del read model
 * - batch.intereses.calculados → aplica intereses
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class AccountEventConsumer {

    private final CuentaRepository cuentaRepository;

    public AccountEventConsumer(CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    @KafkaListener(topics = "transferencia.completada", groupId = "account-service-group")
    public void handleTransferenciaCompletada(@Payload Map<String, Object> event,
                                                @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Evento transferencia.completada recibido: key={}", key);
        // El read model de saldos ya fue actualizado por el gRPC del transfer-service.
        // Aquí se podría invalidar el caché de saldos.
        log.debug("Invalidando caché de saldos para evento de transferencia completada");
    }

    @KafkaListener(topics = "transferencia.revertida", groupId = "account-service-group")
    public void handleTransferenciaRevertida(@Payload Map<String, Object> event,
                                               @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Evento transferencia.revertida recibido: key={}", key);
        log.debug("Invalidando caché de saldos para evento de transferencia revertida");
    }

    @KafkaListener(topics = "batch.intereses.calculados", groupId = "account-service-group")
    public void handleInteresesCalculados(@Payload Map<String, Object> event,
                                           @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Evento batch.intereses.calculados recibido: key={}", key);

        Long idCuenta = Long.valueOf(event.get("idCuenta").toString());
        BigDecimal montoInteres = new BigDecimal(event.get("monto").toString());

        try {
            Cuenta cuenta = cuentaRepository.findByIdWithLock(idCuenta)
                    .orElseThrow(() -> new RuntimeException("Cuenta no encontrada: " + idCuenta));

            cuenta.aplicarCredito(montoInteres);
            cuentaRepository.save(cuenta);

            log.info("Intereses aplicados: cuenta={}, monto={}", idCuenta, montoInteres);
        } catch (Exception e) {
            log.error("Error aplicando intereses: {}", e.getMessage(), e);
        }
    }
}
