package com.fincore.notification.repository;

import com.fincore.notification.entity.Notificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de notificaciones.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByIdUsuario(String idUsuario);

    List<Notificacion> findByIdUsuarioOrderByFechaCreacionDesc(String idUsuario);

    Page<Notificacion> findByIdUsuario(String idUsuario, Pageable pageable);

    List<Notificacion> findByIdTransferencia(Long idTransferencia);
}
