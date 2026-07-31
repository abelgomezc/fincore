package com.fincore.auth.dto.response;

import com.fincore.auth.enums.EstadoUsuario;
import com.fincore.auth.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta con información del usuario.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String email;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String nombreCompleto;
    private Rol rol;
    private EstadoUsuario estado;
    private Long idCliente;
    private Integer intentosFallidos;
    private String fechaCreacion;
    private String fechaActualizacion;
}
