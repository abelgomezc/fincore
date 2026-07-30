package com.fincore.auditservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "entidad_tipo", nullable = false, length = 100)
    private String entidadTipo;

    @Column(name = "entidad_id", nullable = false)
    private Long entidadId;

    @Column(name = "accion", nullable = false, length = 50)
    private String accion;

    @Column(name = "estado_anterior", columnDefinition = "TEXT")
    private String estadoAnterior;

    @Column(name = "estado_nuevo", columnDefinition = "TEXT")
    private String estadoNuevo;

    @Column(name = "usuario_tipo", length = 50)
    private String usuarioTipo;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "trace_id", length = 100)
    private String traceId;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
    }
}
