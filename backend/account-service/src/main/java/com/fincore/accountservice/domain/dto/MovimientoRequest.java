package com.fincore.accountservice.domain.dto;

import com.fincore.accountservice.domain.enums.EstadoCuenta;
import com.fincore.accountservice.domain.enums.Moneda;
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
public class MovimientoRequest {

    @NotNull(message = "El id de la cuenta es obligatorio")
    private Long cuentaId;

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Size(max = 20, message = "El tipo de movimiento no debe superar 20 caracteres")
    private String tipoMovimiento;

    @NotNull(message = "El monto es obligatorio")
    private BigDecimal monto;

    @NotNull(message = "La moneda es obligatoria")
    private Moneda moneda;

    @Size(max = 255, message = "La descripcion no debe superar 255 caracteres")
    private String descripcion;

    @NotNull(message = "El saldo anterior es obligatorio")
    private BigDecimal saldoAnterior;

    @NotNull(message = "El saldo posterior es obligatorio")
    private BigDecimal saldoPosterior;

    @Size(max = 50, message = "La referencia no debe superar 50 caracteres")
    private String referencia;

    @NotNull(message = "El estado es obligatorio")
    private EstadoCuenta estado;

    private String createdBy;
}