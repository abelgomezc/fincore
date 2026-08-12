package com.fincore.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de auditoría.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaResponse {

    private Long id;
    private String entidad;
    private String idEntidad;
    private String accion;
    private String estadoAnterior;
    private String estadoNuevo;
    private String motivo;
    private String ipOrigen;
    private String userAgent;
    private String dispositivo;
    private LocalDateTime fechaCambio;
    private String creadoPor;
}
