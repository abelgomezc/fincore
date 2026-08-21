package com.fincore.loan.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fincore.loan.enums.EstadoEvaluacionRiesgo;
import com.fincore.loan.enums.EstadoSolicitudPrestamo;
import com.fincore.loan.enums.TipoPrestamo;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response de solicitud de préstamo.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Getter
@Setter
@NoArgsConstructor
public class SolicitudPrestamoResponse {
    private Long id;
    private Long idCliente;
    private String numeroSolicitud;
    private String tipoPrestamo;
    private String estado;
    private BigDecimal montoSolicitado;
    private Integer plazoMeses;
    private BigDecimal tasaInteresAnual;
    private BigDecimal montoAprobado;
    private Integer scoreBuro;
    private Integer scoreRiesgo;
    private String evaluacionRiesgo;
    private String motivoRechazo;
    private Long idDocumentoContrato;
    private Long idDocumentoPagare;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaAprobacion;
    private LocalDateTime fechaRechazo;
    private LocalDateTime fechaDesembolso;
}
