package com.fincore.account.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Comando para liberar una retención de fondos.
 *
 * Usado como compensating transaction en la saga.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiberarReservaCommand {

    @NotNull(message = "El ID de la cuenta es obligatorio")
    private Long idCuenta;

    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    private String emailUsuario;
    private String ipOrigen;
    private String traceId;
}
