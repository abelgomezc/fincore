package com.fincore.customer.entity;

import com.fincore.customer.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad ContactoEmergencia — contacto de emergencia del cliente.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "contactos_emergencia")
@Getter
@Setter
@NoArgsConstructor
public class ContactoEmergencia extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @Column(name = "parentesco", nullable = false, length = 50)
    private String parentesco;
}
