package com.fincore.ledger.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad LineaAsiento — línea individual de un asiento contable.
 *
 * INMUTABLE: nunca se actualiza ni elimina.
 * Cada línea es un débito o crédito a una cuenta del plan contable.
 * La suma de débitos debe ser igual a la suma de créditos.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "lineas_asiento")
@Getter
@Setter
@NoArgsConstructor
public class LineaAsiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_asiento", nullable = false)
    private Long idAsiento;

    @Column(name = "codigo_cuenta", nullable = false, length = 10)
    private String codigoCuenta;

    @Column(name = "id_cuenta_bancaria")
    private Long idCuentaBancaria;

    @Column(name = "tipo_movimiento", nullable = false, length = 10)
    private String tipoMovimiento; // DEBITO, CREDITO

    @Column(name = "monto", nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // NO fecha_actualizacion — entidad inmutable
}
