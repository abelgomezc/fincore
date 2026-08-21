package com.fincore.loan.service.impl;

import com.fincore.loan.dto.request.AprobarRechazarRequest;
import com.fincore.loan.dto.request.CrearSolicitudPrestamoRequest;
import com.fincore.loan.dto.request.EvaluarRiesgoRequest;
import com.fincore.loan.dto.response.EvaluacionRiesgoResponse;
import com.fincore.loan.dto.response.SolicitudPrestamoResponse;
import com.fincore.loan.entity.EvaluacionRiesgo;
import com.fincore.loan.entity.HistorialSolicitudPrestamo;
import com.fincore.loan.entity.SolicitudPrestamo;
import com.fincore.loan.enums.EstadoEvaluacionRiesgo;
import com.fincore.loan.enums.EstadoSolicitudPrestamo;
import com.fincore.loan.enums.TipoPrestamo;
import com.fincore.loan.exception.SolicitudPrestamoNoEncontradaException;
import com.fincore.loan.kafka.PrestamoEventProducer;
import com.fincore.loan.repository.EvaluacionRiesgoRepository;
import com.fincore.loan.repository.HistorialSolicitudPrestamoRepository;
import com.fincore.loan.repository.SolicitudPrestamoRepository;
import com.fincore.loan.service.PrestamoOrchestrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementación del servicio de orquestación de préstamos.
 *
 * Orquesta el flujo completo de originación de préstamos en línea.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Service
@Slf4j
@Transactional
public class PrestamoOrchestrationServiceImpl implements PrestamoOrchestrationService {

    private final SolicitudPrestamoRepository solicitudRepository;
    private final EvaluacionRiesgoRepository evaluacionRiesgoRepository;
    private final HistorialSolicitudPrestamoRepository historialRepository;
    private final PrestamoEventProducer eventProducer;

    public PrestamoOrchestrationServiceImpl(SolicitudPrestamoRepository solicitudRepository,
                                            EvaluacionRiesgoRepository evaluacionRiesgoRepository,
                                            HistorialSolicitudPrestamoRepository historialRepository,
                                            PrestamoEventProducer eventProducer) {
        this.solicitudRepository = solicitudRepository;
        this.evaluacionRiesgoRepository = evaluacionRiesgoRepository;
        this.historialRepository = historialRepository;
        this.eventProducer = eventProducer;
    }

    @Override
    public SolicitudPrestamoResponse crearSolicitud(CrearSolicitudPrestamoRequest request) {
        log.info("Creando solicitud de préstamo para cliente: {} - tipo: {}", request.idCliente(), request.tipoPrestamo());

        SolicitudPrestamo solicitud = new SolicitudPrestamo();
        solicitud.setIdCliente(request.idCliente());
        solicitud.setNumeroSolicitud(generarNumeroSolicitud());
        solicitud.setTipoPrestamo(request.tipoPrestamo());
        solicitud.setEstado(EstadoSolicitudPrestamo.ENVIADA);
        solicitud.setMontoSolicitado(request.montoSolicitado());
        solicitud.setPlazoMeses(request.plazoMeses());
        solicitud.setTasaInteresAnual(calcularTasaInteres(request.tipoPrestamo(), request.montoSolicitado(), request.plazoMeses()));
        solicitud.setCreadoPor(request.creadoPor() != null ? request.creadoPor() : "system");

        solicitudRepository.save(solicitud);
        registrarHistorial(solicitud, null, EstadoSolicitudPrestamo.ENVIADA, "Solicitud creada");

        eventProducer.publicarSolicitudCreada(solicitud.getId(), solicitud.getNumeroSolicitud());

        return toResponse(solicitud);
    }

    @Override
    public SolicitudPrestamoResponse iniciarFlujo(Long idSolicitud) {
        SolicitudPrestamo solicitud = obtenerEntidadSolicitud(idSolicitud);
        solicitud.setEstado(EstadoSolicitudPrestamo.IDENTIDAD_VERIFICADA);
        solicitudRepository.save(solicitud);
        registrarHistorial(solicitud, EstadoSolicitudPrestamo.ENVIADA, EstadoSolicitudPrestamo.IDENTIDAD_VERIFICADA, "Flujo iniciado");
        eventProducer.publicarFlujoIniciado(idSolicitud);
        return toResponse(solicitud);
    }

