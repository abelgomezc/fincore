package com.fincore.customer.service.impl;

import com.fincore.customer.entity.Cliente;
import com.fincore.customer.entity.KycVerificacion;
import com.fincore.customer.enums.EstadoKyc;
import com.fincore.customer.exception.ClienteNoEncontradoException;
import com.fincore.customer.kafka.ClienteEventProducer;
import com.fincore.customer.repository.ClienteRepository;
import com.fincore.customer.service.KycService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Implementación del servicio de KYC (Know Your Customer).
 *
 * Gestiona el proceso de verificación:
 * - Iniciación de verificación
 * - Aprobación/rechazo
 * - Verificación de documentos
 * - Consulta de estado
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Service
@Slf4j
@Transactional
public class KycServiceImpl implements KycService {

    private final ClienteRepository clienteRepository;
    private final ClienteEventProducer eventProducer;

    public KycServiceImpl(ClienteRepository clienteRepository,
                          ClienteEventProducer eventProducer) {
        this.clienteRepository = clienteRepository;
        this.eventProducer = eventProducer;
    }

    @Override
    public void iniciarVerificacion(Long idCliente) {
        log.info("Iniciando verificación KYC para cliente: {}", idCliente);

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado: " + idCliente));

        KycVerificacion kyc = new KycVerificacion();
        kyc.setCliente(cliente);
        kyc.setEstado(EstadoKyc.EN_REVISION);
        kyc.setVerificadoPor("sistema");

        cliente.getKycVerificaciones().add(kyc);
        clienteRepository.save(cliente);

        eventProducer.publicarClienteKycEnRevision(idCliente);
    }

    @Override
    public void aprobarKyc(Long idCliente, String verificadoPor, String observaciones) {
        log.info("Aprobando KYC para cliente: {}", idCliente);

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado: " + idCliente));

        if (cliente.getKycVerificaciones() == null || cliente.getKycVerificaciones().isEmpty()) {
            iniciarVerificacion(idCliente);
            cliente = clienteRepository.findById(idCliente)
                    .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado: " + idCliente));
        }

        KycVerificacion kyc = cliente.getKycVerificaciones().get(cliente.getKycVerificaciones().size() - 1);
        kyc.setEstado(EstadoKyc.APROBADO);
        kyc.setFechaVerificacion(LocalDate.now().atStartOfDay());
        kyc.setVerificadoPor(verificadoPor);
        kyc.setObservaciones(observaciones);

        clienteRepository.save(cliente);
        eventProducer.publicarClienteKycAprobado(idCliente);
    }

    @Override
    public void rechazarKyc(Long idCliente, String verificadoPor, String observaciones) {
        log.info("Rechazando KYC para cliente: {}", idCliente);

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado: " + idCliente));

        if (cliente.getKycVerificaciones() == null || cliente.getKycVerificaciones().isEmpty()) {
            iniciarVerificacion(idCliente);
            cliente = clienteRepository.findById(idCliente)
                    .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado: " + idCliente));
        }

        KycVerificacion kyc = cliente.getKycVerificaciones().get(cliente.getKycVerificaciones().size() - 1);
        kyc.setEstado(EstadoKyc.RECHAZADO);
        kyc.setFechaVerificacion(LocalDate.now().atStartOfDay());
        kyc.setVerificadoPor(verificadoPor);
        kyc.setObservaciones(observaciones);

        clienteRepository.save(cliente);
        eventProducer.publicarClienteKycRechazado(idCliente);
    }

    @Override
    public void verificarDocumentos(Long idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado: " + idCliente));

        if (cliente.getDocumentos() != null) {
            for (var doc : cliente.getDocumentos()) {
                if (doc.esVigente()) {
                    doc.setVerificado(true);
                    doc.setFechaVerificacion(LocalDate.now());
                } else {
                    doc.setVerificado(false);
                }
            }
            clienteRepository.save(cliente);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaKycAprobado(Long idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado: " + idCliente));

        return cliente.getKycVerificaciones() != null && cliente.getKycVerificaciones().stream()
                .anyMatch(k -> k.getEstado() == EstadoKyc.APROBADO);
    }
}
