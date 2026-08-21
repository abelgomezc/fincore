package com.fincore.loan.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fincore.loan.enums.EstadoEvaluacionRiesgo;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response de evaluación de riesgo.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Getter
@Setter
@NoArgsConstructor
public class EvaluacionRiesgoResponse {
    private Long id;
    private Long idSolicitud;
    private String estado;
    private Integer scoreBuro;
    private Integer scoreInterno;
    private Integer scoreFinal;
    private BigDecimal montoAprobado;
    private BigDecimal tasaInteresAprobada;
    private Integer plazoAprobadoMeses;
    private String detalle;
    private String reglasAplicadas;
    private LocalDateTime fechaEvaluacion;
    private String evaluadoPor;
}
