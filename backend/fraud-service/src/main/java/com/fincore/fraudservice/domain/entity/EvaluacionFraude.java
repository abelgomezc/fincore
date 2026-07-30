package com.fincore.fraudservice.domain.entity;

import com.fincore.fraudservice.domain.enums.DecisionFraude;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluaciones_fraude")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class EvaluacionFraude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion", nullable = false, updatable = false)
    private Long id;

    @Column(name = "id_transaccion", nullable = false, length = 50, unique = true)
    private String idTransaccion;

    @Column(name = "id_cuenta_origen", length = 50)
    private String idCuentaOrigen;

    @Column(name = "id_cuenta_destino", length = 50)
    private String idCuentaDestino;

    @Column(name = "monto", precision = 18, scale = 2)
    private BigDecimal monto;

    @Column(name = "moneda", length = 3)
    private String moneda;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", length = 20, nullable = false)
    private DecisionFraude decision;

    @Column(name = "puntuacion_riesgo", precision = 5, scale = 2)
    private BigDecimal puntuacionRiesgo;

    @Column(name = "motivo", length = 500)
    private String motivo;

    @CreationTimestamp
    @Column(name = "fecha_evaluacion", nullable = false, updatable = false)
    private LocalDateTime fechaEvaluacion;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
