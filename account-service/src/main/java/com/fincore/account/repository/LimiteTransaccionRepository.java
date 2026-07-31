package com.fincore.account.repository;

import com.fincore.account.entity.LimiteTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de límites de transacción.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface LimiteTransaccionRepository extends JpaRepository<LimiteTransaccion, Long> {

    Optional<LimiteTransaccion> findByIdCuenta(Long idCuenta);

    @Lock(org.springframework.data.jpa.repository.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT lt FROM LimiteTransaccion lt WHERE lt.idCuenta = :idCuenta")
    Optional<LimiteTransaccion> findByIdCuentaWithLock(@Param("idCuenta") Long idCuenta);
}
