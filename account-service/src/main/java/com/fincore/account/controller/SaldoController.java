package com.fincore.account.controller;

import com.fincore.account.dto.response.SaldoResponse;
import com.fincore.account.query.ObtenerMovimientosQuery;
import com.fincore.account.query.ObtenerSaldoQuery;
import com.fincore.account.service.CuentaQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de saldos y movimientos.
 *
 * Endpoints:
 * - GET /api/saldos/{id} — obtener saldo de cuenta
 * - GET /api/saldos/numero/{numero} — obtener saldo por número
 * - GET /api/saldos/cliente/{idCliente} — saldos de todas las cuentas
 * - GET /api/saldos/movimientos/{id} — historial de movimientos
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@RestController
@RequestMapping("/api/saldos")
@Slf4j
public class SaldoController {

    private final CuentaQueryService cuentaQueryService;

    public SaldoController(CuentaQueryService cuentaQueryService) {
        this.cuentaQueryService = cuentaQueryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaldoResponse> obtenerSaldo(@PathVariable Long id) {
        log.info("GET /api/saldos/{}", id);
        SaldoResponse response = cuentaQueryService.obtenerSaldo(
                ObtenerSaldoQuery.builder().idCuenta(id).build());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<SaldoResponse> obtenerSaldoPorNumero(@PathVariable String numero) {
        log.info("GET /api/saldos/numero/{}", numero);
        SaldoResponse response = cuentaQueryService.obtenerSaldo(
                ObtenerSaldoQuery.builder().numeroCuenta(numero).build());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<SaldoResponse>> obtenerSaldosCliente(@PathVariable Long idCliente) {
        log.info("GET /api/saldos/cliente/{}", idCliente);
        List<SaldoResponse> response = cuentaQueryService.obtenerCuentasPorCliente(idCliente).stream()
                .map(c -> SaldoResponse.builder()
                        .idCuenta(c.getId())
                        .numeroCuenta(c.getNumeroCuenta())
                        .saldoContable(c.getSaldoContable())
                        .saldoDisponible(c.getSaldoDisponible())
                        .saldoRetenido(c.getSaldoRetenido())
                        .saldoProyectado(c.getSaldoProyectado())
                        .moneda(c.getMoneda())
                        .estado(c.getEstado().name())
                        .build())
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/movimientos/{id}")
    public ResponseEntity<List<SaldoResponse>> obtenerMovimientos(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        log.info("GET /api/saldos/movimientos/{} — desde={}, hasta={}", id, desde, hasta);

        ObtenerMovimientosQuery query = ObtenerMovimientosQuery.builder()
                .idCuenta(id)
                .fechaDesde(desde)
                .fechaHasta(hasta)
                .build();

        List<SaldoResponse> response = cuentaQueryService.obtenerMovimientos(query);
        return ResponseEntity.ok(response);
    }
}
