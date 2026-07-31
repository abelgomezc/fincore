package com.fincore.account.entity;

import com.fincore.account.config.BaseEntity;
import com.fincore.account.enums.EstadoCuenta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad Cuenta — representa una cuenta bancaria.
 *
 * Mantiene los 4 tipos de saldo sincronizados:
 * - saldoContable: lo que realmente tiene en la cuenta (contable)
 * - saldoDisponible: lo que puede usar ahora (contable - retenido)
 * - saldoRetenido: lo que está bloqueado temporalmente
 * - saldoProyectado: proyección incluyendo transacciones pendientes
 *
 * @Version para optimistic locking — crítico en concurrencia bancaria
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "cuentas")
@Getter
@Setter
@NoArgsConstructor
public class Cuenta extends BaseEntity {

    @Column(name = "numero_cuenta", unique = true, nullable = false, length = 20)
    private String numeroCuenta;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_cuenta", nullable = false)
    private TipoCuenta tipoCuenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCuenta estado = EstadoCuenta.ACTIVA;

    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda = "USD";

    @Column(name = "saldo_contable", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoContable = BigDecimal.ZERO;

    @Column(name = "saldo_disponible", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoDisponible = BigDecimal.ZERO;

    @Column(name = "saldo_retenido", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoRetenido = BigDecimal.ZERO;

    @Column(name = "saldo_proyectado", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoProyectado = BigDecimal.ZERO;

    @Column(name = "motivo_bloqueo", columnDefinition = "TEXT")
    private String motivoBloqueo;

    @Column(name = "fecha_apertura")
    private LocalDate fechaApertura;

    @Column(name = "fecha_ultimo_movimiento")
    private LocalDateTime fechaUltimoMovimiento;

    @Column(name = "fecha_cierre")
    private LocalDate fechaCierre;

    /**
     * Reservar fondos: decrementa saldoDisponible e incrementa saldoRetenido.
     * No modifica saldoContable (la cuenta sigue teniendo el dinero).
     */
    public void reservarFondos(BigDecimal monto) {
        validarSaldoSuficiente(monto);
        this.saldoDisponible = this.saldoDisponible.subtract(monto);
        this.saldoRetenido = this.saldoRetenido.add(monto);
        this.saldoProyectado = this.saldoContable.subtract(monto);
        this.fechaUltimoMovimiento = LocalDateTime.now();
    }

    /**
     * Liberar retención: incrementa saldoDisponible y decrementa saldoRetenido.
     */
    public void liberarRetencion(BigDecimal monto) {
        this.saldoDisponible = this.saldoDisponible.add(monto);
        this.saldoRetenido = this.saldoRetenido.subtract(monto);
        this.saldoProyectado = this.saldoContable;
        this.fechaUltimoMovimiento = LocalDateTime.now();
    }

    /**
     * Aplicar débito: decrementa saldoContable, saldoDisponible y saldoProyectado.
     */
    public void aplicarDebito(BigDecimal monto) {
        this.saldoContable = this.saldoContable.subtract(monto);
        this.saldoDisponible = this.saldoDisponible.subtract(monto);
        this.saldoProyectado = this.saldoProyectado.subtract(monto);
        this.fechaUltimoMovimiento = LocalDateTime.now();
    }

    /**
     * Aplicar crédito: incrementa saldoContable, saldoDisponible y saldoProyectado.
     */
    public void aplicarCredito(BigDecimal monto) {
        this.saldoContable = this.saldoContable.add(monto);
        this.saldoDisponible = this.saldoDisponible.add(monto);
        this.saldoProyectado = this.saldoProyectado.add(monto);
        this.fechaUltimoMovimiento = LocalDateTime.now();
    }

    /**
     * Revertir débito: incrementa saldoContable, saldoDisponible y saldoProyectado.
     */
    public void revertirDebito(BigDecimal monto) {
        this.saldoContable = this.saldoContable.add(monto);
        this.saldoDisponible = this.saldoDisponible.add(monto);
        this.saldoProyectado = this.saldoProyectado.add(monto);
        this.fechaUltimoMovimiento = LocalDateTime.now();
    }

    /**
     * Revertir crédito: decrementa saldoContable, saldoDisponible y saldoProyectado.
     */
    public void revertirCredito(BigDecimal monto) {
        this.saldoContable = this.saldoContable.subtract(monto);
        this.saldoDisponible = this.saldoDisponible.subtract(monto);
        this.saldoProyectado = this.saldoProyectado.subtract(monto);
        this.fechaUltimoMovimiento = LocalDateTime.now();
    }

    private void validarSaldoSuficiente(BigDecimal monto) {
        if (saldoDisponible.compareTo(monto) < 0) {
            if (tipoCuenta.getPermiteSobregiro() == null || !tipoCuenta.getPermiteSobregiro()) {
                throw new IllegalStateException("Saldo insuficiente. Disponible: " + saldoDisponible);
            }
        }
    }

    public boolean esTransferible() {
        return estado == EstadoCuenta.ACTIVA;
    }
}
