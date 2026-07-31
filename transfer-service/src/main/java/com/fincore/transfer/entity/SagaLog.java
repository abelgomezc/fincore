package com.fincore.transfer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad SagaLog — log de cada paso de la saga ejecutado.
 *
 * Registra el orden, estado de ejecución, detalles y tiempos
 * de cada paso de la saga para auditoría.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "saga_log")
@Getter
@Setter
@NoArgsConstructor
public class SagaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_transferencia", nullable = false)
    private Long idTransferencia;

    @Column(name = "paso_saga", nullable = false, length = 50)
    private String pasoSaga;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    @Column(name = "estado_ejecucion", nullable = false, length = 20)
    private String estadoEjecucion;

    @Column(name = "detalle", columnDefinition = "TEXT")
    private String detalle;

    @Column(name = "error_detalle", columnDefinition = "TEXT")
    private String errorDetalle;

    @Column(name = "tiempo_ejecucion_ms")
    private Integer tiempoEjecucionMs;

    @Column(name = "fecha_ejecucion", nullable = false)
    private LocalDateTime fechaEjecucion = LocalDateTime.now();
}
