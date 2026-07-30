package com.fincore.notificationservice.domain.dto;

import com.fincore.notificationservice.domain.enums.CanalNotificacion;
import com.fincore.notificationservice.domain.enums.EstadoNotificacion;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/*
 * DTO de respuesta con los datos de una notificación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionResponse {

    private Long id;
    private CanalNotificacion canal;
    private String destinatario;
    private String asunto;
    private String cuerpo;
    private EstadoNotificacion estado;
    private Integer intentos;
    private String respuestaExterna;
    private LocalDateTime creadoEn;
}
