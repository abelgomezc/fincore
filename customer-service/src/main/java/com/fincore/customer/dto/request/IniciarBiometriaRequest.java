package com.fincore.customer.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request para iniciar sesión biométrica.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public record IniciarBiometriaRequest(
        @NotNull Long idCliente,
        @NotNull @Size(max = 50) String tipoBiometria,
        String proveedor,
        String idSesionProveedor
) {}
