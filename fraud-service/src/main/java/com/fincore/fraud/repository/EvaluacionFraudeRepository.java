package com.fincore.fraud.repository;

import com.fincore.fraud.entity.EvaluacionFraude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de evaluaciones de fraude.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface EvaluacionFraudeRepository extends JpaRepository<EvaluacionFraude, Long> {
    List<EvaluacionFraude> findByIdTransferencia(Long idTransferencia);
}
