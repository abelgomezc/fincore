package com.fincore.customer.repository;

import com.fincore.customer.entity.Cliente;
import com.fincore.customer.enums.EstadoCliente;
import com.fincore.customer.enums.TipoCliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de clientes.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {

    Optional<Cliente> findByEmail(String email);

    List<Cliente> findByTipoCliente(TipoCliente tipoCliente);

    List<Cliente> findByEstado(EstadoCliente estado);

    boolean existsByEmail(String email);

    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(CONCAT(c.primerNombre, ' ', COALESCE(c.segundoNombre, ''), ' ', " +
           "c.primerApellido, ' ', COALESCE(c.segundoApellido, ''))) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    Page<Cliente> findByNombreContainingIgnoreCase(@Param("nombre") String nombre, Pageable pageable);
}
