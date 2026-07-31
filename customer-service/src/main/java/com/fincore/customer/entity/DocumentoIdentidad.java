package com.fincore.customer.entity;

import com.fincore.customer.config.BaseEntity;
import com.fincore.customer.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entidad DocumentoIdentidad — documento de identidad del cliente.
 *
 * Un cliente puede tener múltiples documentos (cédula, pasaporte, RUC).
 * La verificación de cédula ecuatoriana se valida con el algoritmo
 * de módulo 10.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "documentos_identidad")
@Getter
@Setter
@NoArgsConstructor
public class DocumentoIdentidad extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 20)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false, length = 20)
    private String numeroDocumento;

    @Column(name = "fecha_expedicion", nullable = false)
    private LocalDate fechaExpedicion;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDate fechaExpiracion;

    @Column(name = "pais_emision", nullable = false, length = 3)
    private String paisEmision = "EC";

    @Column(name = "verificado", nullable = false)
    private Boolean verificado = false;

    @Column(name = "fecha_verificacion")
    private LocalDate fechaVerificacion;

    public boolean esVigente() {
        return fechaExpiracion.isAfter(LocalDate.now());
    }
}
