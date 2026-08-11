package com.fincore.transfer.entity;

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
 * Auditoría extendida de transferencias.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "auditoria_transferencias", indexes = {
        @Index(name = "idx_auditoria_transferencias_transferencia", columnList = "id_transferencia"),
        @Index(name = "idx_auditoria_transferencias_fecha", columnList = "fecha_accion"),
        @Index(name = "idx_auditoria_transferencias_trace", columnList = "trace_id")
})
@Getter
@Setter
@NoArgsConstructor
public class AuditoriaTransferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_transferencia", nullable = false)
    private Long idTransferencia;

    @Column(name = "accion", length = 50, nullable = false)
    private String accion;

    @Column(name = "estado_anterior", length = 20)
    private String estadoAnterior;

    @Column(name = "estado_nuevo", length = 20)
    private String estadoNuevo;

    @Column(name = "resultado", length = 20, nullable = false)
    private String resultado;

    @Column(name = "detalle")
    private String detalle;

    @Column(name = "error_detalle")
    private String errorDetalle;

    @Column(name = "id_usuario", length = 100)
    private String idUsuario;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "dispositivo", length = 255)
    private String dispositivo;

    @Column(name = "trace_id", length = 100)
    private String traceId;

    @Column(name = "fecha_accion", nullable = false)
    private LocalDateTime fechaAccion;

    @Column(name = "creado_por", length = 100)
    private String creadoPor;

    @Column(name = "actualizado_por", length = 100)
    private String actualizadoPor;

    @Column(name = "version", nullable = false)
    private Long version = 0L;
}