    @Override
    public SolicitudPrestamoResponse validarIdentidad(Long idSolicitud) {
        SolicitudPrestamo solicitud = obtenerEntidadSolicitud(idSolicitud);
        solicitud.setEstado(EstadoSolicitudPrestamo.IDENTIDAD_VERIFICADA);
        solicitudRepository.save(solicitud);
        registrarHistorial(solicitud, EstadoSolicitudPrestamo.ENVIADA, EstadoSolicitudPrestamo.IDENTIDAD_VERIFICADA, "Identidad validada");
        eventProducer.publicarIdentidadValidada(idSolicitud);
        return toResponse(solicitud);
    }

    @Override
    public SolicitudPrestamoResponse consultarBuro(Long idSolicitud) {
        SolicitudPrestamo solicitud = obtenerEntidadSolicitud(idSolicitud);
        solicitud.setScoreBuro(750);
        solicitud.setEstado(EstadoSolicitudPrestamo.BURO_CONSULTADO);
        solicitudRepository.save(solicitud);
        registrarHistorial(solicitud, EstadoSolicitudPrestamo.IDENTIDAD_VERIFICADA, EstadoSolicitudPrestamo.BURO_CONSULTADO, "Buró consultado - Score: 750");
        eventProducer.publicarBuroConsultado(idSolicitud, 750);
        return toResponse(solicitud);
    }

    @Override
    public EvaluacionRiesgoResponse evaluarRiesgo(EvaluarRiesgoRequest request) {
        SolicitudPrestamo solicitud = obtenerEntidadSolicitud(request.idSolicitud());
        solicitud.setEstado(EstadoSolicitudPrestamo.EVALUANDO_RIESGO);
        solicitudRepository.save(solicitud);

        EvaluacionRiesgo evaluacion = new EvaluacionRiesgo();
        evaluacion.setIdSolicitud(solicitud.getId());
        evaluacion.setEstado(EstadoEvaluacionRiesgo.EN_PROCESO);
        evaluacion.setScoreBuro(request.scoreBuro() != null ? request.scoreBuro() : solicitud.getScoreBuro());
        evaluacion.setScoreInterno(request.scoreInterno() != null ? request.scoreInterno() : 80);
        evaluacion.setDetalle(request.detalle());

        int scoreFinal = (evaluacion.getScoreBuro() + evaluacion.getScoreInterno()) / 2;
        evaluacion.setScoreFinal(scoreFinal);

        if (scoreFinal >= 700 && solicitud.getMontoSolicitado().compareTo(new BigDecimal("50000")) <= 0) {
            evaluacion.setEstado(EstadoEvaluacionRiesgo.APROBADO);
            evaluacion.setMontoAprobado(solicitud.getMontoSolicitado());
            evaluacion.setTasaInteresAprobada(solicitud.getTasaInteresAnual());
            evaluacion.setPlazoAprobadoMeses(solicitud.getPlazoMeses());
        } else if (scoreFinal >= 600) {
            evaluacion.setEstado(EstadoEvaluacionRiesgo.REQUIERE_REVISION_MANUAL);
            evaluacion.setMontoAprobado(solicitud.getMontoSolicitado().multiply(new BigDecimal("0.8")));
            evaluacion.setTasaInteresAprobada(solicitud.getTasaInteresAnual().add(new BigDecimal("2")));
            evaluacion.setPlazoAprobadoMeses(solicitud.getPlazoMeses());
        } else {
            evaluacion.setEstado(EstadoEvaluacionRiesgo.RECHAZADO);
        }

        evaluacionRiesgoRepository.save(evaluacion);

        solicitud.setEvaluacionRiesgo(evaluacion.getEstado());
        solicitud.setScoreRiesgo(scoreFinal);
        if (evaluacion.getMontoAprobado() != null) {
            solicitud.setMontoAprobado(evaluacion.getMontoAprobado());
        }
        if (evaluacion.getTasaInteresAprobada() != null) {
            solicitud.setTasaInteresAnual(evaluacion.getTasaInteresAprobada());
        }
        if (evaluacion.getPlazoAprobadoMeses() != null) {
            solicitud.setPlazoMeses(evaluacion.getPlazoAprobadoMeses());
        }

        if (evaluacion.getEstado() == EstadoEvaluacionRiesgo.APROBADO) {
            solicitud.setEstado(EstadoSolicitudPrestamo.APROBADA);
            solicitud.setFechaAprobacion(LocalDateTime.now());
        } else if (evaluacion.getEstado() == EstadoEvaluacionRiesgo.RECHAZADO) {
            solicitud.setEstado(EstadoSolicitudPrestamo.RECHAZADA);
            solicitud.setFechaRechazo(LocalDateTime.now());
            solicitud.setMotivoRechazo("Score de riesgo insuficiente: " + scoreFinal);
        } else {
            solicitud.setEstado(EstadoSolicitudPrestamo.EVALUANDO_RIESGO);
        }

        solicitudRepository.save(solicitud);
        registrarHistorial(solicitud, EstadoSolicitudPrestamo.BURO_CONSULTADO, solicitud.getEstado(), "Riesgo evaluado - Score final: " + scoreFinal);

        eventProducer.publicarRiesgoEvaluado(request.idSolicitud(), evaluacion.getEstado().name());

        return toEvaluacionResponse(evaluacion);
    }

