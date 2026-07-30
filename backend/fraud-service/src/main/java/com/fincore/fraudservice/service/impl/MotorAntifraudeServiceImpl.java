package com.fincore.fraudservice.service.impl;

import com.fincore.fraudservice.domain.dto.EvaluacionFraudeResponse;
import com.fincore.fraudservice.domain.dto.EvaluarFraudeRequest;
import com.fincore.fraudservice.domain.entity.EvaluacionFraude;
import com.fincore.fraudservice.domain.enums.DecisionFraude;
import com.fincore.fraudservice.domain.entity.ReglaAntifraude;
import com.fincore.fraudservice.repository.EvaluacionFraudeRepository;
import com.fincore.fraudservice.repository.ReglaAntifraudeRepository;
import com.fincore.fraudservice.service.MotorAntifraudeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MotorAntifraudeServiceImpl implements MotorAntifraudeService {

    private final ReglaAntifraudeRepository reglaAntifraudeRepository;
    private final EvaluacionFraudeRepository evaluacionFraudeRepository;

    @Override
    @Transactional
    public EvaluacionFraudeResponse evaluarTransaccion(EvaluarFraudeRequest request) {
        List<ReglaAntifraude> reglas = reglaAntifraudeRepository.findByActivoTrueOrderByPrioridadAsc();

        BigDecimal puntuacionRiesgo = calcularPuntuacionRiesgo(request, reglas);
        DecisionFraude decision = determinarDecision(puntuacionRiesgo);
        String motivo = generarMotivo(decision, reglas);

        EvaluacionFraude evaluacion = EvaluacionFraude.builder()
                .idTransaccion(request.getIdTransaccion())
                .idCuentaOrigen(request.getIdCuentaOrigen())
                .idCuentaDestino(request.getIdCuentaDestino())
                .monto(request.getMonto())
                .moneda(request.getMoneda())
                .decision(decision)
                .puntuacionRiesgo(puntuacionRiesgo)
                .motivo(motivo)
                .build();

        evaluacionFraudeRepository.save(evaluacion);

        return EvaluacionFraudeResponse.builder()
                .idEvaluacion(evaluacion.getId())
                .idTransaccion(evaluacion.getIdTransaccion())
                .decision(evaluacion.getDecision())
                .puntuacionRiesgo(evaluacion.getPuntuacionRiesgo())
                .motivo(evaluacion.getMotivo())
                .fechaEvaluacion(evaluacion.getFechaEvaluacion())
                .build();
    }

    private BigDecimal calcularPuntuacionRiesgo(EvaluarFraudeRequest request, List<ReglaAntifraude> reglas) {
        BigDecimal puntuacion = BigDecimal.ZERO;

        for (ReglaAntifraude regla : reglas) {
            BigDecimal factor = BigDecimal.ONE;
            if (regla.getTipoRegla().equals("MONTO_ALTO") && request.getMonto().compareTo(regla.getUmbral()) > 0) {
                factor = new BigDecimal("0.8");
            }
            puntuacion = puntuacion.add(factor.multiply(new BigDecimal(regla.getPrioridad())));
        }

        return puntuacion.divide(BigDecimal.valueOf(reglas.size()), 2, RoundingMode.HALF_UP);
    }

    private DecisionFraude determinarDecision(BigDecimal puntuacionRiesgo) {
        if (puntuacionRiesgo.compareTo(new BigDecimal("7.0")) >= 0) {
            return DecisionFraude.RECHAZADO;
        } else if (puntuacionRiesgo.compareTo(new BigDecimal("4.0")) >= 0) {
            return DecisionFraude.EN_REVISION;
        } else {
            return DecisionFraude.APROBADO;
        }
    }

    private String generarMotivo(DecisionFraude decision, List<ReglaAntifraude> reglas) {
        return String.format("Decision %s basada en %d reglas activas", decision, reglas.size());
    }
}
