package com.fincore.auth.repository;

import com.fincore.auth.entity.AuditoriaPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de auditoría de cambios de contraseña.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface AuditoriaPasswordRepository extends JpaRepository<AuditoriaPassword, Long> {
    List<AuditoriaPassword> findByIdUsuarioOrderByFechaCambioDesc(Long idUsuario);
}
