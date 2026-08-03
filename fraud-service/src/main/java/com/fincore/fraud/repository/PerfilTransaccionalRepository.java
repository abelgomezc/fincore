package com.fincore.fraud.repository;

import com.fincore.fraud.entity.PerfilTransaccional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de perfiles transaccionales.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface PerfilTransaccionalRepository extends JpaRepository<PerfilTransaccional, Long> {
    Optional<PerfilTransaccional> findByIdCliente(Long idCliente);
}
