package com.fincore.loan.controller;

import com.fincore.loan.dto.request.AprobarRechazarRequest;
import com.fincore.loan.dto.request.CrearSolicitudPrestamoRequest;
import com.fincore.loan.dto.request.EvaluarRiesgoRequest;
import com.fincore.loan.dto.response.EvaluacionRiesgoResponse;
import com.fincore.loan.dto.response.SolicitudPrestamoResponse;
import com.fincore.loan.service.PrestamoOrchestrationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de orquestación de préstamos.
 *
 * Endpoints:
 * - POST /api/prestamos/solicitudes
 * - GET /api/prestamos/solicitudes
 * - GET /api/prestamos/solicitudes/{id}
 * - POST /api/prestamos/solicitudes/{id}/iniciar-flujo
 * - POST /api/prestamos/solicitudes/{id}/validar-identidad
 * - POST /api/prestamos/solicitudes/{id}/consultar-buro
 * - POST /api/prestamos/evaluar-riesgo
 * - POST /api/prestamos/solicitudes/{id}/aprobar
 * - POST /api/prestamos/solicitudes/{id}/rechazar
 * - POST /api/prestamos/solicitudes/{id}/generar-contrato
 * - POST /api/prestamos/solicitudes/{id}/enviar-contrato
 * - POST /api/prestamos/solicitudes/{id}/desembolsar
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@RestController
@RequestMapping("/api/prestamos")
@Slf4j
public class PrestamoController {

    private final PrestamoOrchestrationService prestamoOrchestrationService;

    public PrestamoController(PrestamoOrchestrationService prestamoOrchestrationService) {
        this.prestamoOrchestrationService = prestamoOrchestrationService;
    }

    @PostMapping("/solicitudes")
    public ResponseEntity<SolicitudPrestamoResponse> crearSolicitud(@Valid @RequestBody CrearSolicitudPrestamoRequest request) {
        SolicitudPrestamoResponse response = prestamoOrchestrationService.crearSolicitud(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/solicitudes")
    public ResponseEntity<List<SolicitudPrestamoResponse>> listarSolicitudes() {
        List<SolicitudPrestamoResponse> response = prestamoOrchestrationService.listarSolicitudes();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/solicitudes/{id}")
    public ResponseEntity<SolicitudPrestamoResponse> obtenerSolicitud(@PathVariable Long id) {
        SolicitudPrestamoResponse response = prestamoOrchestrationService.obtenerSolicitud(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/solicitudes/{id}/iniciar-flujo")
    public ResponseEntity<SolicitudPrestamoResponse> iniciarFlujo(@PathVariable Long id) {
        SolicitudPrestamoResponse response = prestamoOrchestrationService.iniciarFlujo(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/solicitudes/{id}/validar-identidad")
    public ResponseEntity<SolicitudPrestamoResponse> validarIdentidad(@PathVariable Long id) {
        SolicitudPrestamoResponse response = prestamoOrchestrationService.validarIdentidad(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/solicitudes/{id}/consultar-buro")
    public ResponseEntity<SolicitudPrestamoResponse> consultarBuro(@PathVariable Long id) {
        SolicitudPrestamoResponse response = prestamoOrchestrationService.consultarBuro(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/evaluar-riesgo")
    public ResponseEntity<EvaluacionRiesgoResponse> evaluarRiesgo(@Valid @RequestBody EvaluarRiesgoRequest request) {
        EvaluacionRiesgoResponse response = prestamoOrchestrationService.evaluarRiesgo(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/solicitudes/{id}/aprobar")
    public ResponseEntity<SolicitudPrestamoResponse> aprobarSolicitud(@PathVariable Long id, @Valid @RequestBody AprobarRechazarRequest request) {
        request = new AprobarRechazarRequest(id, "APROBAR", request.motivo());
        SolicitudPrestamoResponse response = prestamoOrchestrationService.aprobarSolicitud(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/solicitudes/{id}/rechazar")
    public ResponseEntity<SolicitudPrestamoResponse> rechazarSolicitud(@PathVariable Long id, @Valid @RequestBody AprobarRechazarRequest request) {
        request = new AprobarRechazarRequest(id, "RECHAZAR", request.motivo());
        SolicitudPrestamoResponse response = prestamoOrchestrationService.rechazarSolicitud(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/solicitudes/{id}/generar-contrato")
    public ResponseEntity<SolicitudPrestamoResponse> generarContrato(@PathVariable Long id) {
        SolicitudPrestamoResponse response = prestamoOrchestrationService.generarContrato(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/solicitudes/{id}/enviar-contrato")
    public ResponseEntity<SolicitudPrestamoResponse> enviarContratoAFirmar(@PathVariable Long id) {
        SolicitudPrestamoResponse response = prestamoOrchestrationService.enviarContratoAFirmar(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/solicitudes/{id}/desembolsar")
    public ResponseEntity<SolicitudPrestamoResponse> desembolsar(@PathVariable Long id) {
        SolicitudPrestamoResponse response = prestamoOrchestrationService.desembolsar(id);
        return ResponseEntity.ok(response);
    }
}
