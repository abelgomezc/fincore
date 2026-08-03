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
 * Entidad ListaNegra — entradas de lista negra para fraude.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "lista_negra")
@Getter
@Setter
@NoArgsConstructor
public class ListaNegra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo", length = 20, nullable = false)
    private String tipo;

    @Column(name = "valor", length = 255, nullable = false)
    private String valor;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "es_activo")
    private Boolean esActivo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
