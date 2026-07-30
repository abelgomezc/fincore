package com.fincore.reportingservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tipo", nullable = false, length = 100)
    private String tipo;

    @Column(name = "parametros")
    private String parametros;

    @Column(name = "ruta_archivo")
    private String rutaArchivo;

    @Column(name = "generado_en", nullable = false, updatable = false)
    private LocalDateTime generadoEn = LocalDateTime.now();

    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;
}
