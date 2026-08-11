package com.fincore.backoffice.entity;

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
 * Auditoría de cambios administrativos en backoffice.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "auditoria_cambios_backoffice", indexes = {
        @Index(name = "idx_auditoria_cambios_backoffice_usuario", columnList = "id_usuario_sistema"),
        @Index(name = "idx_auditoria_cambios_backoffice_entidad", columnList = "entidad, id_entidad"),
        @Index(name = "idx_auditoria_cambios_backoffice_fecha", columnList = "fecha_cambio")
})
@Getter
@Setter
@NoArgsConstructor
public class AuditoriaCambioBackoffice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_usuario_sistema", nullable = false)
    private Long idUsuarioSistema;

    @Column(name = "entidad", length = 50, nullable = false)
    private String entidad;

    @Column(name = "id_entidad", length = 100, nullable = false)
    private String idEntidad;

    @Column(name = "accion", length = 100, nullable = false)
    private String accion;

    @Column(name = "valores_anteriores")
    private String valoresAnteriores;

    @Column(name = "valores_nuevos")
    private String valoresNuevos;

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
