package com.fincore.ledger.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad AsientoContable — asiento contable de doble partida.
 *
 * INMUTABLE: nunca se actualiza ni elimina.
 * Si una transacción necesita revertirse, se crea un nuevo asiento
 * de reversión con el mismo id_referencia pero estado REVERSADO.
 *
 * La regla de oro: débitos = créditos. Siempre.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "asientos_contables")
@Getter
@Setter
@NoArgsConstructor
public class AsientoContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_asiento", unique = true, nullable = false, length = 30)
    private String numeroAsiento;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "id_referencia")
    private Long idReferencia;

    @Column(name = "tipo_referencia", length = 50)
    private String tipoReferencia;

    @Column(name = "id_usuario", length = 100)
    private String idUsuario;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "trace_id", length = 100)
    private String traceId;

    @Column(name = "fecha_asiento", nullable = false)
    private LocalDateTime fechaAsiento = LocalDateTime.now();

    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "ACTIVO";

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // NO fecha_actualizacion — entidad inmutable

    public boolean esActivo() {
        return "ACTIVO".equals(estado);
    }

    public void reversar() {
        this.estado = "REVERSADO";
    }
}
