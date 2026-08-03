package com.fincore.audit.entity;

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
 * Entidad EventoSaga — auditoría de cada paso de la saga.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "eventos_saga")
@Getter
@Setter
@NoArgsConstructor
public class EventoSaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_transferencia", nullable = false)
    private Long idTransferencia;

    @Column(name = "numero_transferencia", length = 50)
    private String numeroTransferencia;

    @Column(name = "paso_saga", length = 50)
    private String pasoSaga;

    @Column(name = "orden")
    private Integer orden;

    @Column(name = "estado_ejecucion", length = 20)
    private String estadoEjecucion;

    @Column(name = "detalle", columnDefinition = "TEXT")
    private String detalle;

    @Column(name = "error_detalle", columnDefinition = "TEXT")
    private String errorDetalle;

    @Column(name = "duracion_ms")
    private Integer duracionMs;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}
