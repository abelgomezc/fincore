package com.fincore.auth.repository;

import com.fincore.auth.entity.Usuario;
import com.fincore.auth.enums.EstadoUsuario;
import com.fincore.auth.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de usuarios.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByIdCliente(Long idCliente);

    List<Usuario> findByRol(Rol rol);

    List<Usuario> findByEstado(EstadoUsuario estado);

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE Usuario u SET u.intentosFallidos = :intentos, " +
           "u.ultimoIntentoFallido = :ultimoIntento, " +
           "u.estado = :estado, " +
           "u.fechaBloqueo = :fechaBloqueo " +
           "WHERE u.id = :id")
    void actualizarIntentosFallidos(
            @Param("id") Long id,
            @Param("intentos") Integer intentos,
            @Param("ultimoIntento") LocalDateTime ultimoIntento,
            @Param("estado") EstadoUsuario estado,
            @Param("fechaBloqueo") LocalDateTime fechaBloqueo
    );

    @Modifying
    @Query("UPDATE Usuario u SET u.intentosFallidos = 0, " +
           "u.ultimoIntentoFallido = NULL, " +
           "u.fechaBloqueo = NULL " +
           "WHERE u.id = :id")
    void resetearIntentos(@Param("id") Long id);
}
