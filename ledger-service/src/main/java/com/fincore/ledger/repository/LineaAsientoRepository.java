package com.fincore.ledger.repository;

import com.fincore.ledger.entity.LineaAsiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de líneas de asiento.
 *
 * Inmutable: solo métodos de lectura.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface LineaAsientoRepository extends JpaRepository<LineaAsiento, Long> {

    List<LineaAsiento> findByIdAsiento(Long idAsiento);

    List<LineaAsiento> findByIdCuentaBancaria(Long idCuentaBancaria);

    List<LineaAsiento> findByCodigoCuenta(String codigoCuenta);

    @Query("SELECT l FROM LineaAsiento l WHERE l.idAsiento IN " +
           "(SELECT a.id FROM AsientoContable a WHERE a.idReferencia = :idReferencia AND a.tipoReferencia = :tipoReferencia)")
    List<LineaAsiento> findByIdReferenciaAndTipoReferencia(@Param("idReferencia") Long idReferencia,
                                                            @Param("tipoReferencia") String tipoReferencia);

    @Query("SELECT SUM(l.monto) FROM LineaAsiento l WHERE l.idAsiento = :idAsiento AND l.tipoMovimiento = 'DEBITO'")
    java.math.BigDecimal sumarDebitosPorAsiento(@Param("idAsiento") Long idAsiento);

    @Query("SELECT SUM(l.monto) FROM LineaAsiento l WHERE l.idAsiento = :idAsiento AND l.tipoMovimiento = 'CREDITO'")
    java.math.BigDecimal sumarCreditosPorAsiento(@Param("idAsiento") Long idAsiento);
}
