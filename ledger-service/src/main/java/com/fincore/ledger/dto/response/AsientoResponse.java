package com.fincore.ledger.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para la creación de asientos.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsientoResponse {

    private boolean exito;
    private String numeroAsiento;
    private Long idAsiento;
    private String mensajeError;
}
