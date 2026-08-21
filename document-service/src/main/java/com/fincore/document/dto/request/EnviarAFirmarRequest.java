package com.fincore.document.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request para enviar a firmar un documento.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public record EnviarAFirmarRequest(
        @NotNull Long idDocumento,
        @NotBlank String idFirmante,
        @NotBlank String nombreFirmante,
        String emailFirmante,
        String proveedor,
        String ipFirmante
) {}
