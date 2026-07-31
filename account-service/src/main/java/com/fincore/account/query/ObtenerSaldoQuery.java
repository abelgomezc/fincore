package com.fincore.account.query;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query para obtener el saldo de una cuenta.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObtenerSaldoQuery {

    @NotNull(message = "El ID de la cuenta es obligatorio")
    private Long idCuenta;

    private String numeroCuenta;

    private Long idCliente;
}
