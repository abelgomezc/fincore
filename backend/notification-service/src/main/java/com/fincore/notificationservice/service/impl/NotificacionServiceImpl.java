package com.fincore.notificationservice.service.impl;

import com.fincore.notificationservice.domain.dto.NotificacionRequest;
import com.fincore.notificationservice.domain.dto.NotificacionResponse;
import com.fincore.notificationservice.domain.entity.Notificacion;
import com.fincore.notificationservice.domain.enums.CanalNotificacion;
import com.fincore.notificationservice.domain.enums.EstadoNotificacion;
import com.fincore.notificationservice.repository.NotificacionRepository;
import com.fincore.notificationservice.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * Implementación del servicio de gestión de notificaciones
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public NotificacionResponse crearNotificacion(NotificacionRequest request) {
        log.info("Creando notificación para destinatario: {}", request.getDestinatario());

        Notificacion notificacion = Notificacion.builder()
                .canal(request.getCanal())
                .destinatario(request.getDestinatario())
                .asunto(request.getAsunto())
                .cuerpo(request.getCuerpo())
                .estado(EstadoNotificacion.PENDIENTE)
                .intentos(0)
                .build();

        Notificacion notificacionGuardada = notificacionRepository.save(notificacion);
        log.info("Notificación creada con ID: {}", notificacionGuardada.getId());

        NotificacionResponse response = mapearANotificacionResponse(notificacionGuardada);

        kafkaTemplate.send("${KAFKA_TOPIC_NOTIFICACION_SOLICITADA:notificacion-solicitada}", response);
        log.info("Evento notificacion-solicitada enviado a Kafka para ID: {}", notificacionGuardada.getId());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponse> obtenerTodasLasNotificaciones() {
        log.info("Obteniendo todas las notificaciones");
        return notificacionRepository.findAll()
                .stream()
                .map(this::mapearANotificacionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificacionResponse> obtenerNotificacionPorId(Long id) {
        log.info("Obteniendo notificación por ID: {}", id);
        return notificacionRepository.findById(id)
                .map(this::mapearANotificacionResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponse> obtenerNotificacionesPorDestinatario(String destinatario) {
        log.info("Obteniendo notificaciones por destinatario: {}", destinatario);
        return notificacionRepository.findAll()
                .stream()
                .filter(n -> n.getDestinatario().equals(destinatario))
                .map(this::mapearANotificacionResponse)
                .collect(Collectors.toList());
    }

    private NotificacionResponse mapearANotificacionResponse(Notificacion notificacion) {
        return NotificacionResponse.builder()
                .id(notificacion.getId())
                .canal(notificacion.getCanal())
                .destinatario(notificacion.getDestinatario())
                .asunto(notificacion.getAsunto())
                .cuerpo(notificacion.getCuerpo())
                .estado(notificacion.getEstado())
                .intentos(notificacion.getIntentos())
                .respuestaExterna(notificacion.getRespuestaExterna())
                .creadoEn(notificacion.getCreadoEn())
                .build();
    }
}
