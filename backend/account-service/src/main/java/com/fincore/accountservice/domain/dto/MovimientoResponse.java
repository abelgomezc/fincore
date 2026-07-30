package com.fincore.accountservice.domain.dto;

import com.fincore.accountservice.domain.enums.EstadoCuenta;
import com.fincore.accountservice.domain.enums.Moneda;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoResponse {

    private Long id;
    private Long cuentaId;
    private String tipoMovimiento;
    private BigDecimal monto;
    private Moneda moneda;
    private String descripcion;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoPosterior;
    private String referencia;
    private EstadoCuenta estado;
    private Integer version;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
}