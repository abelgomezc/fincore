package com.fincore.document.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fincore.document.enums.EstadoDocumento;
import com.fincore.document.enums.TipoDocumento;
import java.time.LocalDateTime;

/**
 * Response de documento generado.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Getter
@Setter
@NoArgsConstructor
public class DocumentoResponse {
    private Long id;
    private String entidad;
    private String idEntidad;
    private String tipoDocumento;
    private String estado;
    private String nombreArchivo;
    private String urlArchivo;
    private String hashArchivo;
    private Long idPlantilla;
    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaFirma;
    private LocalDateTime fechaVencimiento;
}
