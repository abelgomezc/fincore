package com.fincore.transfer.repository;

import com.fincore.transfer.entity.CompensatingTransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de log de transacciones de compensación.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface CompensatingTransactionLogRepository extends JpaRepository<CompensatingTransactionLog, Long> {

    List<CompensatingTransactionLog> findByIdTransferenciaOrderByFechaEjecucion(Long idTransferencia);
}
