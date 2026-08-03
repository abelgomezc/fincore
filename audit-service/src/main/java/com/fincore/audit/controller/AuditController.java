package com.fincore.audit.controller;

import com.fincore.audit.entity.RegistroAuditoria;
import com.fincore.audit.repository.RegistroAuditoriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para consultas de auditoría.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@RestController
@RequestMapping("/api/auditoria")
public class AuditController {

    private final RegistroAuditoriaRepository auditoriaRepository;

    public AuditController(RegistroAuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @GetMapping("/trace/{traceId}")
    public ResponseEntity<List<RegistroAuditoria>> getByTraceId(@PathVariable String traceId) {
        List<RegistroAuditoria> registros = auditoriaRepository.findByTraceId(traceId);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/servicio/{servicio}")
    public ResponseEntity<Page<RegistroAuditoria>> getByServicio(
            @PathVariable String servicio,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        Page<RegistroAuditoria> registros = auditoriaRepository.findByServicio(servicio, pageable);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<Page<RegistroAuditoria>> getByUsuario(
            @PathVariable String idUsuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        Page<RegistroAuditoria> registros = auditoriaRepository.findByIdUsuario(idUsuario, pageable);
        return ResponseEntity.ok(registros);
    }
}
