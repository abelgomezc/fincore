package com.fincore.loan.dto.request;

import com.fincore.loan.enums.TipoPrestamo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Request para crear una solicitud de préstamo.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public record CrearSolicitudPrestamoRequest(
        @NotNull Long idCliente,
        @NotNull TipoPrestamo tipoPrestamo,
        @NotNull @DecimalMin(value = "100.0") BigDecimal montoSolicitado,
        @NotNull Integer plazoMeses,
        BigDecimal tasaInteresAnual,
        String creadoPor
) {}
