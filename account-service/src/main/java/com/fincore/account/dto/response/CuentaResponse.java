package com.fincore.account.dto.response;

import com.fincore.account.enums.EstadoCuenta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de respuesta con información de cuenta.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaResponse {

    private Long id;
    private String numeroCuenta;
    private Long idCliente;
    private String tipoCuenta;
    private String codigoMoneda;
    private EstadoCuenta estado;
    private BigDecimal saldoContable;
    private BigDecimal saldoDisponible;
    private BigDecimal saldoRetenido;
    private BigDecimal saldoProyectado;
    private String moneda;
    private String fechaApertura;
    private String fechaUltimoMovimiento;
    private String motivoBloqueo;
    private String nombrePropietario;
    private String identificacionPropietario;
}
