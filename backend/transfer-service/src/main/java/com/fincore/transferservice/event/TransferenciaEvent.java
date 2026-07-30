package com.fincore.transferservice.event;

import com.fincore.transferservice.domain.enums.EstadoTransferencia;
import java.time.LocalDateTime;

public record TransferenciaEvent(
    Long transferenciaId,
    EstadoTransferencia estado,
    LocalDateTime timestamp,
    String mensaje
) {
}