package com.fincore.fraudservice.repository;

import com.fincore.fraudservice.domain.entity.ReglaAntifraude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReglaAntifraudeRepository extends JpaRepository<ReglaAntifraude, Long> {
    List<ReglaAntifraude> findByActivoTrue();
    List<ReglaAntifraude> findByActivoTrueOrderByPrioridadAsc();
}
