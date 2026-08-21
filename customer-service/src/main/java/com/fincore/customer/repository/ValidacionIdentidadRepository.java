package com.fincore.customer.repository;

import com.fincore.customer.entity.ValidacionIdentidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de validaciones de identidad.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface ValidacionIdentidadRepository extends JpaRepository<ValidacionIdentidad, Long> {

    List<ValidacionIdentidad> findByIdClienteOrderByFechaValidacionDesc(Long idCliente);

    @Query("SELECT v FROM ValidacionIdentidad v WHERE v.cliente.id = :idCliente AND v.fechaValidacion >= :desde")
    List<ValidacionIdentidad> findByIdClienteAndFechaValidacionAfter(@Param("idCliente") Long idCliente, @Param("desde") LocalDateTime desde);

    List<ValidacionIdentidad> findByTipoValidacionAndEstado(String tipoValidacion, String estado);
}
