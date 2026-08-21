package com.fincore.document.service;

import com.fincore.document.dto.request.CrearPlantillaRequest;
import com.fincore.document.dto.request.EnviarAFirmarRequest;
import com.fincore.document.dto.request.GenerarDocumentoRequest;
import com.fincore.document.dto.response.DocumentoResponse;
import com.fincore.document.dto.response.FirmaElectronicaResponse;
import com.fincore.document.dto.response.PlantillaResponse;

import java.util.List;

/**
 * Servicio de gestión documental.
 *
 * Gestiona:
 * - Plantillas de documentos
 * - Generación de documentos
 * - Firma electrónica
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface DocumentoService {

    PlantillaResponse crearPlantilla(CrearPlantillaRequest request);

    List<PlantillaResponse> listarPlantillas();

    PlantillaResponse obtenerPlantilla(Long id);

    DocumentoResponse generarDocumento(GenerarDocumentoRequest request);

    DocumentoResponse obtenerDocumento(Long id);

    List<DocumentoResponse> consultarDocumentos(String entidad, String idEntidad);

    FirmaElectronicaResponse enviarAFirmar(EnviarAFirmarRequest request);

    FirmaElectronicaResponse finalizarFirma(Long idFirma, String estado, String motivoRechazo, String ipFirmante);
}
