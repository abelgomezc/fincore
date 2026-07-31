package com.fincore.transfer.repository;

import com.fincore.transfer.entity.TransferenciaEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de historial de estados de transferencia.
 *
 * INMUTABLE: solo lectura.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface TransferenciaEstadoRepository extends JpaRepository<TransferenciaEstado, Long> {

    List<TransferenciaEstado> findByIdTransferenciaOrderByFechaCambio(Long idTransferencia);

    List<TransferenciaEstado> findByIdTransferenciaAndEstadoNuevo(Long idTransferencia, String estadoNuevo);
}
