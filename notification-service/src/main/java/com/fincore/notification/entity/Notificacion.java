package com.fincore.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad de notificaciones históricas.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "notificaciones")
@Getter
@Setter
@NoArgsConstructor
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_notificacion", length = 50, nullable = false)
    private String tipoNotificacion;

    @Column(name = "id_usuario", length = 100)
    private String idUsuario;

    @Column(name = "id_transferencia")
    private Long idTransferencia;

    @Column(name = "numero_transferencia", length = 50)
    private String numeroTransferencia;

    @Column(name = "canal", length = 20, nullable = false)
    private String canal;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "titulo", length = 255)
    private String titulo;

    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "datos_adicionales", columnDefinition = "jsonb")
    private String datosAdicionales;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
