package com.fincore.transfer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad TransferenciaEstado — historial inmutable de cambios de estado.
 *
 * Registra cada transición de estado de la transferencia durante la saga.
 * INMUTABLE: no tiene fecha_actualizacion.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "transferencia_estados")
@Getter
@Setter
@NoArgsConstructor
public class TransferenciaEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_transferencia", nullable = false)
    private Long idTransferencia;

    @Column(name = "estado_anterior", length = 20)
    private String estadoAnterior;

    @Column(name = "estado_nuevo", nullable = false, length = 20)
    private String estadoNuevo;

    @Column(name = "paso_saga", length = 50)
    private String pasoSaga;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "error_detalle", columnDefinition = "TEXT")
    private String errorDetalle;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio = LocalDateTime.now();
}
