package com.fincore.account.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Comando para actualizar saldo de una cuenta.
 *
 * Aplica un débito o crédito al saldo contable, disponible y proyectado.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarSaldoCommand {

    @NotNull(message = "El ID de la cuenta es obligatorio")
    private Long idCuenta;

    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    private String tipoMovimiento; // DEBITO, CREDITO, RETENCION, LIBERACION, COMISION

    private String emailUsuario;
    private String ipOrigen;
    private String traceId;
}
