package com.fincore.customerservice.service;

import com.fincore.customerservice.domain.dto.ActualizarClienteRequest;
import com.fincore.customerservice.domain.dto.ClienteResponse;
import com.fincore.customerservice.domain.dto.CrearClienteRequest;

import java.util.List;
import java.util.Optional;

/*
 * Interfaz de servicio para la gestión de clientes
 */
public interface ClienteService {

    ClienteResponse crearCliente(CrearClienteRequest request);

    List<ClienteResponse> obtenerTodosLosClientes();

    Optional<ClienteResponse> obtenerClientePorId(Long id);

    Optional<ClienteResponse> obtenerClientePorNumeroIdentificacion(String numeroIdentificacion);
}
