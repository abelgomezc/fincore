package com.fincore.transfer.service;

import com.fincore.transfer.dto.request.CrearTransferenciaRequest;
import com.fincore.transfer.dto.response.TransferenciaResponse;
import com.fincore.transfer.dto.response.TransferenciaResponse.EstadoTransferenciaDto;
import com.fincore.transfer.entity.Transferencia;
import com.fincore.transfer.entity.TransferenciaEstado;
import com.fincore.transfer.enums.EstadoTransferencia;
import com.fincore.transfer.repository.TransferenciaEstadoRepository;
import com.fincore.transfer.repository.TransferenciaRepository;
import com.fincore.transfer.saga.SagaOrchestrator;
import com.fincore.transfer.saga.SagaResult;
import com.fincore.transfer.websocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio de transferencias.
 *
 * Orquesta la creación y consulta de transferencias,
 * delegando el procesamiento complejo al SagaOrchestrator.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Service
@Slf4j
@Transactional
public class TransferenciaServiceImpl implements TransferenciaService {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;
    private final NumeradorService numeradorService;
    private final SagaOrchestrator sagaOrchestrator;
    private final WebSocketService webSocketService;

    public TransferenciaServiceImpl(TransferenciaRepository transferenciaRepository,
                                    TransferenciaEstadoRepository estadoRepository,
                                    NumeradorService numeradorService,
                                    SagaOrchestrator sagaOrchestrator,
                                    WebSocketService webSocketService) {
        this.transferenciaRepository = transferenciaRepository;
        this.estadoRepository = estadoRepository;
        this.numeradorService = numeradorService;
        this.sagaOrchestrator = sagaOrchestrator;
        this.webSocketService = webSocketService;
    }

    @Override
    @Transactional
    public TransferenciaResponse crearTransferencia(CrearTransferenciaRequest request, String idUsuario, String ipOrigen) {
        log.info("Creando transferencia: origen={}, destino={}, monto={}",
                request.getNumeroCuentaOrigen(), request.getNumeroCuentaDestino(), request.getMonto());

        // Generar número y trace ID
        String numeroTransferencia = numeradorService.generarNumeroTransferencia();
        String traceId = numeradorService.generarTraceId();

        // Crear entidad
        Transferencia transferencia = new Transferencia();
        transferencia.setNumeroTransferencia(numeroTransferencia);
        transferencia.setIdCuentaOrigen(request.getIdCuentaOrigen());
        transferencia.setNumeroCuentaOrigen(request.getNumeroCuentaOrigen());
        transferencia.setIdCuentaDestino(request.getIdCuentaDestino());
        transferencia.setNumeroCuentaDestino(request.getNumeroCuentaDestino());
        transferencia.setNombreBeneficiario(request.getNombreBeneficiario());
        transferencia.setMonto(request.getMonto());
        transferencia.setMoneda(request.getMoneda() != null ? request.getMoneda() : "USD");
        transferencia.setComision(request.getComision() != null ? request.getComision() : java.math.BigDecimal.ZERO);
        transferencia.setConcepto(request.getConcepto());
        transferencia.setEstado(EstadoTransferencia.PENDIENTE);
        transferencia.setPasoSagaActual("INICIO");
        transferencia.setIntentosSaga(0);
        transferencia.setIdUsuario(idUsuario);
        transferencia.setIpOrigen(ipOrigen);
        transferencia.setDispositivo(request.getDispositivo());
        transferencia.setTraceId(traceId);
        transferencia.setFechaIniciada(LocalDateTime.now());

        // Guardar (estado PENDIENTE)
        transferenciaRepository.save(transferencia);

        // Persistir estado inicial
        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(null);
        estado.setEstadoNuevo(EstadoTransferencia.PENDIENTE.name());
        estado.setPasoSaga("INICIO");
        estado.setDescripcion("Transferencia creada por el usuario");
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);

        log.info("Transferencia creada con número: {}", numeroTransferencia);

        // Ejecutar saga de forma asíncrona para no bloquear el API
        Thread.startVirtualThread(() -> {
            try {
                SagaResult result = sagaOrchestrator.ejecutarSaga(transferencia);
                if (result.isExito()) {
                    log.info("Saga completada exitosamente: {}", transferencia.getNumeroTransferencia());
                    // Notificación WebSocket ya se hace en el step de notificación
                } else {
                    log.warn("Saga falló: {} — {}", transferencia.getNumeroTransferencia(), result.getMensaje());
                    webSocketService.notificarError(transferencia, result.getMensaje());
                }
            } catch (Exception e) {
                log.error("Error ejecutando saga para transferencia {}: {}",
                        transferencia.getNumeroTransferencia(), e.getMessage(), e);
                webSocketService.notificarError(transferencia, "Error ejecutando saga: " + e.getMessage());
            }
        });

