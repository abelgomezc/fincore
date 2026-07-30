package com.fincore.fraudservice.domain.dto;

import com.fincore.fraudservice.domain.enums.DecisionFraude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluarFraudeRequest {
    private String idTransaccion;
    private String idCuentaOrigen;
    private String idCuentaDestino;
    private BigDecimal monto;
    private String moneda;
}
