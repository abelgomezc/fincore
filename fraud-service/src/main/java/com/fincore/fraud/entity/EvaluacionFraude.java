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
 * Entidad EvaluacionFraude — registro de cada evaluación de fraude.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "evaluaciones_fraude")
@Getter
@Setter
@NoArgsConstructor
public class EvaluacionFraude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_transferencia", nullable = false)
    private Long idTransferencia;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "score_total")
    private Integer scoreTotal;

    @Column(name = "decision", length = 20)
    private String decision;

    @Column(name = "reglas_activadas", columnDefinition = "jsonb")
    private String reglasActivadas;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "dispositivo", length = 255)
    private String dispositivo;

    @Column(name = "tiempo_evaluacion_ms")
    private Integer tiempoEvaluacionMs;

    @Column(name = "revisado_por", length = 100)
    private String revisadoPor;

    @Column(name = "fecha_revision")
    private LocalDateTime fechaRevision;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}
