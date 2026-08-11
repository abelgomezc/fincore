package com.fincore.customer.entity;

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
 * Auditoría de cambios de estado de cliente.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "auditoria_estados_cliente", indexes = {
        @Index(name = "idx_auditoria_estados_cliente", columnList = "id_cliente"),
        @Index(name = "idx_auditoria_estados_cliente_fecha", columnList = "fecha_cambio")
})
@Getter
@Setter
@NoArgsConstructor
public class AuditoriaEstadoCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "estado_anterior", length = 20, nullable = false)
    private String estadoAnterior;

    @Column(name = "estado_nuevo", length = 20, nullable = false)
    private String estadoNuevo;

    @Column(name = "motivo")
    private String motivo;

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
