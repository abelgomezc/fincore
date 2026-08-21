package com.fincore.loan.repository;

import com.fincore.loan.entity.HistorialSolicitudPrestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de historial de solicitudes de préstamo.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface HistorialSolicitudPrestamoRepository extends JpaRepository<HistorialSolicitudPrestamo, Long> {

    List<HistorialSolicitudPrestamo> findByIdSolicitudOrderByFechaCambioDesc(Long idSolicitud);
}
