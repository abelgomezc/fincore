package com.fincore.customer.repository;

import com.fincore.customer.entity.AuditoriaEstadoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de auditoría de cambios de estado de cliente.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface AuditoriaEstadoClienteRepository extends JpaRepository<AuditoriaEstadoCliente, Long> {
    List<AuditoriaEstadoCliente> findByIdClienteOrderByFechaCambioDesc(Long idCliente);
}
