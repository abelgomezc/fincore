package com.fincore.customer.service;

import com.fincore.customer.dto.request.CrearClienteRequest;
import com.fincore.customer.dto.request.ActualizarClienteRequest;
import com.fincore.customer.dto.response.ClienteResponse;
import com.fincore.customer.dto.response.KycResponse;
import com.fincore.customer.enums.EstadoKyc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interfaz del servicio de clientes.
 *
 * Define operaciones CRUD y de negocio para clientes,
 * incluyendo validación de cédula ecuatoriana y proceso KYC.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface ClienteService {

    ClienteResponse crearCliente(CrearClienteRequest request);

    ClienteResponse actualizarCliente(Long id, ActualizarClienteRequest request);

    ClienteResponse obtenerClientePorId(Long id);

    ClienteResponse obtenerClientePorEmail(String email);

    List<ClienteResponse> listarClientes();

    Page<ClienteResponse> buscarClientes(String nombre, Pageable pageable);

    void bloquearCliente(Long id, String motivo);

    void desbloquearCliente(Long id);

    void eliminarCliente(Long id);

    boolean validarCedula(String cedula);

    KycResponse obtenerKyc(Long idCliente);

    KycResponse actualizarKyc(Long idCliente, EstadoKyc estado, String observaciones);
}
