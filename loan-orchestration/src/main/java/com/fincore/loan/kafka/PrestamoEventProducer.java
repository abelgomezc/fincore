package com.fincore.loan.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Productor de eventos Kafka para el loan-orchestration-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
public class PrestamoEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public PrestamoEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarSolicitudCreada(Long idSolicitud, String numeroSolicitud) {
        String mensaje = String.format("{\"evento\":\"SOLICITUD_PRESTAMO_CREADA\",\"idSolicitud\":%d,\"numeroSolicitud\":\"%s\"}", idSolicitud, numeroSolicitud);
        kafkaTemplate.send("prestamo-solicitud", mensaje);
    }

    public void publicarFlujoIniciado(Long idSolicitud) {
        String mensaje = String.format("{\"evento\":\"FLUJO_PRESTAMO_INICIADO\",\"idSolicitud\":%d}", idSolicitud);
        kafkaTemplate.send("prestamo-evento", mensaje);
    }

    public void publicarIdentidadValidada(Long idSolicitud) {
        String mensaje = String.format("{\"evento\":\"IDENTIDAD_VALIDADA\",\"idSolicitud\":%d}", idSolicitud);
        kafkaTemplate.send("prestamo-evento", mensaje);
    }

    public void publicarBuroConsultado(Long idSolicitud, Integer score) {
        String mensaje = String.format("{\"evento\":\"BURO_CONSULTADO\",\"idSolicitud\":%d,\"score\":%d}", idSolicitud, score);
        kafkaTemplate.send("prestamo-evento", mensaje);
    }

    public void publicarRiesgoEvaluado(Long idSolicitud, String estado) {
        String mensaje = String.format("{\"evento\":\"RIESGO_EVALUADO\",\"idSolicitud\":%d,\"estado\":\"%s\"}", idSolicitud, estado);
        kafkaTemplate.send("prestamo-evento", mensaje);
    }

    public void publicarSolicitudAprobada(Long idSolicitud) {
        String mensaje = String.format("{\"evento\":\"SOLICITUD_APROBADA\",\"idSolicitud\":%d}", idSolicitud);
        kafkaTemplate.send("prestamo-evento", mensaje);
    }

    public void publicarSolicitudRechazada(Long idSolicitud) {
        String mensaje = String.format("{\"evento\":\"SOLICITUD_RECHAZADA\",\"idSolicitud\":%d}", idSolicitud);
        kafkaTemplate.send("prestamo-evento", mensaje);
    }

    public void publicarContratoGenerado(Long idSolicitud) {
        String mensaje = String.format("{\"evento\":\"CONTRATO_GENERADO\",\"idSolicitud\":%d}", idSolicitud);
        kafkaTemplate.send("prestamo-evento", mensaje);
    }

    public void publicarContratoEnviadoAFirmar(Long idSolicitud) {
        String mensaje = String.format("{\"evento\":\"CONTRATO_ENVIADO_FIRMA\",\"idSolicitud\":%d}", idSolicitud);
        kafkaTemplate.send("prestamo-evento", mensaje);
    }

    public void publicarPrestamoDesembolsado(Long idSolicitud, java.math.BigDecimal monto) {
        String mensaje = String.format("{\"evento\":\"PRESTAMO_DESEMBOLSADO\",\"idSolicitud\":%d,\"monto\":%s}", idSolicitud, monto);
        kafkaTemplate.send("prestamo-evento", mensaje);
    }
}
