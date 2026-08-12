package com.fincore.backoffice.repository;

import com.fincore.backoffice.entity.AuditoriaCambioBackoffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de auditoría de cambios administrativos.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface AuditoriaCambioBackofficeRepository extends JpaRepository<AuditoriaCambioBackoffice, Long> {
    List<AuditoriaCambioBackoffice> findByIdUsuarioSistemaOrderByFechaCambioDesc(Long idUsuarioSistema);
}
