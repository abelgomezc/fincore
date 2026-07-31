package com.fincore.auth.entity;

import com.fincore.auth.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad SesionActiva — sesión activa de un usuario.
 *
 * Se almacena en Redis durante la sesión activa y se persiste
 * en la base de datos para auditoría. Permite:
 * - Listar sesiones activas de un usuario
 * - Cerrar sesión desde cualquier dispositivo
 * - Detectar sesiones simultáneas
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "sesiones_activas")
@Getter
@Setter
@NoArgsConstructor
public class SesionActiva extends BaseEntity {

    @Column(name = "session_id", unique = true, nullable = false, length = 255)
    private String sessionId;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "device_id", length = 255)
    private String deviceId;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_ultima_actividad", nullable = false)
    private LocalDateTime fechaUltimaActividad;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(name = "es_activa", nullable = false)
    private Boolean esActiva = true;

    public boolean esValida() {
        return esActiva && LocalDateTime.now().isBefore(fechaExpiracion);
    }

    public void cerrar() {
        this.esActiva = false;
        this.fechaUltimaActividad = LocalDateTime.now();
    }

    public void actualizarActividad() {
        this.fechaUltimaActividad = LocalDateTime.now();
    }
}
