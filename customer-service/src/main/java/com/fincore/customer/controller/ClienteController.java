package com.fincore.customer.controller;

import com.fincore.customer.dto.request.ActualizarClienteRequest;
import com.fincore.customer.dto.request.CrearClienteRequest;
import com.fincore.customer.dto.request.ActualizarClienteRequest;
import com.fincore.customer.dto.response.ClienteResponse;
import com.fincore.customer.dto.response.KycResponse;
import com.fincore.customer.entity.AuditoriaEstadoCliente;
import com.fincore.customer.enums.EstadoKyc;
import com.fincore.customer.service.ClienteService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de clientes.
 *
 * Endpoints:
 * - POST /api/clientes — crear cliente
 * - PUT /api/clientes/{id} — actualizar cliente
 * - GET /api/clientes/{id} — obtener cliente
 * - GET /api/clientes/email/{email} — obtener por email
 * - GET /api/clientes — listar todos
 * - GET /api/clientes/buscar — buscar por nombre (paginado)
 * - PUT /api/clientes/{id}/bloquear — bloquear cliente
 * - PUT /api/clientes/{id}/desbloquear — desbloquear cliente
 * - PUT /api/clientes/{id}/suspender — suspender cliente
 * - PUT /api/clientes/{id}/reactivar — reactivar cliente
 * - DELETE /api/clientes/{id} — eliminar cliente
 * - GET /api/clientes/{id}/kyc — obtener estado KYC
 * - PUT /api/clientes/{id}/kyc — actualizar KYC
 * - GET /api/clientes/validar-cedula/{cedula} — validar cédula ecuatoriana
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@RestController
@RequestMapping("/api/clientes")
@Slf4j
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> crearCliente(@Valid @RequestBody CrearClienteRequest request) {
        log.info("POST /api/clientes — creando cliente");
        ClienteResponse response = clienteService.crearCliente(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarClienteRequest request) {
        log.info("PUT /api/clientes/{}", id);
        ClienteResponse response = clienteService.actualizarCliente(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtenerCliente(@PathVariable Long id) {
        log.info("GET /api/clientes/{}", id);
        ClienteResponse response = clienteService.obtenerClientePorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ClienteResponse> obtenerClientePorEmail(@PathVariable String email) {
        log.info("GET /api/clientes/email/{}", email);
        ClienteResponse response = clienteService.obtenerClientePorEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listarClientes() {
        log.info("GET /api/clientes — listar todos");
        List<ClienteResponse> response = clienteService.listarClientes();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<ClienteResponse>> buscarClientes(
            @RequestParam String nombre,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/clientes/buscar?nombre={}", nombre);
        Page<ClienteResponse> response = clienteService.buscarClientes(nombre, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/bloquear")
    public ResponseEntity<Void> bloquearCliente(
            @PathVariable Long id,
            @RequestParam String motivo) {
        log.info("PUT /api/clientes/{}/bloquear — motivo: {}", id, motivo);
        clienteService.bloquearCliente(id, motivo);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/desbloquear")
    public ResponseEntity<Void> desbloquearCliente(@PathVariable Long id) {
        log.info("PUT /api/clientes/{}/desbloquear", id);
        clienteService.desbloquearCliente(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/suspender")
    public ResponseEntity<Void> suspenderCliente(
            @PathVariable Long id,
            @RequestParam String motivo) {
        log.info("PUT /api/clientes/{}/suspender — motivo: {}", id, motivo);
        clienteService.suspenderCliente(id, motivo);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivarCliente(@PathVariable Long id) {
        log.info("PUT /api/clientes/{}/reactivar", id);
        clienteService.reactivarCliente(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/auditoria")
    public ResponseEntity<List<AuditoriaEstadoCliente>> consultarAuditoria(@PathVariable Long id) {
        log.info("GET /api/clientes/{}/auditoria", id);
        List<AuditoriaEstadoCliente> auditoria = clienteService.consultarAuditoria(id);
        return ResponseEntity.ok(auditoria);
    }
}
