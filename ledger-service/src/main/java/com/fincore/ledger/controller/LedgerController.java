package com.fincore.ledger.controller;

import com.fincore.ledger.dto.AsientoDTO;
import com.fincore.ledger.dto.LineaAsientoDTO;
import com.fincore.ledger.dto.response.AsientoResponse;
import com.fincore.ledger.dto.response.BalanceGeneralResponse;
import com.fincore.ledger.dto.response.EstadoCuentaResponse;
import com.fincore.ledger.dto.response.ExtractoResponse;
import com.fincore.ledger.entity.AsientoContable;
import com.fincore.ledger.entity.LineaAsiento;
import com.fincore.ledger.service.LedgerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador del ledger contable.
 *
 * Endpoints:
 * - POST /api/ledger/asientos — crear asiento
 * - GET /api/ledger/asientos/{numero} — obtener asiento
 * - GET /api/ledger/asientos/{numero}/lineas — líneas de un asiento
 * - POST /api/ledger/asientos/{numero}/revertir — revertir asiento
 * - GET /api/ledger/extracto/cuenta/{idCuentaBancaria} — extracto bancario
 * - GET /api/ledger/extracto/cuenta-contable/{codigoCuenta} — extracto contable
 * - GET /api/ledger/estado-cuenta/{codigoCuenta} — estado de cuenta
 * - GET /api/ledger/balance — balance general
 * - GET /api/ledger/equilibrio — verificar equilibrio del ledger
 * - GET /api/ledger/asientos/referencia/{tipoReferencia}/{idReferencia} — asientos por referencia
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@RestController
@RequestMapping("/api/ledger")
@Slf4j
public class LedgerController {

    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @PostMapping("/asientos")
    public ResponseEntity<AsientoResponse> crearAsiento(@Valid @RequestBody AsientoDTO dto) {
        log.info("POST /api/ledger/asientos");
        AsientoResponse response = ledgerService.crearAsiento(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/asientos/{numero}")
    public ResponseEntity<AsientoContable> obtenerAsiento(@PathVariable String numero) {
        log.info("GET /api/ledger/asientos/{}", numero);
        AsientoContable asiento = ledgerService.obtenerAsientoPorNumero(numero);
        return ResponseEntity.ok(asiento);
    }

    @GetMapping("/asientos/{numero}/lineas")
    public ResponseEntity<List<LineaAsiento>> obtenerLineasDeAsiento(@PathVariable String numero) {
        log.info("GET /api/ledger/asientos/{}/lineas", numero);
        AsientoContable asiento = ledgerService.obtenerAsientoPorNumero(numero);
        List<LineaAsiento> lineas = ledgerService.obtenerLineasDeAsiento(asiento.getId());
        return ResponseEntity.ok(lineas);
    }

    @PostMapping("/asientos/{numero}/revertir")
    public ResponseEntity<AsientoResponse> revertirAsiento(
            @PathVariable String numero,
            @RequestParam String descripcion,
            @RequestParam String idUsuario,
            @RequestParam String traceId) {
        log.info("POST /api/ledger/asientos/{}/revertir", numero);
        AsientoResponse response = ledgerService.reversarAsiento(numero, descripcion, idUsuario, traceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/extracto/cuenta/{idCuentaBancaria}")
    public ResponseEntity<ExtractoResponse> obtenerExtracto(
            @PathVariable Long idCuentaBancaria,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        log.info("GET /api/ledger/extracto/cuenta/{} — desde={}, hasta={}", idCuentaBancaria, desde, hasta);
        ExtractoResponse response = ledgerService.obtenerExtracto(idCuentaBancaria, desde, hasta);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/extracto/cuenta-contable/{codigoCuenta}")
    public ResponseEntity<ExtractoResponse> obtenerExtractoPorCodigo(
            @PathVariable String codigoCuenta,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        log.info("GET /api/ledger/extracto/cuenta-contable/{} — desde={}, hasta={}", codigoCuenta, desde, hasta);
        ExtractoResponse response = ledgerService.obtenerExtractoPorCodigo(codigoCuenta, desde, hasta);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/estado-cuenta/{codigoCuenta}")
    public ResponseEntity<EstadoCuentaResponse> obtenerEstadoCuenta(@PathVariable String codigoCuenta) {
        log.info("GET /api/ledger/estado-cuenta/{}", codigoCuenta);
        EstadoCuentaResponse response = ledgerService.obtenerEstadoCuenta(codigoCuenta);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceGeneralResponse> obtenerBalanceGeneral() {
        log.info("GET /api/ledger/balance");
        BalanceGeneralResponse response = ledgerService.obtenerBalanceGeneral();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/equilibrio")
    public ResponseEntity<BalanceGeneralResponse> verificarEquilibrio() {
        log.info("GET /api/ledger/equilibrio");
        BalanceGeneralResponse response = ledgerService.verificarEquilibrio();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/asientos/referencia/{tipoReferencia}/{idReferencia}")
    public ResponseEntity<List<AsientoContable>> obtenerAsientosPorReferencia(
            @PathVariable String tipoReferencia,
            @PathVariable Long idReferencia) {
        log.info("GET /api/ledger/asientos/referencia/{}/{}", tipoReferencia, idReferencia);
        List<AsientoContable> asientos = ledgerService.obtenerAsientosPorReferencia(idReferencia, tipoReferencia);
        return ResponseEntity.ok(asientos);
    }
}
