package com.fincore.auth.entity;

import com.fincore.auth.config.BaseEntity;
import com.fincore.auth.enums.EstadoUsuario;
import com.fincore.auth.enums.Rol;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad Usuario — representa un usuario del sistema de autenticación.
 *
 * Un usuario puede ser un cliente del banco (con id_cliente asociado)
 * o un empleado del backoffice (sin id_cliente).
 *
 * Reglas:
 * - El email es único
 * - El password_hash usa BCrypt
 * - Después de 5 intentos fallidos, el usuario se bloquea 30 minutos
 * - El rol determina los permisos del usuario
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario extends BaseEntity {

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "primer_nombre", nullable = false, length = 100)
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 100)
    private String segundoNombre;

    @Column(name = "primer_apellido", nullable = false, length = 100)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 100)
    private String segundoApellido;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 20)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoUsuario estado;

    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "intentos_fallidos", nullable = false)
    private Integer intentosFallidos = 0;

    @Column(name = "ultimo_intento_fallido")
    private LocalDateTime ultimoIntentoFallido;

    @Column(name = "fecha_bloqueo")
    private LocalDateTime fechaBloqueo;

    public boolean isBloqueado() {
        if (estado != EstadoUsuario.BLOQUEADO) {
            return false;
        }
        // Verificar si el bloqueo ha expirado
        if (fechaBloqueo != null) {
            LocalDateTime ahora = LocalDateTime.now();
            if (ahora.isAfter(fechaBloqueo.plusMinutes(30))) {
                // El bloqueo ha expirado — desbloquear
                this.estado = EstadoUsuario.ACTIVO;
                this.intentosFallidos = 0;
                this.fechaBloqueo = null;
                setFechaActualizacion(ahora);
                return false;
            }
        }
        return true;
    }

    public void registrarIntentoFallido(int maxIntentos) {
        this.intentosFallidos++;
        this.ultimoIntentoFallido = LocalDateTime.now();
        if (this.intentosFallidos >= maxIntentos) {
            this.estado = EstadoUsuario.BLOQUEADO;
            this.fechaBloqueo = LocalDateTime.now();
        }
    }

    public void resetearIntentosFallidos() {
        this.intentosFallidos = 0;
        this.ultimoIntentoFallido = null;
        this.fechaBloqueo = null;
    }

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
}
