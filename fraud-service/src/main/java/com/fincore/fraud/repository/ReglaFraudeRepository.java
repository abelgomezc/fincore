package com.fincore.fraud.repository;

import com.fincore.fraud.entity.ReglaFraude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de reglas de fraude.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface ReglaFraudeRepository extends JpaRepository<ReglaFraude, Long> {
    List<ReglaFraude> findByEsActivoTrue();
}
