package com.fincore.document.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Response de plantilla de documento.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Getter
@Setter
@NoArgsConstructor
public class PlantillaResponse {
    private Long id;
    private String tipoDocumento;
    private String nombre;
    private String descripcion;
    private String contenidoHtml;
    private Integer version;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
