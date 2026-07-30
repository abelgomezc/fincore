package com.fincore.customerservice.domain.dto;

import com.fincore.customerservice.domain.enums.EstadoCliente;
import com.fincore.customerservice.domain.enums.TipoPersona;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/*
 * DTO de request para crear un nuevo cliente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearClienteRequest {

    @NotNull(message = "El tipo de persona es obligatorio")
    private TipoPersona tipoPersona;

    @NotBlank(message = "El número de identificación es obligatorio")
    @Size(max = 20, message = "El número de identificación no puede exceder 20 caracteres")
    private String numeroIdentificacion;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres")
    private String nombres;

    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;

    @Size(max = 150, message = "La razón social no puede exceder 150 caracteres")
    private String razonSocial;

    private LocalDate fechaNacimiento;

    @Size(max = 50, message = "La nacionalidad no puede exceder 50 caracteres")
    private String nacionalidad;

    private String direccion;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @NotNull(message = "El estado es obligatorio")
    private EstadoCliente estado;
}
