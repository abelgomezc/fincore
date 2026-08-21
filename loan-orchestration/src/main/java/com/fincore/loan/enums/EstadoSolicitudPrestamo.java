package com.fincore.loan.enums;

/**
 * Estado de la solicitud de préstamo.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum EstadoSolicitudPrestamo {
    BORRADOR,
    ENVIADA,
    IDENTIDAD_VERIFICADA,
    BURO_CONSULTADO,
    EVALUANDO_RIESGO,
    APROBADA,
    RECHAZADA,
    CONTRATO_GENERADO,
    CONTRATO_FIRMADO,
    DESEMBOLSADA,
    CANCELADA
}
