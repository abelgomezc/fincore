package com.fincore.notificationservice.repository;

import com.fincore.notificationservice.domain.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
 * Repositorio de acceso a datos para la entidad Notificacion
 */
@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
}
