package com.fincore.transfer.dto.request;

import com.fincore.transfer.enums.PasoSaga;
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
 * DTO para solicitud de transferencia.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearTransferenciaRequest {

    @NotNull(message = "El ID de cuenta origen es obligatorio")
    private Long idCuentaOrigen;

    @NotBlank(message = "El número de cuenta origen es obligatorio")
    @Size(max = 20)
    private String numeroCuentaOrigen;

    @NotNull(message = "El ID de cuenta destino es obligatorio")
    private Long idCuentaDestino;

    @NotBlank(message = "El número de cuenta destino es obligatorio")
    @Size(max = 20)
    private String numeroCuentaDestino;

    @NotBlank(message = "El nombre del beneficiario es obligatorio")
    @Size(max = 255)
    private String nombreBeneficiario;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @Size(max = 3)
    private String moneda = "USD";

    @DecimalMin(value = "0.0", message = "La comisión no puede ser negativa")
    private BigDecimal comision = BigDecimal.ZERO;

    private String concepto;

    private String ipOrigen;
    private String dispositivo;
    private String traceId;
}
