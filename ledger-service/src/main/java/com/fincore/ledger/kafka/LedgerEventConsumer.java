package com.fincore.ledger.kafka;

import com.fincore.ledger.dto.AsientoDTO;
import com.fincore.ledger.dto.LineaAsientoDTO;
import com.fincore.ledger.service.LedgerService;
import com.fincore.ledger.service.AsientoFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Consumidor de eventos Kafka para Ledger Service.
 *
 * Consume eventos de Kafka y crea los asientos contables correspondientes:
 * - transferencia.fondos.reservados → asiento de retención
 * - transferencia.debito.ejecutado → asiento de débito
 * - transferencia.credito.ejecutado → asiento de crédito
 * - transferencia.revertida → asiento de reversion
 * - cuenta.creada → asiento de apertura (si hay saldo inicial)
 * - batch.intereses.calculados → asiento de intereses
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class LedgerEventConsumer {

    private final LedgerService ledgerService;
    private final AsientoFactory asientoFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LedgerEventConsumer(LedgerService ledgerService, AsientoFactory asientoFactory) {
        this.ledgerService = ledgerService;
        this.asientoFactory = asientoFactory;
    }

    @KafkaListener(topics = "transferencia.fondos.reservados", groupId = "ledger-service-group")
    public void handleFondosReservados(@Payload Map<String, Object> event,
                                       @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {
        log.info("Evento transferencia.fondos.reservados: key={}", key);
        try {
            Long idCuentaOrigen = Long.valueOf(event.get("idCuentaOrigen").toString());
            BigDecimal monto = new BigDecimal(event.get("monto").toString());
            String traceId = (String) event.get("traceId");
            Long idTransferencia = Long.valueOf(event.get("idTransferencia").toString());

            List<LineaAsientoDTO> lineas = asientoFactory.crearAsientoRetencion(idCuentaOrigen, monto);

            ledgerService.crearAsiento(lineas,
                    "Retención de fondos — transferencia " + idTransferencia,
                    "TRANSFERENCIA",
                    idTransferencia,
                    "system",
                    (String) event.get("ipOrigen"),
                    traceId);

            log.info("Asiento de retención creado para transferencia: {}", idTransferencia);
        } catch (Exception e) {
            log.error("Error procesando transferencia.fondos.reservados: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "transferencia.debito.ejecutado", groupId = "ledger-service-group")
    public void handleDebitoEjecutado(@Payload Map<String, Object> event,
                                      @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {
        log.info("Evento transferencia.debito.ejecutado: key={}", key);
        try {
            Long idCuentaOrigen = Long.valueOf(event.get("idCuentaOrigen").toString());
            BigDecimal monto = new BigDecimal(event.get("monto").toString());
            String traceId = (String) event.get("traceId");
            Long idTransferencia = Long.valueOf(event.get("idTransferencia").toString());

            List<LineaAsientoDTO> lineas = asientoFactory.crearAsientoTransferencia(idCuentaOrigen, null, monto);
            // El débito es solo al origen, el crédito se hará en el siguiente evento

            ledgerService.crearAsiento(lineas,
                    "Débito ejecutado — transferencia " + idTransferencia,
                    "TRANSFERENCIA",
                    idTransferencia,
                    "system",
                    (String) event.get("ipOrigen"),
                    traceId);

            log.info("Asiento de débito creado para transferencia: {}", idTransferencia);
        } catch (Exception e) {
            log.error("Error procesando transferencia.debito.ejecutado: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "transferencia.credito.ejecutado", groupId = "ledger-service-group")
    public void handleCreditoEjecutado(@Payload Map<String, Object> event,
                                       @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {
        log.info("Evento transferencia.credito.ejecutado: key={}", key);
        try {
            Long idCuentaDestino = Long.valueOf(event.get("idCuentaDestino").toString());
            BigDecimal monto = new BigDecimal(event.get("monto").toString());
            String traceId = (String) event.get("traceId");
            Long idTransferencia = Long.valueOf(event.get("idTransferencia").toString());

            List<LineaAsientoDTO> lineas = asientoFactory.crearAsientoTransferencia(null, idCuentaDestino, monto);

            ledgerService.crearAsiento(lineas,
                    "Crédito ejecutado — transferencia " + idTransferencia,
                    "TRANSFERENCIA",
                    idTransferencia,
                    "system",
                    (String) event.get("ipOrigen"),
                    traceId);

            log.info("Asiento de crédito creado para transferencia: {}", idTransferencia);
        } catch (Exception e) {
            log.error("Error procesando transferencia.credito.ejecutado: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "transferencia.revertida", groupId = "ledger-service-group")
    public void handleTransferenciaRevertida(@Payload Map<String, Object> event,
                                             @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {
        log.info("Evento transferencia.revertida: key={}", key);
        try {
            Long idCuentaOrigen = Long.valueOf(event.get("idCuentaOrigen").toString());
            Long idCuentaDestino = Long.valueOf(event.get("idCuentaDestino").toString());
            BigDecimal monto = new BigDecimal(event.get("monto").toString());
            String traceId = (String) event.get("traceId");
            Long idTransferencia = Long.valueOf(event.get("idTransferencia").toString());

            List<LineaAsientoDTO> lineas = asientoFactory.crearAsientoReversionTransferencia(idCuentaOrigen, idCuentaDestino, monto);

            ledgerService.crearAsiento(lineas,
                    "Reversión de transferencia " + idTransferencia,
                    "TRANSFERENCIA_REVERTIDA",
                    idTransferencia,
                    "system",
                    (String) event.get("ipOrigen"),
                    traceId);

            log.info("Asiento de reversión creado para transferencia: {}", idTransferencia);
        } catch (Exception e) {
            log.error("Error procesando transferencia.revertida: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "batch.intereses.calculados", groupId = "ledger-service-group")
    public void handleInteresesCalculados(@Payload Map<String, Object> event,
                                          @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {
        log.info("Evento batch.intereses.calculados: key={}", key);
        try {
            Long idCuenta = Long.valueOf(event.get("idCuenta").toString());
            BigDecimal monto = new BigDecimal(event.get("monto").toString());
            String traceId = (String) event.get("traceId");

            List<LineaAsientoDTO> lineas = asientoFactory.crearAsientoIntereses(idCuenta, monto);

            ledgerService.crearAsiento(lineas,
                    "Intereses calculados por batch nocturno",
                    "INTERESES",
                    idCuenta,
                    "batch-service",
                    "system",
                    traceId);

            log.info("Asiento de intereses creado: cuenta={}, monto={}", idCuenta, monto);
        } catch (Exception e) {
            log.error("Error procesando batch.intereses.calculados: {}", e.getMessage(), e);
        }
    }
}
