package com.fincore.audit.entity;

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
 * Entidad RegistroAuditoria — auditoría completa de todas las operaciones.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "registros_auditoria")
@Getter
@Setter
@NoArgsConstructor
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trace_id", length = 100, nullable = false)
    private String traceId;

    @Column(name = "servicio", length = 50, nullable = false)
    private String servicio;

    @Column(name = "endpoint", length = 255)
    private String endpoint;

    @Column(name = "metodo_http", length = 10)
    private String metodoHttp;

    @Column(name = "id_usuario", length = 100)
    private String idUsuario;

    @Column(name = "rol_usuario", length = 50)
    private String rolUsuario;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "id_recurso", length = 100)
    private String idRecurso;

    @Column(name = "tipo_recurso", length = 50)
    private String tipoRecurso;

    @Column(name = "accion", length = 100)
    private String accion;

    @Column(name = "resultado", length = 20)
    private String resultado;

    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    @Column(name = "response_codigo")
    private Integer responseCodigo;

    @Column(name = "tiempo_respuesta_ms")
    private Integer tiempoRespuestaMs;

    @Column(name = "detalle", columnDefinition = "TEXT")
    private String detalle;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}
