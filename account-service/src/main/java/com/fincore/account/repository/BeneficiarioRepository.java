package com.fincore.account.repository;

import com.fincore.account.entity.Beneficiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de beneficiarios frecuentes.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Repository
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {

    List<Beneficiario> findByIdClienteAndActivoTrue(Long idCliente);

    List<Beneficiario> findByIdCliente(Long idCliente);

    boolean existsByIdClienteAndCuentaBeneficiario_Id(Long idCliente, Long idCuentaBeneficiario);
}
