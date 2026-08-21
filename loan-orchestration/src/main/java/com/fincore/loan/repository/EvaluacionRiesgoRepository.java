package com.fincore.loan.repository;

import com.fincore.loan.entity.EvaluacionRiesgo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de evaluaciones de riesgo.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface EvaluacionRiesgoRepository extends JpaRepository<EvaluacionRiesgo, Long> {

    List<EvaluacionRiesgo> findByIdSolicitudOrderByFechaEvaluacionDesc(Long idSolicitud);
}
