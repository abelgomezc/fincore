package com.fincore.audit.repository;

import com.fincore.audit.entity.EventoSaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de eventos de saga.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface EventoSagaRepository extends JpaRepository<EventoSaga, Long> {

    List<EventoSaga> findByIdTransferenciaOrderByOrden(Long idTransferencia);
}
