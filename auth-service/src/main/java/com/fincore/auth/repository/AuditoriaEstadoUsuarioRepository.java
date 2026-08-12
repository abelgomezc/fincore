package com.fincore.auth.repository;

import com.fincore.auth.entity.AuditoriaEstadoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de auditoría de cambios de estado de usuario.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface AuditoriaEstadoUsuarioRepository extends JpaRepository<AuditoriaEstadoUsuario, Long> {
    List<AuditoriaEstadoUsuario> findByIdUsuarioOrderByFechaCambioDesc(Long idUsuario);
}
