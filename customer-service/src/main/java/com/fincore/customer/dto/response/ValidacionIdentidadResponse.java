package com.fincore.customer.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Response de validación de identidad.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Getter
@Setter
@NoArgsConstructor
public class ValidacionIdentidadResponse {
    private Long id;
    private Long idCliente;
    private String tipoValidacion;
    private String estado;
    private String proveedor;
    private Integer puntajeConfianza;
    private String detalle;
    private LocalDateTime fechaValidacion;
    private String idTransaccion;
}
