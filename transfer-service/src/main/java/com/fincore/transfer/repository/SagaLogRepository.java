package com.fincore.transfer.repository;

import com.fincore.transfer.entity.SagaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de log de saga.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface SagaLogRepository extends JpaRepository<SagaLog, Long> {

    List<SagaLog> findByIdTransferenciaOrderByOrden(Long idTransferencia);

    List<SagaLog> findByPasoSagaAndEstadoEjecucion(String pasoSaga, String estadoEjecucion);
}
