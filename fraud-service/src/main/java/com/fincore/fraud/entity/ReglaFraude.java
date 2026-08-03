package com.fincore.fraud.entity;

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
 * Entidad ReglaFraude — regla de scoring configurable en BD.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "reglas_fraude")
@Getter
@Setter
@NoArgsConstructor
public class ReglaFraude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", length = 50, unique = true, nullable = false)
    private String codigo;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "puntos")
    private Integer puntos;

    @Column(name = "parametros", columnDefinition = "jsonb")
    private String parametros;

    @Column(name = "es_activo")
    private Boolean esActivo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
