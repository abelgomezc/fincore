package com.fincore.loan.entity;

import com.fincore.loan.enums.EstadoEvaluacionRiesgo;
import com.fincore.loan.enums.EstadoSolicitudPrestamo;
import com.fincore.loan.enums.TipoPrestamo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad SolicitudPrestamo — solicitud principal de préstamo.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "solicitudes_prestamo", indexes = {
        @Index(name = "idx_solicitudes_prestamo_cliente", columnList = "id_cliente"),
        @Index(name = "idx_solicitudes_prestamo_estado", columnList = "estado"),
        @Index(name = "idx_solicitudes_prestamo_fecha", columnList = "fecha_solicitud")
})
@Getter
@Setter
@NoArgsConstructor
public class SolicitudPrestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "numero_solicitud", length = 50, unique = true, nullable = false)
    private String numeroSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_prestamo", nullable = false, length = 50)
    private TipoPrestamo tipoPrestamo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 50)
    private EstadoSolicitudPrestamo estado = EstadoSolicitudPrestamo.BORRADOR;

    @Column(name = "monto_solicitado", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoSolicitado;

    @Column(name = "plazo_meses", nullable = false)
    private Integer plazoMeses;

    @Column(name = "tasa_interes_anual", precision = 5, scale = 2)
    private BigDecimal tasaInteresAnual;

    @Column(name = "monto_aprobado", precision = 15, scale = 2)
    private BigDecimal montoAprobado;

    @Column(name = "score_buro")
    private Integer scoreBuro;

    @Column(name = "score_riesgo")
    private Integer scoreRiesgo;

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluacion_riesgo", length = 30)
    private EstadoEvaluacionRiesgo evaluacionRiesgo = EstadoEvaluacionRiesgo.PENDIENTE;

    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    @Column(name = "id_documento_contrato")
    private Long idDocumentoContrato;

    @Column(name = "id_documento_pagare")
    private Long idDocumentoPagare;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Column(name = "fecha_rechazo")
    private LocalDateTime fechaRechazo;

    @Column(name = "fecha_desembolso")
    private LocalDateTime fechaDesembolso;

    @Column(name = "creado_por", length = 100)
    private String creadoPor;

    @Column(name = "actualizado_por", length = 100)
    private String actualizadoPor;

    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        fechaSolicitud = LocalDateTime.now();
        creadoPor = "system";
        actualizadoPor = "system";
        version = 0L;
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoPor = "system";
    }
}
