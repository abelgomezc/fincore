package com.fincore.auditservice.domain.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroAuditoriaRequest {
    private String entidadTipo;
    private Long entidadId;
    private String accion;
    private String estadoAnterior;
    private String estadoNuevo;
    private String usuarioTipo;
    private Long usuarioId;
    private String ipAddress;
    private String traceId;
}
