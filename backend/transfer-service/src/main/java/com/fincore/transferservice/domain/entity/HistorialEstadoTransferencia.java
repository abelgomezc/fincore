package com.fincore.transferservice.domain.entity;

import com.fincore.transferservice.domain.enums.EstadoTransferencia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "historial_estados_transferencia")
public class HistorialEstadoTransferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "transferencia_id", nullable = false)
    private Long transferenciaId;

    @Column(name = "estado_anterior")
    @Enumerated(EnumType.STRING)
    private EstadoTransferencia estadoAnterior;

    @Column(name = "estado_nuevo", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoTransferencia estadoNuevo;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    @Column(name = "motivo")
    private String motivo;
}