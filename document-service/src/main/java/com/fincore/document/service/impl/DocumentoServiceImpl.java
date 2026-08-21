package com.fincore.document.service.impl;

import com.fincore.document.dto.request.CrearPlantillaRequest;
import com.fincore.document.dto.request.EnviarAFirmarRequest;
import com.fincore.document.dto.request.GenerarDocumentoRequest;
import com.fincore.document.dto.request.FinalizarFirmaRequest;
import com.fincore.document.dto.response.DocumentoResponse;
import com.fincore.document.dto.response.FirmaElectronicaResponse;
import com.fincore.document.dto.response.PlantillaResponse;
import com.fincore.document.entity.DocumentoGenerado;
import com.fincore.document.entity.DocumentoPlantilla;
import com.fincore.document.entity.FirmaElectronica;
import com.fincore.document.enums.EstadoDocumento;
import com.fincore.document.enums.EstadoFirma;
import com.fincore.document.enums.TipoDocumento;
import com.fincore.document.exception.DocumentoNoEncontradoException;
import com.fincore.document.exception.PlantillaNoEncontradaException;
import com.fincore.document.kafka.DocumentoEventProducer;
import com.fincore.document.repository.DocumentoGeneradoRepository;
import com.fincore.document.repository.DocumentoPlantillaRepository;
import com.fincore.document.repository.FirmaElectronicaRepository;
import com.fincore.document.service.DocumentoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión documental.
 *
 * Simula generación de PDF y firma electrónica con proveedores externos.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Service
@Slf4j
@Transactional
public class DocumentoServiceImpl implements DocumentoService {

    private final DocumentoPlantillaRepository plantillaRepository;
    private final DocumentoGeneradoRepository documentoGeneradoRepository;
    private final FirmaElectronicaRepository firmaElectronicaRepository;
    private final DocumentoEventProducer eventProducer;

    public DocumentoServiceImpl(DocumentoPlantillaRepository plantillaRepository,
                                DocumentoGeneradoRepository documentoGeneradoRepository,
                                FirmaElectronicaRepository firmaElectronicaRepository,
                                DocumentoEventProducer eventProducer) {
        this.plantillaRepository = plantillaRepository;
        this.documentoGeneradoRepository = documentoGeneradoRepository;
        this.firmaElectronicaRepository = firmaElectronicaRepository;
        this.eventProducer = eventProducer;
    }

