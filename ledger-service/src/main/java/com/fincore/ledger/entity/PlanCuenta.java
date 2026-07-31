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

import java.time.LocalDateTime;

/**
 * Entidad PlanCuenta — catálogo de cuentas contables.
 *
 * Define la estructura del plan de cuentas con códigos que van
 * desde 1000 (activo) hasta 5010 (gastos operativos).
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "plan_cuentas")
@Getter
@Setter
@NoArgsConstructor
public class PlanCuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true, nullable = false, length = 10)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;

    @Column(name = "naturaleza", nullable = false, length = 10)
    private String naturaleza;

    @Column(name = "nivel", nullable = false)
    private Integer nivel;

    @Column(name = "codigo_padre", length = 10)
    private String codigoPadre;

    @Column(name = "es_hoja", nullable = false)
    private Boolean esHoja = true;

    @Column(name = "es_activa", nullable = false)
    private Boolean esActiva = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
