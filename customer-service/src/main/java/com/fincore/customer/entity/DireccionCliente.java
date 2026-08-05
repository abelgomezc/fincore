package com.fincore.customer.entity;

import com.fincore.customer.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidad DireccionCliente — dirección física del cliente.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "direcciones")
@Getter
@Setter
@NoArgsConstructor
public class DireccionCliente extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "tipo_direccion", nullable = false, length = 20)
    private String tipoDireccion;

    @Column(name = "calle_principal", nullable = false, columnDefinition = "TEXT")
    private String callePrincipal;

    @Column(name = "calle_secundaria", columnDefinition = "TEXT")
    private String calleSecundaria;

    @Column(name = "ciudad", nullable = false, length = 100)
    private String ciudad;

    @Column(name = "provincia", nullable = false, length = 100)
    private String provincia;

    @Column(name = "pais", nullable = false, length = 3)
    private String pais = "EC";

    @Column(name = "codigo_postal", length = 20)
    private String codigoPostal;

    @Column(name = "latitud", precision = 10, scale = 8)
    private BigDecimal latitud;

    @Column(name = "longitud", precision = 11, scale = 8)
    private BigDecimal longitud;
}
