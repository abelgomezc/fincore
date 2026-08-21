package com.fincore.document.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Productor de eventos Kafka para el document-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
public class DocumentoEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public DocumentoEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarDocumentoGenerado(Long idDocumento, String entidad, String idEntidad) {
        String mensaje = String.format("{\"evento\":\"DOCUMENTO_GENERADO\",\"idDocumento\":%d,\"entidad\":\"%s\",\"idEntidad\":\"%s\"}",
                idDocumento, entidad, idEntidad);
        kafkaTemplate.send("documento-generado", mensaje);
    }

    public void publicarDocumentoEnviadoAFirmar(Long idDocumento, String idFirmante) {
        String mensaje = String.format("{\"evento\":\"DOCUMENTO_ENVIADO_FIRMA\",\"idDocumento\":%d,\"idFirmante\":\"%s\"}",
                idDocumento, idFirmante);
        kafkaTemplate.send("documento-enviado-firma", mensaje);
    }

    public void publicarFirmaFinalizada(Long idDocumento, String estado) {
        String mensaje = String.format("{\"evento\":\"FIRMA_FINALIZADA\",\"idDocumento\":%d,\"estado\":\"%s\"}",
                idDocumento, estado);
        kafkaTemplate.send("documento-firma-finalizada", mensaje);
    }
}
