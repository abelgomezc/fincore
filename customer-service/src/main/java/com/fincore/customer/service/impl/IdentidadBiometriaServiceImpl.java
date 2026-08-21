package com.fincore.customer.service.impl;

import com.fincore.customer.dto.request.IniciarBiometriaRequest;
import com.fincore.customer.dto.request.ValidarIdentidadRequest;
import com.fincore.customer.dto.response.SesionBiometricaResponse;
import com.fincore.customer.dto.response.ValidacionIdentidadResponse;
import com.fincore.customer.entity.Cliente;
import com.fincore.customer.entity.SesionBiometrica;
import com.fincore.customer.entity.ValidacionIdentidad;
import com.fincore.customer.exception.ClienteNoEncontradoException;
import com.fincore.customer.kafka.ClienteEventProducer;
import com.fincore.customer.repository.ClienteRepository;
import com.fincore.customer.repository.SesionBiometricaRepository;
import com.fincore.customer.repository.ValidacionIdentidadRepository;
import com.fincore.customer.service.IdentidadBiometriaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementación del servicio de identidad y biometría.
 *
 * Simula integración con proveedores externos de biometría y validación de identidad.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Service
@Slf4j
@Transactional
public class IdentidadBiometriaServiceImpl implements IdentidadBiometriaService {

    private final ValidacionIdentidadRepository validacionIdentidadRepository;
    private final SesionBiometricaRepository sesionBiometricaRepository;
    private final ClienteRepository clienteRepository;
    private final ClienteEventProducer eventProducer;

    public IdentidadBiometriaServiceImpl(ValidacionIdentidadRepository validacionIdentidadRepository,
                                          SesionBiometricaRepository sesionBiometricaRepository,
                                          ClienteRepository clienteRepository,
                                          ClienteEventProducer eventProducer) {
        this.validacionIdentidadRepository = validacionIdentidadRepository;
        this.sesionBiometricaRepository = sesionBiometricaRepository;
        this.clienteRepository = clienteRepository;
        this.eventProducer = eventProducer;
    }

    @Override
    public ValidacionIdentidadResponse validarIdentidad(ValidarIdentidadRequest request) {
        log.info("Iniciando validación de identidad para cliente: {} - tipo: {}", request.idCliente(), request.tipoValidacion());

        Cliente cliente = clienteRepository.findById(request.idCliente())
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado: " + request.idCliente()));

        ValidacionIdentidad validacion = new ValidacionIdentidad();
        validacion.setCliente(cliente);
        validacion.setTipoValidacion(request.tipoValidacion());
        validacion.setEstado("EN_PROCESO");
        validacion.setProveedor(request.proveedor() != null ? request.proveedor() : "mock-provider");
        validacion.setPuntajeConfianza(85);
        validacion.setDetalle(request.detalle());
        validacion.setFechaValidacion(LocalDateTime.now());
        validacion.setIdTransaccion(UUID.randomUUID().toString());

        validacionIdentidadRepository.save(validacion);

        validacion.setEstado("APROBADO");
        validacion.setPuntajeConfianza(92);
        validacionIdentidadRepository.save(validacion);

        eventProducer.publicarValidacionIdentidadCompletada(request.idCliente(), "APROBADO");

        return toResponse(validacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValidacionIdentidadResponse> consultarValidaciones(Long idCliente) {
        return validacionIdentidadRepository.findByIdClienteOrderByFechaValidacionDesc(idCliente)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public SesionBiometricaResponse iniciarBiometria(IniciarBiometriaRequest request) {
        log.info("Iniciando biometría para cliente: {} - tipo: {}", request.idCliente(), request.tipoBiometria());

        Cliente cliente = clienteRepository.findById(request.idCliente())
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado: " + request.idCliente()));

        SesionBiometrica sesion = new SesionBiometrica();
        sesion.setCliente(cliente);
        sesion.setTipoBiometria(request.tipoBiometria());
        sesion.setEstado("INICIADA");
        sesion.setProveedor(request.proveedor() != null ? request.proveedor() : "mock-provider");
        sesion.setFechaInicio(LocalDateTime.now());
        sesion.setIdSesionProveedor(request.idSesionProveedor() != null ? request.idSesionProveedor() : UUID.randomUUID().toString());

        sesionBiometricaRepository.save(sesion);
        eventProducer.publicarBiometriaIniciada(request.idCliente());

        return toResponse(sesion);
    }

    @Override
    public SesionBiometricaResponse finalizarBiometria(Long idSesion, String estado, Integer puntajeConfianza, String detalle) {
        log.info("Finalizando biometría sesión: {} - estado: {}", idSesion, estado);

        SesionBiometrica sesion = sesionBiometricaRepository.findById(idSesion)
                .orElseThrow(() -> new ClienteNoEncontradoException("Sesión biométrica no encontrada: " + idSesion));

        sesion.setEstado(estado);
        sesion.setPuntajeConfianza(puntajeConfianza);
        sesion.setDetalle(detalle);
        sesion.setFechaFin(LocalDateTime.now());

        sesionBiometricaRepository.save(sesion);
        eventProducer.publicarBiometriaFinalizada(sesion.getCliente().getId(), estado);

        return toResponse(sesion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SesionBiometricaResponse> consultarBiometrias(Long idCliente) {
        return sesionBiometricaRepository.findByIdClienteOrderByFechaInicioDesc(idCliente)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ValidacionIdentidadResponse toResponse(ValidacionIdentidad v) {
        ValidacionIdentidadResponse r = new ValidacionIdentidadResponse();
        r.setId(v.getId());
        r.setIdCliente(v.getCliente().getId());
        r.setTipoValidacion(v.getTipoValidacion());
        r.setEstado(v.getEstado());
        r.setProveedor(v.getProveedor());
        r.setPuntajeConfianza(v.getPuntajeConfianza());
        r.setDetalle(v.getDetalle());
        r.setFechaValidacion(v.getFechaValidacion());
        r.setIdTransaccion(v.getIdTransaccion());
        return r;
    }

    private SesionBiometricaResponse toResponse(SesionBiometrica s) {
        SesionBiometricaResponse r = new SesionBiometricaResponse();
        r.setId(s.getId());
        r.setIdCliente(s.getCliente().getId());
        r.setTipoBiometria(s.getTipoBiometria());
        r.setEstado(s.getEstado());
        r.setProveedor(s.getProveedor());
        r.setPuntajeConfianza(s.getPuntajeConfianza());
        r.setFechaInicio(s.getFechaInicio());
        r.setFechaFin(s.getFechaFin());
        r.setIdSesionProveedor(s.getIdSesionProveedor());
        r.setDetalle(s.getDetalle());
        return r;
    }
}
