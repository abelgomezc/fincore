package com.fincore.customerservice.service.impl;

import com.fincore.customerservice.domain.dto.ClienteResponse;
import com.fincore.customerservice.domain.dto.CrearClienteRequest;
import com.fincore.customerservice.domain.entity.Cliente;
import com.fincore.customerservice.domain.enums.EstadoCliente;
import com.fincore.customerservice.repository.ClienteRepository;
import com.fincore.customerservice.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * Implementación del servicio de gestión de clientes
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public ClienteResponse crearCliente(CrearClienteRequest request) {
        log.info("Creando nuevo cliente con número de identificación: {}", request.getNumeroIdentificacion());

        Cliente cliente = Cliente.builder()
                .tipoPersona(request.getTipoPersona())
                .numeroIdentificacion(request.getNumeroIdentificacion())
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .razonSocial(request.getRazonSocial())
                .fechaNacimiento(request.getFechaNacimiento())
                .nacionalidad(request.getNacionalidad())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .estado(request.getEstado() != null ? request.getEstado() : EstadoCliente.ACTIVO)
                .build();

        Cliente clienteGuardado = clienteRepository.save(cliente);
        log.info("Cliente creado con ID: {}", clienteGuardado.getId());

        ClienteResponse response = mapearAClienteResponse(clienteGuardado);

        kafkaTemplate.send("${KAFKA_TOPIC_CLIENTE_CREADO:cliente-creado}", response);
        log.info("Evento cliente-creado enviado a Kafka para ID: {}", clienteGuardado.getId());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> obtenerTodosLosClientes() {
        log.info("Obteniendo todos los clientes");
        return clienteRepository.findAll()
                .stream()
                .map(this::mapearAClienteResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteResponse> obtenerClientePorId(Long id) {
        log.info("Obteniendo cliente por ID: {}", id);
        return clienteRepository.findById(id)
                .map(this::mapearAClienteResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteResponse> obtenerClientePorNumeroIdentificacion(String numeroIdentificacion) {
        log.info("Obteniendo cliente por número de identificación: {}", numeroIdentificacion);
        return clienteRepository.findByNumeroIdentificacion(numeroIdentificacion)
                .map(this::mapearAClienteResponse);
    }

    private ClienteResponse mapearAClienteResponse(Cliente cliente) {
        return ClienteResponse.builder()
                .id(cliente.getId())
                .tipoPersona(cliente.getTipoPersona())
                .numeroIdentificacion(cliente.getNumeroIdentificacion())
                .nombres(cliente.getNombres())
                .apellidos(cliente.getApellidos())
                .razonSocial(cliente.getRazonSocial())
                .fechaNacimiento(cliente.getFechaNacimiento())
                .nacionalidad(cliente.getNacionalidad())
                .direccion(cliente.getDireccion())
                .telefono(cliente.getTelefono())
                .email(cliente.getEmail())
                .estado(cliente.getEstado())
                .creadoEn(cliente.getCreadoEn())
                .actualizadoEn(cliente.getActualizadoEn())
                .build();
    }
}
