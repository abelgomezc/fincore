package com.fincore.batch.repository;

import com.fincore.batch.entity.Conciliacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de conciliaciones batch.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface ConciliacionRepository extends JpaRepository<Conciliacion, Long> {
}
