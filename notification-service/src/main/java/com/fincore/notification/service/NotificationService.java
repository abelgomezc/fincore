package com.fincore.notification.service;

import com.fincore.notification.entity.Notificacion;
import com.fincore.notification.enums.TipoNotificacion;

import java.util.Map;

/**
 * Servicio de notificaciones.
 *
 * Envía notificaciones vía WebSocket, email y push.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface NotificationService {

    /**
     * Procesa un evento de transferencia y envía notificaciones
     * al usuario correspondiente.
     *
     * @param tipoNotificacion Tipo de evento
     * @param evento           Mapa con los datos del evento
     */
    void procesarEvento(TipoNotificacion tipoNotificacion, Map<String, Object> evento);

    /**
     * Notifica a un usuario vía WebSocket en tiempo real.
     *
     * @param idUsuario  ID del usuario a notificar
     * @param titulo      Título de la notificación
     * @param mensaje     Mensaje de la notificación
     * @param datosExtra  Datos adicionales JSON
     */
    void notificarWebSocket(String idUsuario, String titulo, String mensaje, String datosExtra);

    /**
     * Envía una notificación de prueba.
     */
    Notificacion enviarNotificacion(String idUsuario, TipoNotificacion tipo, String titulo,
                                    String mensaje, String datosExtra);
}
