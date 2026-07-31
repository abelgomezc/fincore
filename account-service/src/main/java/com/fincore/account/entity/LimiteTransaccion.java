package com.fincore.account.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad LimiteTransaccion — límites de transacción por cuenta.
 *
 * Controla los montos máximos que un cliente puede transferir
 * diariamente, por transacción y mensualmente.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "limites_transaccion")
@Getter
@Setter
@NoArgsConstructor
public class LimiteTransaccion extends com.fincore.account.config.BaseEntity {

    @Column(name = "id_cuenta", nullable = false, unique = true)
    private Long idCuenta;

    @Column(name = "monto_maximo_diario", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoMaximoDiario = new BigDecimal("5000.00");

    @Column(name = "monto_maximo_por_transaccion", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoMaximoPorTransaccion = new BigDecimal("2000.00");

    @Column(name = "monto_maximo_mensual", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoMaximoMensual = new BigDecimal("20000.00");

    @Column(name = "contador_diario", nullable = false, precision = 18, scale = 2)
    private BigDecimal contadorDiario = BigDecimal.ZERO;

    @Column(name = "contador_mensual", nullable = false, precision = 18, scale = 2)
    private BigDecimal contadorMensual = BigDecimal.ZERO;

    @Column(name = "fecha_ultima_transaccion", nullable = false)
    private java.time.LocalDateTime fechaUltimaTransaccion = java.time.LocalDateTime.now();

    public boolean validarLimiteTransaccion(BigDecimal monto) {
        if (monto.compareTo(montoMaximoPorTransaccion) > 0) {
            return false;
        }
        if (contadorDiario.add(monto).compareTo(montoMaximoDiario) > 0) {
            return false;
        }
        if (contadorMensual.add(monto).compareTo(montoMaximoMensual) > 0) {
            return false;
        }
        return true;
    }
}
