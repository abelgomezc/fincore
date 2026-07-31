package com.fincore.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de respuesta con el saldo de una cuenta.
 *
 * Incluye los 4 tipos de saldo: contable, disponible, retenido, proyectado.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaldoResponse {

    private Long idCuenta;
    private String numeroCuenta;
    private BigDecimal saldoContable;
    private BigDecimal saldoDisponible;
    private BigDecimal saldoRetenido;
    private BigDecimal saldoProyectado;
    private String moneda;
    private String estado;
    private String fechaActualizacion;
}
