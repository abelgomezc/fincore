package com.fincore.account.repository;

import com.fincore.account.entity.TipoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de tipos de cuenta.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface TipoCuentaRepository extends JpaRepository<TipoCuenta, Long> {

    Optional<TipoCuenta> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}
