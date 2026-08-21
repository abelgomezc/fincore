package com.fincore.customer.controller;

import com.fincore.customer.dto.request.IniciarBiometriaRequest;
import com.fincore.customer.dto.request.ValidarIdentidadRequest;
import com.fincore.customer.dto.response.SesionBiometricaResponse;
import com.fincore.customer.dto.response.ValidacionIdentidadResponse;
import com.fincore.customer.service.IdentidadBiometriaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de identidad y biometría.
 *
 * Endpoints:
 * - POST /api/clientes/{id}/identidad/validar
 * - GET /api/clientes/{id}/identidad/validaciones
 * - POST /api/clientes/{id}/biometria/iniciar
 * - PUT /api/clientes/biometria/{idSesion}/finalizar
 * - GET /api/clientes/{id}/biometria/sesiones
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@RestController
@RequestMapping("/api/clientes")
@Slf4j
public class IdentidadBiometriaController {

    private final IdentidadBiometriaService identidadBiometriaService;

    public IdentidadBiometriaController(IdentidadBiometriaService identidadBiometriaService) {
        this.identidadBiometriaService = identidadBiometriaService;
    }

    @PostMapping("/{id}/identidad/validar")
    public ResponseEntity<ValidacionIdentidadResponse> validarIdentidad(
            @PathVariable Long id,
            @Valid @RequestBody ValidarIdentidadRequest request) {
        request = new ValidarIdentidadRequest(id, request.tipoValidacion(), request.proveedor(), request.detalle());
        ValidacionIdentidadResponse response = identidadBiometriaService.validarIdentidad(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/identidad/validaciones")
    public ResponseEntity<List<ValidacionIdentidadResponse>> consultarValidaciones(@PathVariable Long id) {
        List<ValidacionIdentidadResponse> response = identidadBiometriaService.consultarValidaciones(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/biometria/iniciar")
    public ResponseEntity<SesionBiometricaResponse> iniciarBiometria(
            @PathVariable Long id,
            @Valid @RequestBody IniciarBiometriaRequest request) {
        request = new IniciarBiometriaRequest(id, request.tipoBiometria(), request.proveedor(), request.idSesionProveedor());
        SesionBiometricaResponse response = identidadBiometriaService.iniciarBiometria(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/biometria/{idSesion}/finalizar")
    public ResponseEntity<SesionBiometricaResponse> finalizarBiometria(
            @PathVariable Long idSesion,
            @RequestParam String estado,
            @RequestParam(required = false) Integer puntajeConfianza,
            @RequestParam(required = false) String detalle) {
        SesionBiometricaResponse response = identidadBiometriaService.finalizarBiometria(idSesion, estado, puntajeConfianza, detalle);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/biometria/sesiones")
    public ResponseEntity<List<SesionBiometricaResponse>> consultarBiometrias(@PathVariable Long id) {
        List<SesionBiometricaResponse> response = identidadBiometriaService.consultarBiometrias(id);
        return ResponseEntity.ok(response);
    }
}
