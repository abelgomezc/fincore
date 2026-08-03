package com.fincore.fraud.repository;

import com.fincore.fraud.entity.ListaNegra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de lista negra.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface ListaNegraRepository extends JpaRepository<ListaNegra, Long> {
    boolean existsByTipoAndValorAndEsActivoTrue(String tipo, String valor);
}
