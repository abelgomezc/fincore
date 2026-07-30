package com.fincore.transferservice.domain.dto;

import com.fincore.transferservice.domain.enums.EstadoTransferencia;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaResponse {

    private Long id;
    private EstadoTransferencia estado;
    private BigDecimal monto;
    private String moneda;
    private String cuentaOrigen;
    private String cuentaDestino;
    private LocalDateTime fechaCreacion;
}