package com.fincore.customer.dto.request;

import com.fincore.customer.enums.EstadoCliente;
import com.fincore.customer.enums.TipoCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar un cliente existente.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarClienteRequest {

    @Size(max = 100)
    private String primerNombre;

    @Size(max = 100)
    private String segundoNombre;

    @Size(max = 100)
    private String primerApellido;

    @Size(max = 100)
    private String segundoApellido;

    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 20)
    private String telefono;

    @Size(max = 100)
    private String ciudad;

    private EstadoCliente estado;

    private String motivoBloqueo;
}
