package com.fincore.loan.entity;

import com.fincore.loan.enums.EstadoSolicitudPrestamo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad HistorialSolicitudPrestamo — historial de cambios de estado de la solicitud.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "historial_solicitudes_prestamo", indexes = {
        @Index(name = "idx_historial_solicitudes_solicitud", columnList = "id_solicitud"),
        @Index(name = "idx_historial_solicitudes_fecha", columnList = "fecha_cambio")
})
@Getter
@Setter
@NoArgsConstructor
public class HistorialSolicitudPrestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_solicitud", nullable = false)
    private Long idSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 50)
    private EstadoSolicitudPrestamo estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 50)
    private EstadoSolicitudPrestamo estadoNuevo;

    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    @Column(name = "creado_por", length = 100)
    private String creadoPor;

    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        fechaCambio = LocalDateTime.now();
        creadoPor = "system";
        version = 0L;
    }
}
