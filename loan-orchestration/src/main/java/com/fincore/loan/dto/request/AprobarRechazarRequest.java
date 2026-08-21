package com.fincore.loan.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * Request para aprobar/rechazar una solicitud.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public record AprobarRechazarRequest(
        @NotNull Long idSolicitud,
        @NotNull String accion,
        String motivo
) {}
