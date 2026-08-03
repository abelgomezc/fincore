package com.fincore.batch.entity;

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
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad de conciliación batch.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "conciliaciones")
@Getter
@Setter
@NoArgsConstructor
public class Conciliacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_procesamiento")
    private LocalDate fechaProcesamiento;

    @Column(name = "total_transferencias")
    private Integer totalTransferencias;

    @Column(name = "total_debitos")
    private BigDecimal totalDebitos;

    @Column(name = "total_creditos")
    private BigDecimal totalCreditos;

    @Column(name = "diferencias", columnDefinition = "jsonb")
    private String diferencias;

    @Column(name = "estado", length = 20)
    private String estado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}