package com.fincore.document.dto.request;

import com.fincore.document.enums.TipoDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Request para generar un documento desde plantilla.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public record GenerarDocumentoRequest(
        @NotNull Long idPlantilla,
        @NotNull TipoDocumento tipoDocumento,
        @NotBlank String entidad,
        @NotBlank String idEntidad,
        @NotBlank String nombreArchivo,
        Map<String, Object> variables,
        String creadoPor
) {}