    @Override
    public SolicitudPrestamoResponse aprobarSolicitud(AprobarRechazarRequest request) {
        SolicitudPrestamo solicitud = obtenerEntidadSolicitud(request.idSolicitud());
        solicitud.setEstado(EstadoSolicitudPrestamo.APROBADA);
        solicitud.setFechaAprobacion(LocalDateTime.now());
        solicitudRepository.save(solicitud);
        registrarHistorial(solicitud, EstadoSolicitudPrestamo.EVALUANDO_RIESGO, EstadoSolicitudPrestamo.APROBADA, request.motivo() != null ? request.motivo() : "Aprobado manualmente");
        eventProducer.publicarSolicitudAprobada(request.idSolicitud());
        return toResponse(solicitud);
    }

    @Override
    public SolicitudPrestamoResponse rechazarSolicitud(AprobarRechazarRequest request) {
        SolicitudPrestamo solicitud = obtenerEntidadSolicitud(request.idSolicitud());
        solicitud.setEstado(EstadoSolicitudPrestamo.RECHAZADA);
        solicitud.setFechaRechazo(LocalDateTime.now());
        solicitud.setMotivoRechazo(request.motivo());
        solicitudRepository.save(solicitud);
        registrarHistorial(solicitud, EstadoSolicitudPrestamo.EVALUANDO_RIESGO, EstadoSolicitudPrestamo.RECHAZADA, request.motivo() != null ? request.motivo() : "Rechazado");
        eventProducer.publicarSolicitudRechazada(request.idSolicitud());
        return toResponse(solicitud);
    }

    @Override
    public SolicitudPrestamoResponse generarContrato(Long idSolicitud) {
        SolicitudPrestamo solicitud = obtenerEntidadSolicitud(idSolicitud);
        solicitud.setEstado(EstadoSolicitudPrestamo.CONTRATO_GENERADO);
        solicitudRepository.save(solicitud);
        registrarHistorial(solicitud, EstadoSolicitudPrestamo.APROBADA, EstadoSolicitudPrestamo.CONTRATO_GENERADO, "Contrato generado");
        eventProducer.publicarContratoGenerado(idSolicitud);
        return toResponse(solicitud);
    }

    @Override
    public SolicitudPrestamoResponse enviarContratoAFirmar(Long idSolicitud) {
        SolicitudPrestamo solicitud = obtenerEntidadSolicitud(idSolicitud);
        solicitud.setEstado(EstadoSolicitudPrestamo.CONTRATO_FIRMADO);
        solicitudRepository.save(solicitud);
        registrarHistorial(solicitud, EstadoSolicitudPrestamo.CONTRATO_GENERADO, EstadoSolicitudPrestamo.CONTRATO_FIRMADO, "Contrato enviado a firma");
        eventProducer.publicarContratoEnviadoAFirmar(idSolicitud);
        return toResponse(solicitud);
    }

    @Override
    public SolicitudPrestamoResponse desembolsar(Long idSolicitud) {
        SolicitudPrestamo solicitud = obtenerEntidadSolicitud(idSolicitud);
        solicitud.setEstado(EstadoSolicitudPrestamo.DESEMBOLSADA);
        solicitud.setFechaDesembolso(LocalDateTime.now());
        solicitudRepository.save(solicitud);
        registrarHistorial(solicitud, EstadoSolicitudPrestamo.CONTRATO_FIRMADO, EstadoSolicitudPrestamo.DESEMBOLSADA, "Desembolsado");
        eventProducer.publicarPrestamoDesembolsado(idSolicitud, solicitud.getMontoAprobado());
        return toResponse(solicitud);
    }

