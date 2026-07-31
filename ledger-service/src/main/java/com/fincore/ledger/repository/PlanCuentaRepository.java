package com.fincore.ledger.repository;

import com.fincore.ledger.entity.PlanCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio del plan de cuentas.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface PlanCuentaRepository extends JpaRepository<PlanCuenta, Long> {

    Optional<PlanCuenta> findByCodigo(String codigo);

    List<PlanCuenta> findByTipo(String tipo);

    List<PlanCuenta> findByNivel(Integer nivel);

    List<PlanCuenta> findByEsHojaTrueAndEsActivaTrue();

    boolean existsByCodigo(String codigo);
}
