package com.fincore.customer.dto.request;

import com.fincore.customer.enums.EstadoCliente;
import com.fincore.customer.enums.TipoCliente;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para crear un nuevo cliente.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearClienteRequest {

    @NotNull(message = "El tipo de cliente es obligatorio")
    private TipoCliente tipoCliente;

    @NotBlank(message = "El primer nombre es obligatorio")
    @Size(max = 100)
    private String primerNombre;

    @Size(max = 100)
    private String segundoNombre;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(max = 100)
    private String primerApellido;

    @Size(max = 100)
    private String segundoApellido;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    @Pattern(regexp = "MASCULINO|FEMENINO|OTRO", message = "Género inválido")
    private String genero;

    @Email(message = "El email debe ser válido")
    @Size(max = 255)
    private String email;

    @Size(max = 20)
    private String telefono;

    @Size(max = 100)
    private String ciudad;

    @Size(max = 3)
    private String pais = "EC";

    @NotEmpty(message = "Debe proporcionar al menos un documento")
    private List<DocumentoRequest> documentos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentoRequest {
        @NotBlank
        private String tipoDocumento;

        @NotBlank
        private String numeroDocumento;

        @NotNull
        private LocalDate fechaExpedicion;

        @NotNull
        private LocalDate fechaExpiracion;

        @Size(max = 3)
        private String paisEmision = "EC";
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DireccionRequest {
        @NotBlank
        private String tipoDireccion;

        @NotBlank
        private String callePrincipal;

        private String calleSecundaria;

        @NotBlank
        private String ciudad;

        @NotBlank
        private String provincia;

        @Size(max = 3)
        private String pais = "EC";

        private String codigoPostal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactoRequest {
        @NotBlank
        private String nombre;

        @NotBlank
        private String telefono;

        @NotBlank
        private String parentesco;
    }
}
