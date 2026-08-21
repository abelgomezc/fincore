package com.fincore.loan.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * Request para evaluar riesgo de una solicitud.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public record EvaluarRiesgoRequest(
        @NotNull Long idSolicitud,
        Integer scoreBuro,
        Integer scoreInterno,
        String detalle
) {}
