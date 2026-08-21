package com.fincore.customer.entity;

import com.fincore.customer.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad SesionBiometrica — registra sesiones de autenticación/validación biométrica.
 *
 * Soporta facial, huella dactilar, voz, etc.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "sesiones_biometricas", indexes = {
        @Index(name = "idx_sesiones_biometricas_cliente", columnList = "id_cliente"),
        @Index(name = "idx_sesiones_biometricas_fecha", columnList = "fecha_inicio")
})
@Getter
@Setter
@NoArgsConstructor
public class SesionBiometrica extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "tipo_biometria", length = 50, nullable = false)
    private String tipoBiometria;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "proveedor", length = 100)
    private String proveedor;

    @Column(name = "puntaje_confianza")
    private Integer puntajeConfianza;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "id_sesion_proveedor", length = 150)
    private String idSesionProveedor;

    @Column(name = "detalle", columnDefinition = "TEXT")
    private String detalle;
}
