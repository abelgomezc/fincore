package com.fincore.account.repository;

import com.fincore.account.entity.AuditoriaEstadoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de auditoría de cambios de estado de cuenta.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface AuditoriaEstadoCuentaRepository extends JpaRepository<AuditoriaEstadoCuenta, Long> {
    List<AuditoriaEstadoCuenta> findByIdCuentaOrderByFechaCambioDesc(Long idCuenta);
}
