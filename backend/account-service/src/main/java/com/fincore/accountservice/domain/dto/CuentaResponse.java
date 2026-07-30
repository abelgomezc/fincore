package com.fincore.accountservice.domain.dto;

import com.fincore.accountservice.domain.enums.EstadoCuenta;
import com.fincore.accountservice.domain.enums.Moneda;
import com.fincore.accountservice.domain.enums.TipoCuenta;
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
public class CuentaResponse {

    private Long id;
    private String numeroCuenta;
    private TipoCuenta tipoCuenta;
    private Moneda moneda;
    private BigDecimal saldoContable;
    private BigDecimal saldoDisponible;
    private BigDecimal saldoRetenido;
    private BigDecimal saldoProyectado;
    private EstadoCuenta estado;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}