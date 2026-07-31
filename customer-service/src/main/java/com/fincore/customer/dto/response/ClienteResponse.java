package com.fincore.customer.dto.response;

import com.fincore.customer.enums.EstadoCliente;
import com.fincore.customer.enums.TipoCliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO de respuesta con información del cliente.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {

    private Long id;
    private TipoCliente tipoCliente;
    private EstadoCliente estado;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String nombreCompleto;
    private LocalDate fechaNacimiento;
    private String genero;
    private String email;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String pais;
    private String fechaRegistro;
    private List<DocumentoResponse> documentos;
    private List<DireccionResponse> direcciones;
    private List<ContactoEmergenciaResponse> contactosEmergencia;
    private boolean kycAprobado;
    private String fechaCreacion;
    private String fechaActualizacion;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentoResponse {
        private Long id;
        private String tipoDocumento;
        private String numeroDocumento;
        private String fechaExpedicion;
        private String fechaExpiracion;
        private String paisEmision;
        private boolean verificado;
        private boolean vigente;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DireccionResponse {
        private Long id;
        private String tipoDireccion;
        private String callePrincipal;
        private String calleSecundaria;
        private String ciudad;
        private String provincia;
        private String pais;
        private String codigoPostal;
        private Double latitud;
        private Double longitud;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactoEmergenciaResponse {
        private Long id;
        private String nombre;
        private String telefono;
        private String parentesco;
    }
}
