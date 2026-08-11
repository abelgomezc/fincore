package com.fincore.transfer.repository;

import com.fincore.transfer.entity.AuditoriaTransferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de auditoría extendida de transferencias.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface AuditoriaTransferenciaRepository extends JpaRepository<AuditoriaTransferencia, Long> {
}
