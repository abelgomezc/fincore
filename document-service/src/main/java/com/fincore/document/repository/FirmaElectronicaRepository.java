package com.fincore.document.repository;

import com.fincore.document.entity.FirmaElectronica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de firmas electrónicas.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface FirmaElectronicaRepository extends JpaRepository<FirmaElectronica, Long> {

    List<FirmaElectronica> findByIdDocumentoOrderByFechaFirmaDesc(Long idDocumento);

    List<FirmaElectronica> findByIdFirmanteOrderByFechaFirmaDesc(String idFirmante);
}
