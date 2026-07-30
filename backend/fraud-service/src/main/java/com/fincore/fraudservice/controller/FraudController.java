package com.fincore.fraudservice.controller;

import com.fincore.fraudservice.domain.dto.EvaluacionFraudeResponse;
import com.fincore.fraudservice.domain.dto.EvaluarFraudeRequest;
import com.fincore.fraudservice.service.MotorAntifraudeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fraud")
@RequiredArgsConstructor
@Tag(name = "Fraude", description = "API de evaluacion de fraude")
public class FraudController {

    private final MotorAntifraudeService motorAntifraudeService;

    @Operation(summary = "Evaluar transaccion contra reglas antifraude")
    @PostMapping("/evaluar")
    public ResponseEntity<EvaluacionFraudeResponse> evaluarTransaccion(@Valid @RequestBody EvaluarFraudeRequest request) {
        EvaluacionFraudeResponse response = motorAntifraudeService.evaluarTransaccion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
