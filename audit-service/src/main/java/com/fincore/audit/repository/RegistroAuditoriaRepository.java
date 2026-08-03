package com.fincore.audit.repository;

import com.fincore.audit.entity.RegistroAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de registros de auditoría.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {

    List<RegistroAuditoria> findByTraceId(String traceId);

    List<RegistroAuditoria> findByServicio(String servicio);

    List<RegistroAuditoria> findByIdUsuario(String idUsuario);

    List<RegistroAuditoria> findByFechaCreacionBetween(LocalDateTime desde, LocalDateTime hasta);

    Page<RegistroAuditoria> findByServicio(String servicio, Pageable pageable);

    Page<RegistroAuditoria> findByIdUsuario(String idUsuario, Pageable pageable);
}
