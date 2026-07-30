package com.fincore.customerservice.domain.dto;

import com.fincore.customerservice.domain.enums.EstadoCliente;
import com.fincore.customerservice.domain.enums.TipoPersona;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * DTO de respuesta con los datos de un cliente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {

    private Long id;
    private TipoPersona tipoPersona;
    private String numeroIdentificacion;
    private String nombres;
    private String apellidos;
    private String razonSocial;
    private LocalDate fechaNacimiento;
    private String nacionalidad;
    private String direccion;
    private String telefono;
    private String email;
    private EstadoCliente estado;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
