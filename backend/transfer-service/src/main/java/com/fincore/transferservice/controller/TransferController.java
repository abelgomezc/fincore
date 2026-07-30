package com.fincore.transferservice.controller;

import com.fincore.transferservice.domain.dto.IniciarTransferenciaRequest;
import com.fincore.transferservice.domain.dto.TransferenciaResponse;
import com.fincore.transferservice.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transferencias")
@Tag(name = "Transferencias", description = "API para gestión de transferencias")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @Operation(summary = "Iniciar una nueva transferencia")
    public ResponseEntity<TransferenciaResponse> iniciarTransferencia(
            @RequestBody IniciarTransferenciaRequest request) {
        TransferenciaResponse response = transferService.iniciarTransferencia(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener transferencia por id")
    public ResponseEntity<TransferenciaResponse> obtenerTransferencia(@PathVariable Long id) {
        TransferenciaResponse response = transferService.obtenerTransferencia(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas las transferencias")
    public ResponseEntity<List<TransferenciaResponse>> listarTransferencias() {
        List<TransferenciaResponse> response = transferService.listarTransferencias();
        return ResponseEntity.ok(response);
    }
}