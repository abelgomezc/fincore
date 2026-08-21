package com.fincore.document.controller;

import com.fincore.document.dto.request.CrearPlantillaRequest;
import com.fincore.document.dto.request.EnviarAFirmarRequest;
import com.fincore.document.dto.request.GenerarDocumentoRequest;
import com.fincore.document.dto.request.FinalizarFirmaRequest;
import com.fincore.document.dto.response.DocumentoResponse;
import com.fincore.document.dto.response.FirmaElectronicaResponse;
import com.fincore.document.dto.response.PlantillaResponse;
import com.fincore.document.service.DocumentoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de gestión documental.
 *
 * Endpoints:
 * - POST /api/documentos/plantillas
 * - GET /api/documentos/plantillas
 * - GET /api/documentos/plantillas/{id}
 * - POST /api/documentos/generar
 * - GET /api/documentos/{id}
 * - GET /api/documentos?entidad=&idEntidad=
 * - POST /api/documentos/{id}/enviar-firmar
 * - PUT /api/documentos/firmas/{idFirma}/finalizar
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@RestController
@RequestMapping("/api/documentos")
@Slf4j
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    @PostMapping("/plantillas")
    public ResponseEntity<PlantillaResponse> crearPlantilla(@Valid @RequestBody CrearPlantillaRequest request) {
        PlantillaResponse response = documentoService.crearPlantilla(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/plantillas")
    public ResponseEntity<List<PlantillaResponse>> listarPlantillas() {
        List<PlantillaResponse> response = documentoService.listarPlantillas();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plantillas/{id}")
    public ResponseEntity<PlantillaResponse> obtenerPlantilla(@PathVariable Long id) {
        PlantillaResponse response = documentoService.obtenerPlantilla(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generar")
    public ResponseEntity<DocumentoResponse> generarDocumento(@Valid @RequestBody GenerarDocumentoRequest request) {
        DocumentoResponse response = documentoService.generarDocumento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentoResponse> obtenerDocumento(@PathVariable Long id) {
        DocumentoResponse response = documentoService.obtenerDocumento(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DocumentoResponse>> consultarDocumentos(
            @RequestParam String entidad,
            @RequestParam String idEntidad) {
        List<DocumentoResponse> response = documentoService.consultarDocumentos(entidad, idEntidad);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/enviar-firmar")
    public ResponseEntity<FirmaElectronicaResponse> enviarAFirmar(
            @PathVariable Long id,
            @Valid @RequestBody EnviarAFirmarRequest request) {
        FirmaElectronicaResponse response = documentoService.enviarAFirmar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/firmas/{idFirma}/finalizar")
    public ResponseEntity<FirmaElectronicaResponse> finalizarFirma(
            @PathVariable Long idFirma,
            @Valid @RequestBody FinalizarFirmaRequest request) {
        FirmaElectronicaResponse response = documentoService.finalizarFirma(
                idFirma, request.estado(), request.motivoRechazo(), request.ipFirmante());
        return ResponseEntity.ok(response);
    }
}
