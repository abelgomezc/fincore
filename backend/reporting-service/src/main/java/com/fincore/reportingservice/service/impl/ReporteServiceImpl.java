package com.fincore.reportingservice.service.impl;

import com.fincore.reportingservice.domain.dto.GenerarReporteRequest;
import com.fincore.reportingservice.domain.dto.ReporteResponse;
import com.fincore.reportingservice.domain.entity.Reporte;
import com.fincore.reportingservice.repository.ReporteRepository;
import com.fincore.reportingservice.service.ReporteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {
    private final ReporteRepository reporteRepository;

    @Override
    @Transactional
    public ReporteResponse generarReporte(GenerarReporteRequest request) {
        Reporte reporte = Reporte.builder()
                .tipo(request.tipo())
                .parametros(request.parametros())
                .rutaArchivo("/tmp/reporte-" + System.currentTimeMillis() + ".pdf")
                .build();
        reporte = reporteRepository.save(reporte);
        return toResponse(reporte);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteResponse> listarReportes() {
        return reporteRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ReporteResponse toResponse(Reporte reporte) {
        return new ReporteResponse(
                reporte.getId(),
                reporte.getTipo(),
                reporte.getParametros(),
                reporte.getRutaArchivo(),
                reporte.getGeneradoEn()
        );
    }
}
