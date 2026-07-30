package com.fincore.fraudservice.repository;

import com.fincore.fraudservice.domain.entity.EvaluacionFraude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluacionFraudeRepository extends JpaRepository<EvaluacionFraude, Long> {
    Optional<EvaluacionFraude> findByIdTransaccion(String idTransaccion);
    boolean existsByIdTransaccion(String idTransaccion);
}
