package com.fincore.notificationservice.domain.dto;

import com.fincore.notificationservice.domain.enums.CanalNotificacion;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/*
 * DTO de request para crear una nueva notificación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionRequest {

    @NotNull(message = "El canal es obligatorio")
    private CanalNotificacion canal;

    @NotBlank(message = "El destinatario es obligatorio")
    @Email(message = "El destinatario debe ser un email válido")
    @Size(max = 150, message = "El destinatario no puede exceder 150 caracteres")
    private String destinatario;

    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 200, message = "El asunto no puede exceder 200 caracteres")
    private String asunto;

    @NotBlank(message = "El cuerpo es obligatorio")
    @Size(max = 1000, message = "El cuerpo no puede exceder 1000 caracteres")
    private String cuerpo;
}
