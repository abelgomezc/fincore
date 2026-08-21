package com.fincore.customer.entity;

import com.fincore.customer.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad ValidacionIdentidad — registra intentos de verificación de identidad del cliente.
 *
 * Incluye validación de documento, biometría facial, huella, OTP, etc.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "validaciones_identidad", indexes = {
        @Index(name = "idx_validaciones_identidad_cliente", columnList = "id_cliente"),
        @Index(name = "idx_validaciones_identidad_fecha", columnList = "fecha_validacion")
})
@Getter
@Setter
@NoArgsConstructor
public class ValidacionIdentidad extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "tipo_validacion", length = 50, nullable = false)
    private String tipoValidacion;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "proveedor", length = 100)
    private String proveedor;

    @Column(name = "puntaje_confianza")
    private Integer puntajeConfianza;

    @Column(name = "detalle", columnDefinition = "TEXT")
    private String detalle;

    @Column(name = "fecha_validacion")
    private LocalDateTime fechaValidacion;

    @Column(name = "id_transaccion", length = 100)
    private String idTransaccion;
}
