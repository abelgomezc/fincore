package com.fincore.ledger.repository;

import com.fincore.ledger.entity.AsientoContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de asientos contables.
 *
 * Inmutable: solo métodos de lectura. NO hay save() para actualizar.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface AsientoContableRepository extends JpaRepository<AsientoContable, Long> {

    Optional<AsientoContable> findByNumeroAsiento(String numeroAsiento);

    List<AsientoContable> findByIdReferenciaAndTipoReferencia(Long idReferencia, String tipoReferencia);

    List<AsientoContable> findByTraceId(String traceId);

    List<AsientoContable> findByFechaAsientoBetween(LocalDateTime desde, LocalDateTime hasta);

    List<AsientoContable> findByEstado(String estado);

    @Query("SELECT COUNT(a) FROM AsientoContable a")
    long contarAsientos();

    @Query("SELECT SUM(l.monto) FROM LineaAsiento l " +
           "JOIN AsientoContable a ON l.idAsiento = a.id " +
           "WHERE l.tipoMovimiento = 'DEBITO' AND a.estado = 'ACTIVO'")
    java.math.BigDecimal sumarDebitos();

    @Query("SELECT SUM(l.monto) FROM LineaAsiento l " +
           "JOIN AsientoContable a ON l.idAsiento = a.id " +
           "WHERE l.tipoMovimiento = 'CREDITO' AND a.estado = 'ACTIVO'")
    java.math.BigDecimal sumarCreditos();
}
