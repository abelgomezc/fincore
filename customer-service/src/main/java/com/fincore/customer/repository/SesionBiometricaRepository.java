package com.fincore.customer.repository;

import com.fincore.customer.entity.SesionBiometrica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de sesiones biométricas.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface SesionBiometricaRepository extends JpaRepository<SesionBiometrica, Long> {

    List<SesionBiometrica> findByIdClienteOrderByFechaInicioDesc(Long idCliente);

    @Query("SELECT s FROM SesionBiometrica s WHERE s.cliente.id = :idCliente AND s.fechaInicio >= :desde")
    List<SesionBiometrica> findByIdClienteAndFechaInicioAfter(@Param("idCliente") Long idCliente, @Param("desde") LocalDateTime desde);

    List<SesionBiometrica> findByTipoBiometriaAndEstado(String tipoBiometria, String estado);
}
