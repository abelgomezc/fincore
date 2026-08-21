package com.fincore.document.repository;

import com.fincore.document.entity.DocumentoGenerado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de documentos generados.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface DocumentoGeneradoRepository extends JpaRepository<DocumentoGenerado, Long> {

    List<DocumentoGenerado> findByEntidadAndIdEntidadOrderByFechaGeneracionDesc(String entidad, String idEntidad);

    List<DocumentoGenerado> findByEntidadAndIdEntidadAndTipoDocumentoOrderByFechaGeneracionDesc(
            String entidad, String idEntidad, com.fincore.document.enums.TipoDocumento tipoDocumento);
}
