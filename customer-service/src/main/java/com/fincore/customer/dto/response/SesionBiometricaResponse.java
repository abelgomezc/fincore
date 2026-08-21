package com.fincore.customer.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Response de sesión biométrica.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Getter
@Setter
@NoArgsConstructor
public class SesionBiometricaResponse {
    private Long id;
    private Long idCliente;
    private String tipoBiometria;
    private String estado;
    private String proveedor;
    private Integer puntajeConfianza;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String idSesionProveedor;
    private String detalle;
}
