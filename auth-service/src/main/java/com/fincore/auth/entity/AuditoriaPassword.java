package com.fincore.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Auditoría de cambios de contraseña.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "auditoria_passwords", indexes = {
        @Index(name = "idx_auditoria_passwords_usuario", columnList = "id_usuario"),
        @Index(name = "idx_auditoria_passwords_fecha", columnList = "fecha_cambio")
})
@Getter
@Setter
@NoArgsConstructor
public class AuditoriaPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "password_hash_anterior", nullable = false)
    private String passwordHashAnterior;

    @Column(name = "password_hash_nuevo", nullable = false)
    private String passwordHashNuevo;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "dispositivo", length = 255)
    private String dispositivo;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    @Column(name = "creado_por", length = 100)
    private String creadoPor;

    @Column(name = "actualizado_por", length = 100)
    private String actualizadoPor;

    @Column(name = "version", nullable = false)
    private Long version = 0L;
}
