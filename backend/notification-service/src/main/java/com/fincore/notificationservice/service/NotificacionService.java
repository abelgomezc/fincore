package com.fincore.notificationservice.service;

import com.fincore.notificationservice.domain.dto.NotificacionRequest;
import com.fincore.notificationservice.domain.dto.NotificacionResponse;

import java.util.List;
import java.util.Optional;

/*
 * Interfaz de servicio para la gestión de notificaciones
 */
public interface NotificacionService {

    NotificacionResponse crearNotificacion(NotificacionRequest request);

    List<NotificacionResponse> obtenerTodasLasNotificaciones();

    Optional<NotificacionResponse> obtenerNotificacionPorId(Long id);

    List<NotificacionResponse> obtenerNotificacionesPorDestinatario(String destinatario);
}
