package com.fincore.account.repository;

import com.fincore.account.entity.SaldoHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio de saldos históricos.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface SaldoHistoricoRepository extends JpaRepository<SaldoHistorico, Long> {

    List<SaldoHistorico> findByCuenta_IdOrderByFechaSnapshotDesc(Long idCuenta);

    List<SaldoHistorico> findByCuenta_IdAndFechaSnapshotBetween(Long idCuenta, LocalDate desde, LocalDate hasta);

    boolean existsByCuenta_IdAndFechaSnapshot(Long idCuenta, LocalDate fecha);
}