    @Override
    public PlantillaResponse crearPlantilla(CrearPlantillaRequest request) {
        log.info("Creando plantilla: {} - tipo: {}", request.nombre(), request.tipoDocumento());

        DocumentoPlantilla plantilla = new DocumentoPlantilla();
        plantilla.setTipoDocumento(request.tipoDocumento());
        plantilla.setNombre(request.nombre());
        plantilla.setDescripcion(request.descripcion());
        plantilla.setContenidoHtml(request.contenidoHtml());
        plantilla.setVersion(1);
        plantilla.setActivo(true);

        plantillaRepository.save(plantilla);
        return toPlantillaResponse(plantilla);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantillaResponse> listarPlantillas() {
        return plantillaRepository.findByActivoTrue()
                .stream()
                .map(this::toPlantillaResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PlantillaResponse obtenerPlantilla(Long id) {
        DocumentoPlantilla plantilla = plantillaRepository.findById(id)
                .orElseThrow(() -> new PlantillaNoEncontradaException("Plantilla no encontrada: " + id));
        return toPlantillaResponse(plantilla);
    }

    @Override
    public DocumentoResponse generarDocumento(GenerarDocumentoRequest request) {
        log.info("Generando documento: {} - plantilla: {}", request.nombreArchivo(), request.idPlantilla());

        DocumentoPlantilla plantilla = plantillaRepository.findById(request.idPlantilla())
                .orElseThrow(() -> new PlantillaNoEncontradaException("Plantilla no encontrada: " + request.idPlantilla()));

        DocumentoGenerado documento = new DocumentoGenerado();
        documento.setEntidad(request.entidad());
        documento.setIdEntidad(request.idEntidad());
        documento.setTipoDocumento(request.tipoDocumento());
        documento.setEstado(EstadoDocumento.GENERADO);
        documento.setNombreArchivo(request.nombreArchivo());
        documento.setUrlArchivo("/docs/generated/" + UUID.randomUUID() + ".pdf");
        documento.setHashArchivo(UUID.randomUUID().toString());
        documento.setIdPlantilla(plantilla.getId());
        documento.setContenidoHtml(plantilla.getContenidoHtml());
        documento.setCreadoPor(request.creadoPor() != null ? request.creadoPor() : "system");

        documentoGeneradoRepository.save(documento);
        eventProducer.publicarDocumentoGenerado(documento.getId(), request.entidad(), request.idEntidad());

        return toDocumentoResponse(documento);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentoResponse obtenerDocumento(Long id) {
        DocumentoGenerado documento = documentoGeneradoRepository.findById(id)
                .orElseThrow(() -> new DocumentoNoEncontradoException("Documento no encontrado: " + id));
        return toDocumentoResponse(documento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoResponse> consultarDocumentos(String entidad, String idEntidad) {
        return documentoGeneradoRepository.findByEntidadAndIdEntidadOrderByFechaGeneracionDesc(entidad, idEntidad)
                .stream()
                .map(this::toDocumentoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FirmaElectronicaResponse enviarAFirmar(EnviarAFirmarRequest request) {
        log.info("Enviando a firmar documento: {} - firmante: {}", request.idDocumento(), request.idFirmante());

        DocumentoGenerado documento = documentoGeneradoRepository.findById(request.idDocumento())
                .orElseThrow(() -> new DocumentoNoEncontradoException("Documento no encontrado: " + request.idDocumento()));

        FirmaElectronica firma = new FirmaElectronica();
        firma.setIdDocumento(documento.getId());
        firma.setIdFirmante(request.idFirmante());
        firma.setNombreFirmante(request.nombreFirmante());
        firma.setEmailFirmante(request.emailFirmante());
        firma.setEstado(EstadoFirma.ENVIADO);
        firma.setProveedor(request.proveedor() != null ? request.proveedor() : "mock-provider");
        firma.setIdTransaccionProveedor(UUID.randomUUID().toString());
        firma.setFechaEnvio(LocalDateTime.now());
        firma.setIpFirmante(request.ipFirmante());

        firmaElectronicaRepository.save(firma);

        documento.setEstado(EstadoDocumento.ENVIADO);
        documentoGeneradoRepository.save(documento);

        eventProducer.publicarDocumentoEnviadoAFirmar(documento.getId(), request.idFirmante());

        return toFirmaResponse(firma);
    }

    @Override
    public FirmaElectronicaResponse finalizarFirma(Long idFirma, String estado, String motivoRechazo, String ipFirmante) {
        log.info("Finalizando firma: {} - estado: {}", idFirma, estado);

        FirmaElectronica firma = firmaElectronicaRepository.findById(idFirma)
                .orElseThrow(() -> new DocumentoNoEncontradoException("Firma no encontrada: " + idFirma));

        firma.setEstado(EstadoFirma.valueOf(estado));
        firma.setFechaFirma(LocalDateTime.now());
        firma.setIpFirmante(ipFirmante);
        firma.setMotivoRechazo(motivoRechazo);

        firmaElectronicaRepository.save(firma);

        DocumentoGenerado documento = documentoGeneradoRepository.findById(firma.getIdDocumento())
                .orElseThrow(() -> new DocumentoNoEncontradoException("Documento no encontrado: " + firma.getIdDocumento()));

        if (estado.equals("FIRMADO")) {
            documento.setEstado(EstadoDocumento.FIRMADO);
            documento.setFechaFirma(LocalDateTime.now());
        } else if (estado.equals("RECHAZADO")) {
            documento.setEstado(EstadoDocumento.RECHAZADO);
        }
        documentoGeneradoRepository.save(documento);

        eventProducer.publicarFirmaFinalizada(documento.getId(), estado);

        return toFirmaResponse(firma);
    }

    private PlantillaResponse toPlantillaResponse(DocumentoPlantilla p) {
        PlantillaResponse r = new PlantillaResponse();
        r.setId(p.getId());
        r.setTipoDocumento(p.getTipoDocumento().name());
        r.setNombre(p.getNombre());
        r.setDescripcion(p.getDescripcion());
        r.setContenidoHtml(p.getContenidoHtml());
        r.setVersion(p.getVersion());
        r.setActivo(p.isActivo());
        r.setFechaCreacion(p.getFechaCreacion());
        r.setFechaActualizacion(p.getFechaActualizacion());
        return r;
    }

    private DocumentoResponse toDocumentoResponse(DocumentoGenerado d) {
        DocumentoResponse r = new DocumentoResponse();
        r.setId(d.getId());
        r.setEntidad(d.getEntidad());
        r.setIdEntidad(d.getIdEntidad());
        r.setTipoDocumento(d.getTipoDocumento().name());
        r.setEstado(d.getEstado().name());
        r.setNombreArchivo(d.getNombreArchivo());
        r.setUrlArchivo(d.getUrlArchivo());
        r.setHashArchivo(d.getHashArchivo());
        r.setIdPlantilla(d.getIdPlantilla());
        r.setFechaGeneracion(d.getFechaGeneracion());
        r.setFechaFirma(d.getFechaFirma());
        r.setFechaVencimiento(d.getFechaVencimiento());
        return r;
    }

    private FirmaElectronicaResponse toFirmaResponse(FirmaElectronica f) {
        FirmaElectronicaResponse r = new FirmaElectronicaResponse();
        r.setId(f.getId());
        r.setIdDocumento(f.getIdDocumento());
        r.setIdFirmante(f.getIdFirmante());
        r.setNombreFirmante(f.getNombreFirmante());
        r.setEmailFirmante(f.getEmailFirmante());
        r.setEstado(f.getEstado().name());
        r.setProveedor(f.getProveedor());
        r.setIdTransaccionProveedor(f.getIdTransaccionProveedor());
        r.setFechaEnvio(f.getFechaEnvio());
        r.setFechaFirma(f.getFechaFirma());
        r.setIpFirmante(f.getIpFirmante());
        r.setHuellaDigital(f.getHuellaDigital());
        r.setCertificadoSerial(f.getCertificadoSerial());
        r.setMotivoRechazo(f.getMotivoRechazo());
        return r;
    }
}
