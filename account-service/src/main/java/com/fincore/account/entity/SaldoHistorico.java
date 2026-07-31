package com.fincore.account.entity;

import com.fincore.account.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad SaldoHistorico — snapshot diario de saldos.
 *
 * Se crea un snapshot al final de cada día para auditoría y reporting.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "saldos_historicos")
@Getter
@Setter
@NoArgsConstructor
public class SaldoHistorico extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuenta", nullable = false)
    private Cuenta cuenta;

    @Column(name = "fecha_snapshot", nullable = false)
    private LocalDate fechaSnapshot;

    @Column(name = "saldo_contable", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoContable = BigDecimal.ZERO;

    @Column(name = "saldo_disponible", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoDisponible = BigDecimal.ZERO;

    @Column(name = "saldo_retenido", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoRetenido = BigDecimal.ZERO;

    @Column(name = "saldo_proyectado", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoProyectado = BigDecimal.ZERO;

    @Column(name = "fecha_creacion", nullable = false)
    private java.time.LocalDateTime fechaCreacion = java.time.LocalDateTime.now();
}
