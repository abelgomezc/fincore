package com.fincore.ledger.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de respuesta para extracto contable.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractoResponse {

    private List<MovimientoContableDTO> movimientos;
    private BigDecimal totalDebitos;
    private BigDecimal totalCreditos;
    private BigDecimal saldoNeto;
    private int totalRegistros;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovimientoContableDTO {
        private String numeroAsiento;
        private String fechaAsiento;
        private String codigoCuenta;
        private String nombreCuenta;
        private String tipoMovimiento;
        private BigDecimal monto;
        private String descripcion;
        private String traceId;
    }
}
