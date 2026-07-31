package com.fincore.transfer.controller;

import com.fincore.transfer.dto.request.CrearTransferenciaRequest;
import com.fincore.transfer.dto.response.TransferenciaResponse;
import com.fincore.transfer.enums.EstadoTransferencia;
import com.fincore.transfer.service.TransferenciaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * REST Controller para transferencias.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@RestController
@RequestMapping("/api/v1/transferencias")
@Slf4j
public class TransferenciaController {

    private final TransferenciaService transferenciaService;

    public TransferenciaController(TransferenciaService transferenciaService) {
        this.transferenciaService = transferenciaService;
    }

    /**
     * Crea una nueva transferencia.
     * La procesa de forma asíncrona mediante el saga orchestrator.
     */
    @PostMapping
    public ResponseEntity<TransferenciaResponse> crearTransferencia(
            @Valid @RequestBody CrearTransferenciaRequest request,
            @RequestHeader(value = "X-Usuario-ID", required = false) String idUsuario,
            @RequestHeader(value = "X-IP-Origen", required = false) String ipOrigen) {

        log.info("POST /api/v1/transferencias: origen={}, destino={}, monto={}",
                request.getNumeroCuentaOrigen(), request.getNumeroCuentaDestino(), request.getMonto());

        String usuario = idUsuario != null ? idUsuario : "anonymous";
        String ip = ipOrigen != null ? ipOrigen : "0.0.0.0";

        TransferenciaResponse response = transferenciaService.crearTransferencia(request, usuario, ip);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Obtiene una transferencia por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransferenciaResponse> obtenerTransferencia(@PathVariable Long id) {
        log.info("GET /api/v1/transferencias/{}", id);
        TransferenciaResponse response = transferenciaService.obtenerTransferencia(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene una transferencia por número.
     */
    @GetMapping("/numero/{numeroTransferencia}")
    public ResponseEntity<TransferenciaResponse> obtenerPorNumero(@PathVariable String numeroTransferencia) {
        log.info("GET /api/v1/transferencias/numero/{}", numeroTransferencia);
        TransferenciaResponse response = transferenciaService.obtenerTransferenciaPorNumero(numeroTransferencia);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista transferencias del usuario paginadas.
     */
    @GetMapping("/mis-transferencias")
    public ResponseEntity<Page<TransferenciaResponse>> listarMisTransferencias(
            @RequestHeader(value = "X-Usuario-ID", required = false) String idUsuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        String usuario = idUsuario != null ? idUsuario : "anonymous";
        Page<TransferenciaResponse> response = transferenciaService.listarTransferenciasPorUsuario(usuario, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista transferencias por estado.
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<TransferenciaResponse>> listarPorEstado(@PathVariable EstadoTransferencia estado) {
        log.info("GET /api/v1/transferencias/estado/{}", estado);
        List<TransferenciaResponse> response = transferenciaService.listarPorEstado(estado);
        return ResponseEntity.ok(response);
    }

    /**
     * Revierte una transferencia completada (cancelación manual).
     */
    @PostMapping("/{id}/revertir")
    public ResponseEntity<TransferenciaResponse> revertirTransferencia(
            @PathVariable Long id,
            @RequestParam String motivo) {
        log.info("POST /api/v1/transferencias/{}/revertir: motivo={}", id, motivo);
        TransferenciaResponse response = transferenciaService.revertirTransferencia(id, motivo);
        return ResponseEntity.ok(response);
    }
}
