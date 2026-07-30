package com.fincore.auditservice.domain.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroAuditoriaResponse {
    private Long id;
    private String entidadTipo;
    private Long entidadId;
    private String accion;
    private String estadoAnterior;
    private String estadoNuevo;
    private String usuarioTipo;
    private Long usuarioId;
    private String ipAddress;
    private String traceId;
    private LocalDateTime creadoEn;
}
