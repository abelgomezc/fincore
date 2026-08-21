package com.fincore.document.repository;

import com.fincore.document.entity.DocumentoPlantilla;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de plantillas de documentos.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface DocumentoPlantillaRepository extends JpaRepository<DocumentoPlantilla, Long> {

    List<DocumentoPlantilla> findByActivoTrue();

    Optional<DocumentoPlantilla> findByTipoDocumentoAndActivoTrueAndVersion(
            com.fincore.document.enums.TipoDocumento tipoDocumento, Integer version);

    List<DocumentoPlantilla> findByTipoDocumento(com.fincore.document.enums.TipoDocumento tipoDocumento);
}
