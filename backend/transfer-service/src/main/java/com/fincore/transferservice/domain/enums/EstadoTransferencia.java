package com.fincore.transferservice.domain.enums;

public enum EstadoTransferencia {
    PENDIENTE,
    VALIDANDO,
    AUTORIZADA,
    EN_REVISION,
    RESERVANDO,
    PROCESANDO,
    ACREDITANDO,
    COMPLETADA,
    RECHAZADA,
    REVERTIDA,
    ERROR
}