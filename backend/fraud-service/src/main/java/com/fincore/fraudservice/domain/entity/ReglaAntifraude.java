package com.fincore.fraudservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reglas_antifraude")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ReglaAntifraude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_regla", nullable = false, updatable = false)
    private Long id;

    @Column(name = "nombre", length = 100, nullable = false, unique = true)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "tipo_regla", length = 50, nullable = false)
    private String tipoRegla;

    @Column(name = "parametros", length = 1000)
    private String parametros;

    @Column(name = "umbral", precision = 18, scale = 2)
    private BigDecimal umbral;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "prioridad", nullable = false)
    private Integer prioridad;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
