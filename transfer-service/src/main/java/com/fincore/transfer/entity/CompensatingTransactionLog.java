package com.fincore.transfer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad CompensatingTransactionLog — log de transacciones de compensación.
 *
 * Registra cada compensating transaction ejecutada durante la reversión
 * de la saga. Permite auditoría completa de compensaciones.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "compensating_transactions_log")
@Getter
@Setter
@NoArgsConstructor
public class CompensatingTransactionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_transferencia", nullable = false)
    private Long idTransferencia;

    @Column(name = "paso_original", nullable = false, length = 50)
    private String pasoOriginal;

    @Column(name = "paso_compensacion", nullable = false, length = 50)
    private String pasoCompensacion;

    @Column(name = "estado_ejecucion", nullable = false, length = 20)
    private String estadoEjecucion;

    @Column(name = "detalle", columnDefinition = "TEXT")
    private String detalle;

    @Column(name = "error_detalle", columnDefinition = "TEXT")
    private String errorDetalle;

    @Column(name = "fecha_ejecucion", nullable = false)
    private LocalDateTime fechaEjecucion = LocalDateTime.now();
}
