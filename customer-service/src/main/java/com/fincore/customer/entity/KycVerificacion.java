package com.fincore.customer.entity;

import com.fincore.customer.config.BaseEntity;
import com.fincore.customer.enums.EstadoKyc;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad KycVerificacion — registro del proceso de verificación KYC.
 *
 * Un cliente puede tener múltiples verificaciones a lo largo del tiempo.
 * La verificación incluye validación de documentos, AML y datos biométricos.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "kyc_verificaciones")
@Getter
@Setter
@NoArgsConstructor
public class KycVerificacion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoKyc estado = EstadoKyc.PENDIENTE;

    @Column(name = "fecha_verificacion")
    private LocalDateTime fechaVerificacion;

    @Column(name = "verificado_por", length = 100)
    private String verificadoPor;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}
