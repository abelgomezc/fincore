package com.fincore.accountservice.controller;

import com.fincore.accountservice.domain.dto.CrearCuentaRequest;
import com.fincore.accountservice.domain.dto.CuentaResponse;
import com.fincore.accountservice.domain.dto.MovimientoRequest;
import com.fincore.accountservice.domain.dto.MovimientoResponse;
import com.fincore.accountservice.service.CuentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account-service/v1/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    @PostMapping
    public ResponseEntity<CuentaResponse> crear(@Valid @RequestBody CrearCuentaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaService.crear(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponse>> obtenerTodas() {
        return ResponseEntity.ok(cuentaService.obtenerTodas());
    }

    @PostMapping("/{id}/movimientos")
    public ResponseEntity<MovimientoResponse> registrarMovimiento(
            @PathVariable Long id,
            @Valid @RequestBody MovimientoRequest request) {
        request.setCuentaId(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaService.registrarMovimiento(request));
    }

    @GetMapping("/{id}/movimientos")
    public ResponseEntity<List<MovimientoResponse>> obtenerMovimientosPorCuenta(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaService.obtenerMovimientosPorCuenta(id));
    }
}