package com.fincore.auth.entity;

import com.fincore.auth.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad RefreshToken — token de refresco para renovación de JWT.
 *
 * Implementa refresh token rotativo: cada vez que se usa un refresh token,
 * se revoca el anterior y se genera uno nuevo. Si se detecta uso de un
 * token revocado, se revocan todos los tokens del usuario (detección
 * de token robado).
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken extends BaseEntity {

    @Column(name = "token", unique = true, nullable = false, length = 512)
    private String token;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_revocacion")
    private LocalDateTime fechaRevocacion;

    @Column(name = "es_revocado", nullable = false)
    private Boolean esRevocado = false;

    @Column(name = "device_id", length = 255)
    private String deviceId;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    public boolean esValido() {
        return !esRevocado && LocalDateTime.now().isBefore(fechaExpiracion);
    }

    public void revocar() {
        this.esRevocado = true;
        this.fechaRevocacion = LocalDateTime.now();
    }
}
