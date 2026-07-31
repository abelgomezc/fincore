package com.fincore.transfer.entity;

import com.fincore.transfer.config.BaseEntity;
import com.fincore.transfer.enums.EstadoTransferencia;
import com.fincore.transfer.enums.PasoSaga;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Transferencia — representa una transferencia bancaria.
 *
 * Cada transferencia pasa por 12 pasos de la saga orchestrada y
 * 10 estados posibles. El @Version garantiza optimistic locking
 * en concurrencia.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "transferencias")
@Getter
@Setter
@NoArgsConstructor
public class Transferencia extends BaseEntity {

    @Column(name = "numero_transferencia", unique = true, nullable = false, length = 30)
    private String numeroTransferencia;

    @Column(name = "id_cuenta_origen", nullable = false)
    private Long idCuentaOrigen;

    @Column(name = "numero_cuenta_origen", nullable = false, length = 20)
    private String numeroCuentaOrigen;

    @Column(name = "id_cuenta_destino", nullable = false)
    private Long idCuentaDestino;

    @Column(name = "numero_cuenta_destino", nullable = false, length = 20)
    private String numeroCuentaDestino;

    @Column(name = "nombre_beneficiario", nullable = false, length = 255)
    private String nombreBeneficiario;

    @Column(name = "monto", nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;

    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda = "USD";

    @Column(name = "comision", nullable = false, precision = 18, scale = 2)
    private BigDecimal comision = BigDecimal.ZERO;

    @Column(name = "concepto", columnDefinition = "TEXT")
    private String concepto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoTransferencia estado = EstadoTransferencia.PENDIENTE;

    @Column(name = "paso_saga_actual", length = 50)
    private String pasoSagaActual;

    @Column(name = "intentos_saga", nullable = false)
    private Integer intentosSaga = 0;

    @Column(name = "score_fraude")
    private Integer scoreFraude;

    @Column(name = "decision_fraude", length = 20)
    private String decisionFraude;

    @Column(name = "id_usuario", nullable = false, length = 100)
    private String idUsuario;

    @Column(name = "ip_origen", nullable = false, length = 45)
    private String ipOrigen;

    @Column(name = "dispositivo", length = 255)
    private String dispositivo;

    @Column(name = "trace_id", length = 100)
    private String traceId;

    @Column(name = "fecha_iniciada", nullable = false)
    private LocalDateTime fechaIniciada = LocalDateTime.now();

    @Column(name = "fecha_completada")
    private LocalDateTime fechaCompletada;

    @Column(name = "fecha_revertida")
    private LocalDateTime fechaRevertida;

    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;
}
