package com.fincore.auditservice.controller;

import com.fincore.auditservice.domain.dto.RegistroAuditoriaRequest;
import com.fincore.auditservice.domain.dto.RegistroAuditoriaResponse;
import com.fincore.auditservice.service.RegistroAuditoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final RegistroAuditoriaService registroAuditoriaService;

    public AuditController(RegistroAuditoriaService registroAuditoriaService) {
        this.registroAuditoriaService = registroAuditoriaService;
    }

    @PostMapping
    public ResponseEntity<RegistroAuditoriaResponse> registrar(@Valid @RequestBody RegistroAuditoriaRequest request) {
        RegistroAuditoriaResponse response = registroAuditoriaService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
