package com.fincore.customer.entity;

import com.fincore.customer.config.BaseEntity;
import com.fincore.customer.enums.EstadoCliente;
import com.fincore.customer.enums.TipoCliente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Entidad Cliente — representa un cliente del banco.
 *
 * Puede ser persona natural o jurídica. Contiene información de
 * contacto, dirección y estado. La entidad está vinculada a un
 * usuario de auth-service mediante el email.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
public class Cliente extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cliente", nullable = false, length = 20)
    private TipoCliente tipoCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCliente estado = EstadoCliente.ACTIVO;

    @Column(name = "primer_nombre", nullable = false, length = 100)
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 100)
    private String segundoNombre;

    @Column(name = "primer_apellido", nullable = false, length = 100)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 100)
    private String segundoApellido;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "genero", length = 20)
    private String genero;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(name = "pais", length = 3)
    private String pais = "EC";

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DocumentoIdentidad> documentos;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DireccionCliente> direcciones;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ContactoEmergencia> contactosEmergencia;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<KycVerificacion> kycVerificaciones;

    public String getNombreCompleto() {
        StringBuilder sb = new StringBuilder(primerNombre);
        if (segundoNombre != null && !segundoNombre.isEmpty()) {
            sb.append(" ").append(segundoNombre);
        }
        sb.append(" ").append(primerApellido);
        if (segundoApellido != null && !segundoApellido.isEmpty()) {
            sb.append(" ").append(segundoApellido);
        }
        return sb.toString();
    }

    public boolean isKycAprobado() {
        return kycVerificaciones != null && kycVerificaciones.stream()
                .anyMatch(k -> k.getEstado() == com.fincore.customer.enums.EstadoKyc.APROBADO);
    }

    public void addDocumento(DocumentoIdentidad documento) {
        documentos.add(documento);
        documento.setCliente(this);
    }

    public void addDireccion(DireccionCliente direccion) {
        direcciones.add(direccion);
        direccion.setCliente(this);
    }

    public void addContactoEmergencia(ContactoEmergencia contacto) {
        contactosEmergencia.add(contacto);
        contacto.setCliente(this);
    }
}
