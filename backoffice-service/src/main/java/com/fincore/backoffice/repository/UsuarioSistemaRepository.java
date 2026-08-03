package com.fincore.backoffice.repository;

import com.fincore.backoffice.entity.UsuarioSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de usuarios del backoffice.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface UsuarioSistemaRepository extends JpaRepository<UsuarioSistema, Long> {
    Optional<UsuarioSistema> findByUsername(String username);
}
