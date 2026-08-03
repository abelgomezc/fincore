package com.fincore.fraud.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad PerfilTransaccional — perfil de comportamiento del cliente.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "perfil_transaccional")
@Getter
@Setter
@NoArgsConstructor
public class PerfilTransaccional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente", nullable = false, unique = true)
    private Long idCliente;

    @Column(name = "promedio_monto_30d")
    private BigDecimal promedioMonto30d;

    @Column(name = "maximo_monto_30d")
    private BigDecimal maximoMonto30d;

    @Column(name = "total_transferencias_30d")
    private Integer totalTransferencias30d;

    @Column(name = "paises_habituales", columnDefinition = "jsonb")
    private String paisesHabituales;

    @Column(name = "dispositivos_habituales", columnDefinition = "jsonb")
    private String dispositivosHabituales;

    @Column(name = "horarios_habituales", columnDefinition = "jsonb")
    private String horariosHabituales;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
