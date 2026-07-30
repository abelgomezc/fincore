package com.fincore.transferservice.service.impl;

import com.fincore.transferservice.domain.dto.IniciarTransferenciaRequest;
import com.fincore.transferservice.domain.dto.TransferenciaResponse;
import com.fincore.transferservice.domain.entity.HistorialEstadoTransferencia;
import com.fincore.transferservice.domain.entity.Transferencia;
import com.fincore.transferservice.domain.enums.EstadoTransferencia;
import com.fincore.transferservice.event.TransferenciaEvent;
import com.fincore.transferservice.repository.HistorialEstadoTransferenciaRepository;
import com.fincore.transferservice.repository.TransferenciaRepository;
import com.fincore.transferservice.service.TransferService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final TransferenciaRepository transferenciaRepository;
    private final HistorialEstadoTransferenciaRepository historialRepository;
    private final KafkaTemplate<String, TransferenciaEvent> kafkaTemplate;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public TransferServiceImpl(TransferenciaRepository transferenciaRepository,
                               HistorialEstadoTransferenciaRepository historialRepository,
                               KafkaTemplate<String, TransferenciaEvent> kafkaTemplate,
                               CircuitBreakerRegistry circuitBreakerRegistry,
                               RetryRegistry retryRegistry) {
        this.transferenciaRepository = transferenciaRepository;
        this.historialRepository = historialRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("transferencia");
        this.retry = retryRegistry.retry("transferenciaRetry");
    }

    @Override
    @Transactional
    public TransferenciaResponse iniciarTransferencia(IniciarTransferenciaRequest request) {
        log.info("Iniciando saga de transferencia para cuenta origen {} a cuenta destino {}",
                request.getCuentaOrigen(), request.getCuentaDestino());

        Transferencia transferencia = Transferencia.builder()
                .estado(EstadoTransferencia.PENDIENTE)
                .monto(request.getMonto())
                .moneda(request.getMoneda())
                .cuentaOrigen(request.getCuentaOrigen())
                .cuentaDestino(request.getCuentaDestino())
                .fechaCreacion(LocalDateTime.now())
                .build();
        transferencia = transferenciaRepository.save(transferencia);
        registrarHistorial(transferencia.getId(), null, EstadoTransferencia.PENDIENTE, "Transferencia creada");
        publicarEvento(transferencia.getId(), EstadoTransferencia.PENDIENTE, "Transferencia en estado PENDIENTE");

        try {
            ejecutarSaga(transferencia);
        } catch (Exception e) {
            log.error("Error en la saga de transferencia id={}: {}", transferencia.getId(), e.getMessage());
            revertirTransferencia(transferencia, e.getMessage());
        }

        return mapToResponse(transferencia);
    }

    private void ejecutarSaga(Transferencia transferencia) {
        avanzarEstado(transferencia, EstadoTransferencia.VALIDANDO, "Validando cuentas");
        if (!circuitBreaker.executeSupplier(() -> validarCuentas(transferencia))) {
            rechazarTransferencia(transferencia, "Validación de cuentas fallida");
            return;
        }

        avanzarEstado(transferencia, EstadoTransferencia.AUTORIZADA, "Transferencia autorizada");
        publicarEvento(transferencia.getId(), EstadoTransferencia.AUTORIZADA, "Transferencia autorizada");

        avanzarEstado(transferencia, EstadoTransferencia.RESERVANDO, "Reservando fondos");
        if (!circuitBreaker.executeSupplier(() -> reservarFondos(transferencia))) {
            rechazarTransferencia(transferencia, "Reserva de fondos fallida");
            return;
        }

        avanzarEstado(transferencia, EstadoTransferencia.PROCESANDO, "Procesando transferencia");
        if (!circuitBreaker.executeSupplier(() -> procesarTransferencia(transferencia))) {
            rechazarTransferencia(transferencia, "Procesamiento de transferencia fallido");
            return;
        }

        avanzarEstado(transferencia, EstadoTransferencia.ACREDITANDO, "Acreditando en cuenta destino");
        if (!circuitBreaker.executeSupplier(() -> acreditarDestino(transferencia))) {
            rechazarTransferencia(transferencia, "Acreditación en cuenta destino fallida");
            return;
        }

        avanzarEstado(transferencia, EstadoTransferencia.COMPLETADA, "Transferencia completada");
        publicarEvento(transferencia.getId(), EstadoTransferencia.COMPLETADA, "Transferencia completada exitosamente");
    }

    private boolean validarCuentas(Transferencia transferencia) {
        log.info("Validando cuentas origen {} y destino {}", transferencia.getCuentaOrigen(), transferencia.getCuentaDestino());
        return retry.executeSupplier(() -> {
            log.info("Validación de cuentas exitosa para transferencia id={}", transferencia.getId());
            return true;
        });
    }

    private boolean reservarFondos(Transferencia transferencia) {
        log.info("Reservando fondos para transferencia id={}", transferencia.getId());
        return retry.executeSupplier(() -> {
            log.info("Reserva de fondos exitosa para transferencia id={}", transferencia.getId());
            return true;
        });
    }

    private boolean procesarTransferencia(Transferencia transferencia) {
        log.info("Procesando transferencia id={}", transferencia.getId());
        return retry.executeSupplier(() -> {
            log.info("Procesamiento exitoso para transferencia id={}", transferencia.getId());
            return true;
        });
    }

    private boolean acreditarDestino(Transferencia transferencia) {
        log.info("Acreditando en cuenta destino {} para transferencia id={}", transferencia.getCuentaDestino(), transferencia.getId());
        return retry.executeSupplier(() -> {
            log.info("Acreditación exitosa para transferencia id={}", transferencia.getId());
            return true;
        });
    }

    private void avanzarEstado(Transferencia transferencia, EstadoTransferencia nuevoEstado, String motivo) {
        EstadoTransferencia anterior = transferencia.getEstado();
        transferencia.setEstado(nuevoEstado);
        transferenciaRepository.save(transferencia);
        registrarHistorial(transferencia.getId(), anterior, nuevoEstado, motivo);
        publicarEvento(transferencia.getId(), nuevoEstado, motivo);
    }

    private void rechazarTransferencia(Transferencia transferencia, String motivo) {
        log.warn("Rechazando transferencia id={}: {}", transferencia.getId(), motivo);
        transferencia.setEstado(EstadoTransferencia.RECHAZADA);
        transferenciaRepository.save(transferencia);
        registrarHistorial(transferencia.getId(), transferencia.getEstado(), EstadoTransferencia.RECHAZADA, motivo);
        publicarEvento(transferencia.getId(), EstadoTransferencia.RECHAZADA, motivo);
    }

    private void revertirTransferencia(Transferencia transferencia, String motivo) {
        log.warn("Revirtiendo transferencia id={}: {}", transferencia.getId(), motivo);
        transferencia.setEstado(EstadoTransferencia.REVERTIDA);
        transferenciaRepository.save(transferencia);
        registrarHistorial(transferencia.getId(), transferencia.getEstado(), EstadoTransferencia.REVERTIDA, motivo);
        publicarEvento(transferencia.getId(), EstadoTransferencia.REVERTIDA, motivo);
    }

    private void registrarHistorial(Long transferenciaId, EstadoTransferencia estadoAnterior,
                                    EstadoTransferencia estadoNuevo, String motivo) {
        HistorialEstadoTransferencia registro = HistorialEstadoTransferencia.builder()
                .transferenciaId(transferenciaId)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(estadoNuevo)
                .fechaCambio(LocalDateTime.now())
                .motivo(motivo)
                .build();
        historialRepository.save(registro);
    }

    private void publicarEvento(Long transferenciaId, EstadoTransferencia estado, String mensaje) {
        TransferenciaEvent event = new TransferenciaEvent(
                transferenciaId, estado, LocalDateTime.now(), mensaje);
        kafkaTemplate.send("transferencia-events", transferenciaId, event);
        log.info("Evento publicado para transferencia id={}: {}", transferenciaId, estado);
    }

    @Override
    @Transactional(readOnly = true)
    public TransferenciaResponse obtenerTransferencia(Long id) {
        Transferencia transferencia = transferenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transferencia no encontrada con id: " + id));
        return mapToResponse(transferencia);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferenciaResponse> listarTransferencias() {
        return transferenciaRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private TransferenciaResponse mapToResponse(Transferencia transferencia) {
        return TransferenciaResponse.builder()
                .id(transferencia.getId())
                .estado(transferencia.getEstado())
                .monto(transferencia.getMonto())
                .moneda(transferencia.getMoneda())
                .cuentaOrigen(transferencia.getCuentaOrigen())
                .cuentaDestino(transferencia.getCuentaDestino())
                .fechaCreacion(transferencia.getFechaCreacion())
                .build();
    }
}