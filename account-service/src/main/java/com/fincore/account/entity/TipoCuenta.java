package com.fincore.account.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidad TipoCuenta — catálogo de tipos de cuenta bancaria.
 *
 * Define las características de cada tipo: tasa de interés,
 * saldo mínimo, límites de transacción y si permite sobregiro.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "tipos_cuenta")
@Getter
@Setter
@NoArgsConstructor
public class TipoCuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true, nullable = false, length = 10)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tasa_interes_anual", precision = 8, scale = 4)
    private BigDecimal tasaInteresAnual = BigDecimal.ZERO;

    @Column(name = "saldo_minimo", precision = 18, scale = 2)
    private BigDecimal saldoMinimo = BigDecimal.ZERO;

    @Column(name = "limite_transaccion_diario", precision = 18, scale = 2)
    private BigDecimal limiteTransaccionDiario;

    @Column(name = "limite_monto_por_transaccion", precision = 18, scale = 2)
    private BigDecimal limiteMontoPorTransaccion;

    @Column(name = "permite_sobregiro", nullable = false)
    private Boolean permiteSobregiro = false;

    @Column(name = "fecha_creacion", nullable = false)
    private java.time.LocalDateTime fechaCreacion = java.time.LocalDateTime.now();
}
