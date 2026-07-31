package com.fincore.transfer.dto.response;

import com.fincore.transfer.enums.EstadoTransferencia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta con información de transferencia.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaResponse {

    private Long id;
    private String numeroTransferencia;
    private Long idCuentaOrigen;
    private String numeroCuentaOrigen;
    private Long idCuentaDestino;
    private String numeroCuentaDestino;
    private String nombreBeneficiario;
    private BigDecimal monto;
    private String moneda;
    private BigDecimal comision;
    private String concepto;
    private EstadoTransferencia estado;
    private String pasoSagaActual;
    private Integer intentosSaga;
    private Integer scoreFraude;
    private String decisionFraude;
    private String idUsuario;
    private String ipOrigen;
    private String dispositivo;
    private String traceId;
    private LocalDateTime fechaIniciada;
    private LocalDateTime fechaCompletada;
    private LocalDateTime fechaRevertida;
    private String motivoRechazo;
    private List<EstadoTransferenciaDto> historialEstados;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstadoTransferenciaDto {
        private String estadoAnterior;
        private String estadoNuevo;
        private String pasoSaga;
        private String descripcion;
        private String errorDetalle;
        private LocalDateTime fechaCambio;
    }
}
