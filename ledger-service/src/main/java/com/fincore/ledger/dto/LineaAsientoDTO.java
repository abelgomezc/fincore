package com.fincore.ledger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para una línea de asiento contable.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineaAsientoDTO {

    private String codigoCuenta;
    private Long idCuentaBancaria;
    private String tipoMovimiento; // DEBITO, CREDITO
    private BigDecimal monto;
    private String descripcion;
}
