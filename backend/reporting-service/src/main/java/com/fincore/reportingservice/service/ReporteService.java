package com.fincore.reportingservice.service;

import com.fincore.reportingservice.domain.dto.GenerarReporteRequest;
import com.fincore.reportingservice.domain.dto.ReporteResponse;

import java.util.List;

public interface ReporteService {
    ReporteResponse generarReporte(GenerarReporteRequest request);
    List<ReporteResponse> listarReportes();
}
