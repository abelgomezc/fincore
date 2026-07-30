package com.fincore.fraudservice.domain.dto;

import com.fincore.fraudservice.domain.enums.DecisionFraude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionFraudeResponse {
    private Long idEvaluacion;
    private String idTransaccion;
    private DecisionFraude decision;
    private BigDecimal puntuacionRiesgo;
    private String motivo;
    private LocalDateTime fechaEvaluacion;
}
