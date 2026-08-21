package com.fincore.document.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fincore.document.enums.EstadoFirma;
import java.time.LocalDateTime;

/**
 * Response de firma electrónica.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Getter
@Setter
@NoArgsConstructor
public class FirmaElectronicaResponse {
    private Long id;
    private Long idDocumento;
    private String idFirmante;
    private String nombreFirmante;
    private String emailFirmante;
    private String estado;
    private String proveedor;
    private String idTransaccionProveedor;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaFirma;
    private String ipFirmante;
    private String huellaDigital;
    private String certificadoSerial;
    private String motivoRechazo;
}
