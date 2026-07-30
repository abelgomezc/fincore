package com.fincore.batchservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ejecucion_batch", schema = "batch")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EjecucionBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre_job", nullable = false, length = 255)
    private String nombreJob;

    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "registros_procesados")
    @Builder.Default
    private Long registrosProcesados = 0L;

    @Column(name = "registros_fallidos")
    @Builder.Default
    private Long registrosFallidos = 0L;

    @Column(name = "mensaje_error")
    private String mensajeError;

    @Version
    @Column(name = "version")
    private Long version;
}
