package com.fincore.document.entity;

import com.fincore.document.enums.EstadoFirma;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad FirmaElectronica — registro de firma electrónica aplicada a un documento.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "firmas_electronicas", indexes = {
        @Index(name = "idx_firmas_electronicas_documento", columnList = "id_documento"),
        @Index(name = "idx_firmas_electronicas_fecha", columnList = "fecha_firma")
})
@Getter
@Setter
@NoArgsConstructor
public class FirmaElectronica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_documento", nullable = false)
    private Long idDocumento;

    @Column(name = "id_firmante", length = 100, nullable = false)
    private String idFirmante;

    @Column(name = "nombre_firmante", length = 200, nullable = false)
    private String nombreFirmante;

    @Column(name = "email_firmante", length = 200)
    private String emailFirmante;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoFirma estado = EstadoFirma.PENDIENTE;

    @Column(name = "proveedor", length = 100)
    private String proveedor;

    @Column(name = "id_transaccion_proveedor", length = 150)
    private String idTransaccionProveedor;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_firma")
    private LocalDateTime fechaFirma;

    @Column(name = "ip_firmante", length = 45)
    private String ipFirmante;

    @Column(name = "huella_digital", length = 255)
    private String huellaDigital;

    @Column(name = "certificado_serial", length = 100)
    private String certificadoSerial;

    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    @Column(name = "creado_por", length = 100)
    private String creadoPor;

    @Column(name = "actualizado_por", length = 100)
    private String actualizadoPor;

    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        creadoPor = "system";
        actualizadoPor = "system";
        version = 0L;
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoPor = "system";
    }
}
