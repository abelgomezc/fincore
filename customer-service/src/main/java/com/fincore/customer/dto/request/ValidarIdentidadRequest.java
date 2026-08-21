package com.fincore.customer.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request para iniciar validación de identidad.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public record ValidarIdentidadRequest(
        @NotNull Long idCliente,
        @NotNull @Size(max = 50) String tipoValidacion,
        String proveedor,
        String detalle
) {}
