package com.fincore.transfer.repository;

import com.fincore.transfer.entity.Transferencia;
import com.fincore.transfer.enums.EstadoTransferencia;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de transferencias.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface TransferenciaRepository extends JpaRepository<Transferencia, Long>, JpaSpecificationExecutor<Transferencia> {

    Optional<Transferencia> findByNumeroTransferencia(String numeroTransferencia);

    List<Transferencia> findByIdCuentaOrigen(Long idCuentaOrigen);

    List<Transferencia> findByIdCuentaDestino(Long idCuentaDestino);

    List<Transferencia> findByIdUsuario(String idUsuario);

    List<Transferencia> findByEstado(EstadoTransferencia estado);

    List<Transferencia> findByEstadoIn(List<EstadoTransferencia> estados);

    List<Transferencia> findByFechaIniciadaBetween(LocalDateTime desde, LocalDateTime hasta);

    List<Transferencia> findByFechaIniciadaBetweenAndIdCuentaOrigen(LocalDateTime desde, LocalDateTime hasta, Long idCuentaOrigen);

    Page<Transferencia> findByIdUsuario(String idUsuario, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Transferencia t WHERE t.id = :id")
    Optional<Transferencia> findByIdWithLock(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Transferencia t WHERE t.numeroTransferencia = :numero")
    Optional<Transferencia> findByNumeroTransferenciaWithLock(@Param("numero") String numeroTransferencia);
}
