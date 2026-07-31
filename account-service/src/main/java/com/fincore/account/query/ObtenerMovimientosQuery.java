package com.fincore.account.query;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Query para obtener movimientos de una cuenta.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObtenerMovimientosQuery {

    @NotNull(message = "El ID de la cuenta es obligatorio")
    private Long idCuenta;

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    private int page = 0;
    private int size = 50;
}
