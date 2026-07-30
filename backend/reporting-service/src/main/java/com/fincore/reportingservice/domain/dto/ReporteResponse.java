package com.fincore.reportingservice.domain.dto;

public record ReporteResponse(Long id, String tipo, String parametros, String rutaArchivo, LocalDateTime generadoEn) {
}
