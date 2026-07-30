package com.fincore.reportingservice.controller;

import com.fincore.reportingservice.domain.dto.GenerarReporteRequest;
import com.fincore.reportingservice.domain.dto.ReporteResponse;
import com.fincore.reportingservice.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@Tag(name = "Reportes", description = "Generación y consulta de reportes")
public class ReporteController {
    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @PostMapping
    @Operation(summary = "Generar reporte")
    public ResponseEntity<ReporteResponse> generarReporte(@Valid @RequestBody GenerarReporteRequest request) {
        ReporteResponse response = reporteService.generarReporte(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar reportes")
    public ResponseEntity<List<ReporteResponse>> listarReportes() {
        return ResponseEntity.ok(reporteService.listarReportes());
    }
}
