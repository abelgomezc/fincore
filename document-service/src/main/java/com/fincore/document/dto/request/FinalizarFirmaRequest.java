package com.fincore.document.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * Request para finalizar una firma electrónica.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public record FinalizarFirmaRequest(
        @NotNull Long idFirma,
        @NotNull String estado,
        String motivoRechazo,
        String ipFirmante,
        String huellaDigital,
        String certificadoSerial
) {}
