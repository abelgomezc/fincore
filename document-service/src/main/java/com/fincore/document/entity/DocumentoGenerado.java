package com.fincore.document.entity;

import com.fincore.document.enums.EstadoDocumento;
import com.fincore.document.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad DocumentoGenerado — documento generado a partir de una plantilla.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "documentos_generados", indexes = {
        @Index(name = "idx_documentos_generados_entidad", columnList = "entidad, id_entidad"),
        @Index(name = "idx_documentos_generados_fecha", columnList = "fecha_generacion")
})
@Getter
@Setter
@NoArgsConstructor
public class DocumentoGenerado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entidad", length = 50, nullable = false)
    private String entidad;

    @Column(name = "id_entidad", length = 100, nullable = false)
    private String idEntidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 50)
    private TipoDocumento tipoDocumento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoDocumento estado = EstadoDocumento.BORRADOR;

    @Column(name = "nombre_archivo", length = 255)
    private String nombreArchivo;

    @Column(name = "url_archivo", columnDefinition = "TEXT")
    private String urlArchivo;

    @Column(name = "contenido_html", columnDefinition = "TEXT")
    private String contenidoHtml;

    @Column(name = "hash_archivo", length = 255)
    private String hashArchivo;

    @Column(name = "id_plantilla")
    private Long idPlantilla;

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

    @Column(name = "fecha_firma")
    private LocalDateTime fechaFirma;

    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;

    @Column(name = "creado_por", length = 100)
    private String creadoPor;

    @Column(name = "actualizado_por", length = 100)
    private String actualizadoPor;

    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        fechaGeneracion = LocalDateTime.now();
        creadoPor = "system";
        actualizadoPor = "system";
        version = 0L;
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoPor = "system";
    }
}
