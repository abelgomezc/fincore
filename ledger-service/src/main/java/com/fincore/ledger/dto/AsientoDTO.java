package com.fincore.ledger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para un asiento contable completo.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsientoDTO {

    private String descripcion;
    private Long idReferencia;
    private String tipoReferencia;
    private String idUsuario;
    private String ipOrigen;
    private String traceId;
    private List<LineaAsientoDTO> lineas;
    private LocalDateTime fechaAsiento;
    private String numeroAsiento;
    private String estado;
}
