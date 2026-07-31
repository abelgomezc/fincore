package com.fincore.account.entity;

import com.fincore.account.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad Beneficiario — beneficiarios frecuentes de un cliente.
 *
 * Permite almacenar cuentas a las que el cliente transfiere frecuentemente
 * para acelerar el proceso de transferencia.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "beneficiarios_frecuentes")
@Getter
@Setter
@NoArgsConstructor
public class Beneficiario extends BaseEntity {

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuenta_beneficiario", nullable = false)
    private Cuenta cuentaBeneficiario;

    @Column(name = "nombre_beneficiario", nullable = false, length = 255)
    private String nombreBeneficiario;

    @Column(name = "numero_cuenta", nullable = false, length = 20)
    private String numeroCuenta;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();
}
