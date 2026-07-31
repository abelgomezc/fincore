package com.fincore.account.repository;

import com.fincore.account.entity.Cuenta;
import com.fincore.account.enums.EstadoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de cuentas bancarias.
 *
 * Usa @Lock(PESSIMISTIC_WRITE) en operaciones críticas para evitar
 * race conditions en la actualización de saldos.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long>, JpaSpecificationExecutor<Cuenta> {

    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

    List<Cuenta> findByIdCliente(Long idCliente);

    List<Cuenta> findByIdClienteAndEstado(Long idCliente, EstadoCuenta estado);

    boolean existsByNumeroCuenta(String numeroCuenta);

    @Lock(org.springframework.data.jpa.repository.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cuenta c WHERE c.id = :id")
    Optional<Cuenta> findByIdWithLock(@Param("id") Long id);

    @Lock(org.springframework.data.jpa.repository.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cuenta c WHERE c.numeroCuenta = :numeroCuenta")
    Optional<Cuenta> findByNumeroCuentaWithLock(@Param("numeroCuenta") String numeroCuenta);

    @Lock(org.springframework.data.jpa.repository.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cuenta c WHERE c.idCliente = :idCliente")
    List<Cuenta> findByIdClienteWithLock(@Param("idCliente") Long idCliente);
}
