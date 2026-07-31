package com.fincore.account.command;

import com.fincore.account.enums.EstadoCuenta;
import com.fincore.account.enums.TipoCuentaEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Comando para aperturar una nueva cuenta.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AbrirCuentaCommand {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long idCliente;

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private TipoCuentaEnum tipoCuenta;

    @NotBlank(message = "La moneda es obligatoria")
    @Size(min = 3, max = 3)
    private String moneda;

    @DecimalMin(value = "0.0", inclusive = false, message = "El saldo inicial debe ser mayor a 0")
    private BigDecimal saldoInicial;

    private String emailUsuario;
    private String ipOrigen;
    private String deviceId;
    private String traceId;
}
