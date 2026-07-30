package com.fincore.fraudservice.service;

import com.fincore.fraudservice.domain.dto.EvaluacionFraudeResponse;
import com.fincore.fraudservice.domain.dto.EvaluarFraudeRequest;

public interface MotorAntifraudeService {
    EvaluacionFraudeResponse evaluarTransaccion(EvaluarFraudeRequest request);
}