        // Retornar respuesta inmediata (transferencia en estado PENDIENTE/VALIDANDO)
        return convertToResponse(transferencia);
    }

    @Override
    @Transactional(readOnly = true)
    public TransferenciaResponse obtenerTransferencia(Long id) {
        Transferencia transferencia = transferenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transferencia no encontrada: " + id));

        return convertToResponse(transferencia);
    }

    @Override
    @Transactional(readOnly = true)
    public TransferenciaResponse obtenerTransferenciaPorNumero(String numeroTransferencia) {
        Transferencia transferencia = transferenciaRepository.findByNumeroTransferencia(numeroTransferencia)
                .orElseThrow(() -> new RuntimeException("Transferencia no encontrada: " + numeroTransferencia));

        return convertToResponse(transferencia);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferenciaResponse> listarTransferenciasPorUsuario(String idUsuario, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaIniciada").descending());
        Page<Transferencia> transferencias = transferenciaRepository.findByIdUsuario(idUsuario, pageable);

        return transferencias.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferenciaResponse> listarPorEstado(EstadoTransferencia estado) {
        return transferenciaRepository.findByEstado(estado)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TransferenciaResponse revertirTransferencia(Long id, String motivo) {
        Transferencia transferencia = transferenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transferencia no encontrada: " + id));

        if (transferencia.getEstado() != EstadoTransferencia.COMPLETADA) {
            throw new RuntimeException("Solo se pueden revertir transferencias completadas. Estado actual: "
                    + transferencia.getEstado());
        }

        // Iniciar compensación manual
        Thread.startVirtualThread(() -> {
            webSocketService.notificarError(transferencia, "Transferencia revertida manualmente: " + motivo);
        });

        transferencia.setEstado(EstadoTransferencia.REVERTIDA);
        transferencia.setFechaRevertida(LocalDateTime.now());
        transferencia.setMotivoRechazo(motivo);
        transferenciaRepository.save(transferencia);

        // Guardar estado
        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(EstadoTransferencia.COMPLETADA.name());
        estado.setEstadoNuevo(EstadoTransferencia.REVERTIDA.name());
        estado.setPasoSaga("REVERSION_MANUAL");
        estado.setDescripcion("Reversionada manualmente: " + motivo);
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);

        return convertToResponse(transferencia);
    }

    private TransferenciaResponse convertToResponse(Transferencia t) {
        List<TransferenciaEstado> estados = estadoRepository.findByIdTransferenciaOrderByFechaCambio(t.getId());

        List<EstadoTransferenciaDto> historial = estados.stream()
                .map(e -> {
                    EstadoTransferenciaDto dto = new EstadoTransferenciaDto();
                    dto.setEstadoAnterior(e.getEstadoAnterior());
                    dto.setEstadoNuevo(e.getEstadoNuevo());
                    dto.setPasoSaga(e.getPasoSaga());
                    dto.setDescripcion(e.getDescripcion());
                    dto.setErrorDetalle(e.getErrorDetalle());
                    dto.setFechaCambio(e.getFechaCambio());
                    return dto;
                })
                .collect(Collectors.toList());

        TransferenciaResponse response = new TransferenciaResponse();
        response.setId(t.getId());
        response.setNumeroTransferencia(t.getNumeroTransferencia());
        response.setIdCuentaOrigen(t.getIdCuentaOrigen());
        response.setNumeroCuentaOrigen(t.getNumeroCuentaOrigen());
        response.setIdCuentaDestino(t.getIdCuentaDestino());
        response.setNumeroCuentaDestino(t.getNumeroCuentaDestino());
        response.setNombreBeneficiario(t.getNombreBeneficiario());
        response.setMonto(t.getMonto());
        response.setMoneda(t.getMoneda());
        response.setComision(t.getComision());
        response.setConcepto(t.getConcepto());
        response.setEstado(t.getEstado());
        response.setPasoSagaActual(t.getPasoSagaActual());
        response.setIntentosSaga(t.getIntentosSaga());
        response.setScoreFraude(t.getScoreFraude());
        response.setDecisionFraude(t.getDecisionFraude());
        response.setIdUsuario(t.getIdUsuario());
        response.setIpOrigen(t.getIpOrigen());
        response.setDispositivo(t.getDispositivo());
        response.setTraceId(t.getTraceId());
        response.setFechaIniciada(t.getFechaIniciada());
        response.setFechaCompletada(t.getFechaCompletada());
        response.setFechaRevertida(t.getFechaRevertida());
        response.setMotivoRechazo(t.getMotivoRechazo());
        response.setHistorialEstados(historial);
        return response;
    }
}
