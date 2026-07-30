package com.fincore.batchservice.dto;

import com.fincore.batchservice.enums.EstadoEjecucion;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EjecucionBatchDTO {

    private Long id;
    private String nombreJob;
    private EstadoEjecucion estado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long registrosProcesados;
    private Long registrosFallidos;
    private String mensajeError;
}
