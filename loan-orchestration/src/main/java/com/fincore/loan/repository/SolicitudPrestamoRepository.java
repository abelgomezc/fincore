package com.fincore.loan.repository;

import com.fincore.loan.entity.SolicitudPrestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de solicitudes de préstamo.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface SolicitudPrestamoRepository extends JpaRepository<SolicitudPrestamo, Long> {

    Optional<SolicitudPrestamo> findByNumeroSolicitud(String numeroSolicitud);

    List<SolicitudPrestamo> findByIdClienteOrderByFechaSolicitudDesc(Long idCliente);

    List<SolicitudPrestamo> findByEstadoOrderByFechaSolicitudDesc(com.fincore.loan.enums.EstadoSolicitudPrestamo estado);
}
