package com.fincore.document.dto.request;

import com.fincore.document.enums.TipoDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request para crear una plantilla de documento.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public record CrearPlantillaRequest(
        @NotNull TipoDocumento tipoDocumento,
        @NotBlank String nombre,
        String descripcion,
        @NotBlank String contenidoHtml
) {}
