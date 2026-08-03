package com.fincore.notification.service.impl;

import com.fincore.notification.entity.Notificacion;
import com.fincore.notification.enums.CanalNotificacion;
import com.fincore.notification.enums.EstadoNotificacion;
import com.fincore.notification.enums.TipoNotificacion;
import com.fincore.notification.repository.NotificacionRepository;
import com.fincore.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Implementación del servicio de notificaciones.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificacionRepository notificacionRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public NotificationServiceImpl(NotificacionRepository notificacionRepository,
                                   SimpMessagingTemplate messagingTemplate,
                                   ObjectMapper objectMapper) {
        this.notificacionRepository = notificacionRepository;
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void procesarEvento(TipoNotificacion tipoNotificacion, Map<String, Object> evento) {
        String idUsuario = (String) evento.get("idUsuario");
        Long idTransferencia = evento.get("transferenciaId") != null
                ? Long.valueOf(evento.get("transferenciaId").toString()) : null;
        String numeroTransferencia = (String) evento.get("numeroTransferencia");

        String titulo = tipoNotificacion.getTitulo();
        String mensaje = construirMensaje(tipoNotificacion, evento);

        try {
            String datosExtra = objectMapper.writeValueAsString(evento);

            if (idUsuario != null) {
                notificarWebSocket(idUsuario, titulo, mensaje, datosExtra);
            }

            Notificacion notificacion = new Notificacion();
            notificacion.setTipoNotificacion(tipoNotificacion.name());
            notificacion.setIdUsuario(idUsuario);
            notificacion.setIdTransferencia(idTransferencia);
            notificacion.setNumeroTransferencia(numeroTransferencia);
            notificacion.setCanal(CanalNotificacion.WEBSOCKET.name());
            notificacion.setEstado(EstadoNotificacion.ENVIADA.name());
            notificacion.setTitulo(titulo);
            notificacion.setMensaje(mensaje);
            notificacion.setDatosAdicionales(datosExtra);
            notificacion.setFechaEnvio(LocalDateTime.now());
            notificacion.setFechaCreacion(LocalDateTime.now());
            notificacion.setFechaActualizacion(LocalDateTime.now());
            notificacionRepository.save(notificacion);

            log.info("Notificación enviada: tipo={}, usuario={}, transferencia={}",
                    tipoNotificacion, idUsuario, idTransferencia);

        } catch (Exception e) {
            log.error("Error enviando notificación: {}", e.getMessage(), e);
        }
    }

    @Override
    public void notificarWebSocket(String idUsuario, String titulo, String mensaje, String datosExtra) {
        try {
            Map<String, Object> notification = Map.of(
                    "titulo", titulo,
                    "mensaje", mensaje,
                    "datosExtra", datosExtra != null ? datosExtra : "",
                    "timestamp", LocalDateTime.now().toString()
            );
            messagingTemplate.convertAndSend("/topic/notifications/" + idUsuario, notification);
        } catch (Exception e) {
            log.error("Error enviando notificación WebSocket a usuario {}: {}", idUsuario, e.getMessage(), e);
        }
    }

    private String construirMensaje(TipoNotificacion tipo, Map<String, Object> evento) {
        return switch (tipo) {
            case TRANSFERENCIA_INICIADA -> "Se ha iniciado una transferencia por $" + evento.get("monto");
            case TRANSFERENCIA_COMPLETADA -> "Transferencia completada exitosamente por $" + evento.get("monto");
            case TRANSFERENCIA_FALLIDA -> "Transferencia fallida: " + evento.get("motivo");
            case TRANSFERENCIA_REVERTIDA -> "Transferencia revertida: " + evento.get("motivo");
        };
    }

    @Override
    public Notificacion enviarNotificacion(String idUsuario, TipoNotificacion tipo, String titulo,
                                           String mensaje, String datosExtra) {
        Notificacion notificacion = new Notificacion();
        notificacion.setTipoNotificacion(tipo.name());
        notificacion.setIdUsuario(idUsuario);
        notificacion.setCanal(CanalNotificacion.WEBSOCKET.name());
        notificacion.setEstado(EstadoNotificacion.ENVIADA.name());
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setDatosAdicionales(datosExtra);
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setFechaCreacion(LocalDateTime.now());
        notificacion.setFechaActualizacion(LocalDateTime.now());
        return notificacionRepository.save(notificacion);
    }
}
