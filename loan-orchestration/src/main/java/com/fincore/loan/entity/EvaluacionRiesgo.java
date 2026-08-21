package com.fincore.loan.entity;

import com.fincore.loan.enums.EstadoEvaluacionRiesgo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad EvaluacionRiesgo — evaluación de riesgo crediticio de una solicitud.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "evaluaciones_riesgo", indexes = {
        @Index(name = "idx_evaluaciones_riesgo_solicitud", columnList = "id_solicitud"),
        @Index(name = "idx_evaluaciones_riesgo_fecha", columnList = "fecha_evaluacion")
})
@Getter
@Setter
@NoArgsConstructor
public class EvaluacionRiesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_solicitud", nullable = false)
    private Long idSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoEvaluacionRiesgo estado = EstadoEvaluacionRiesgo.PENDIENTE;

    @Column(name = "score_buro")
    private Integer scoreBuro;

    @Column(name = "score_interno")
    private Integer scoreInterno;

    @Column(name = "score_final")
    private Integer scoreFinal;

    @Column(name = "monto_aprobado", precision = 15, scale = 2)
    private java.math.BigDecimal montoAprobado;

    @Column(name = "tasa_interes_aprobada", precision = 5, scale = 2)
    private java.math.BigDecimal tasaInteresAprobada;

    @Column(name = "plazo_aprobado_meses")
    private Integer plazoAprobadoMeses;

    @Column(name = "detalle", columnDefinition = "TEXT")
    private String detalle;

    @Column(name = "reglas_aplicadas", columnDefinition = "TEXT")
    private String reglasAplicadas;

    @Column(name = "fecha_evaluacion")
    private LocalDateTime fechaEvaluacion;

    @Column(name = "evaluado_por", length = 100)
    private String evaluadoPor;

    @Column(name = "creado_por", length = 100)
    private String creadoPor;

    @Column(name = "actualizado_por", length = 100)
    private String actualizadoPor;

    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        fechaEvaluacion = LocalDateTime.now();
        creadoPor = "system";
        actualizadoPor = "system";
        version = 0L;
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoPor = "system";
    }
}