    @Override
    @Transactional(readOnly = true)
    public SolicitudPrestamoResponse obtenerSolicitud(Long idSolicitud) {
        SolicitudPrestamo solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new SolicitudPrestamoNoEncontradaException("Solicitud no encontrada: " + idSolicitud));
        return toResponse(solicitud);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudPrestamoResponse> listarSolicitudes() {
        return solicitudRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private SolicitudPrestamo obtenerEntidadSolicitud(Long idSolicitud) {
        return solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new SolicitudPrestamoNoEncontradaException("Solicitud no encontrada: " + idSolicitud));
    }

    private void registrarHistorial(SolicitudPrestamo solicitud, EstadoSolicitudPrestamo anterior, EstadoSolicitudPrestamo nuevo, String motivo) {
        HistorialSolicitudPrestamo historial = new HistorialSolicitudPrestamo();
        historial.setIdSolicitud(solicitud.getId());
        historial.setEstadoAnterior(anterior);
        historial.setEstadoNuevo(nuevo);
        historial.setMotivo(motivo);
        historialRepository.save(historial);
    }

    private String generarNumeroSolicitud() {
        return "SOL-" + LocalDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BigDecimal calcularTasaInteres(TipoPrestamo tipo, BigDecimal monto, Integer plazo) {
        BigDecimal tasaBase = switch (tipo) {
            case PERSONAL -> new BigDecimal("18.0");
            case HIPOTECARIO -> new BigDecimal("12.0");
            case AUTOMOTOR -> new BigDecimal("15.0");
            case COMERCIAL -> new BigDecimal("20.0");
            case MICROCREDITO -> new BigDecimal("25.0");
        };
        if (monto.compareTo(new BigDecimal("100000")) > 0) {
            tasaBase = tasaBase.add(new BigDecimal("2"));
        }
        if (plazo > 60) {
            tasaBase = tasaBase.add(new BigDecimal("1"));
        }
        return tasaBase;
    }

    private SolicitudPrestamoResponse toResponse(SolicitudPrestamo s) {
        SolicitudPrestamoResponse r = new SolicitudPrestamoResponse();
        r.setId(s.getId());
        r.setIdCliente(s.getIdCliente());
        r.setNumeroSolicitud(s.getNumeroSolicitud());
        r.setTipoPrestamo(s.getTipoPrestamo().name());
        r.setEstado(s.getEstado().name());
        r.setMontoSolicitado(s.getMontoSolicitado());
        r.setPlazoMeses(s.getPlazoMeses());
        r.setTasaInteresAnual(s.getTasaInteresAnual());
        r.setMontoAprobado(s.getMontoAprobado());
        r.setScoreBuro(s.getScoreBuro());
        r.setScoreRiesgo(s.getScoreRiesgo());
        r.setEvaluacionRiesgo(s.getEvaluacionRiesgo() != null ? s.getEvaluacionRiesgo().name() : null);
        r.setMotivoRechazo(s.getMotivoRechazo());
        r.setIdDocumentoContrato(s.getIdDocumentoContrato());
        r.setIdDocumentoPagare(s.getIdDocumentoPagare());
        r.setFechaSolicitud(s.getFechaSolicitud());
        r.setFechaAprobacion(s.getFechaAprobacion());
        r.setFechaRechazo(s.getFechaRechazo());
        r.setFechaDesembolso(s.getFechaDesembolso());
        return r;
    }

    private EvaluacionRiesgoResponse toEvaluacionResponse(EvaluacionRiesgo e) {
        EvaluacionRiesgoResponse r = new EvaluacionRiesgoResponse();
        r.setId(e.getId());
        r.setIdSolicitud(e.getIdSolicitud());
        r.setEstado(e.getEstado().name());
        r.setScoreBuro(e.getScoreBuro());
        r.setScoreInterno(e.getScoreInterno());
        r.setScoreFinal(e.getScoreFinal());
        r.setMontoAprobado(e.getMontoAprobado());
        r.setTasaInteresAprobada(e.getTasaInteresAprobada());
        r.setPlazoAprobadoMeses(e.getPlazoAprobadoMeses());
        r.setDetalle(e.getDetalle());
        r.setReglasAplicadas(e.getReglasAplicadas());
        r.setFechaEvaluacion(e.getFechaEvaluacion());
        r.setEvaluadoPor(e.getEvaluadoPor());
        return r;
    }
}
