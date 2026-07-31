package com.fincore.auth.repository;

import com.fincore.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de refresh tokens.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByIdUsuarioAndEsRevocadoFalse(Long idUsuario);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.esRevocado = true, rt.fechaRevocacion = :fecha " +
           "WHERE rt.idUsuario = :idUsuario AND rt.esRevocado = false")
    void revocarTokensActivos(@Param("idUsuario") Long idUsuario, @Param("fecha") LocalDateTime fecha);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.fechaExpiracion < :ahora")
    void eliminarTokensExpirados(@Param("ahora") LocalDateTime ahora);
}
