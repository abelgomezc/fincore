package com.fincore.accountservice.domain.dto;

import com.fincore.accountservice.domain.enums.EstadoCuenta;
import com.fincore.accountservice.domain.enums.Moneda;
import com.fincore.accountservice.domain.enums.TipoCuenta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaRequest {

    @NotBlank(message = "El numero de cuenta es obligatorio")
    @Size(max = 20, message = "El numero de cuenta no debe superar 20 caracteres")
    private String numeroCuenta;

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private TipoCuenta tipoCuenta;

    @NotNull(message = "La moneda es obligatoria")
    private Moneda moneda;

    @NotNull(message = "El saldo contable es obligatorio")
    private BigDecimal saldoContable;

    @NotNull(message = "El saldo disponible es obligatorio")
    private BigDecimal saldoDisponible;

    @NotNull(message = "El saldo retenido es obligatorio")
    private BigDecimal saldoRetenido;

    @NotNull(message = "El saldo proyectado es obligatorio")
    private BigDecimal saldoProyectado;

    @NotNull(message = "El estado es obligatorio")
    private EstadoCuenta estado;
}