package com.fincore.customer.service.impl;

import com.fincore.customer.dto.request.ActualizarClienteRequest;
import com.fincore.customer.dto.request.CrearClienteRequest;
import com.fincore.customer.dto.response.ClienteResponse;
import com.fincore.customer.dto.response.KycResponse;
import com.fincore.customer.entity.Cliente;
import com.fincore.customer.entity.ContactoEmergencia;
import com.fincore.customer.entity.DireccionCliente;
import com.fincore.customer.entity.DocumentoIdentidad;
import com.fincore.customer.entity.KycVerificacion;
import com.fincore.customer.enums.EstadoCliente;
import com.fincore.customer.enums.EstadoKyc;
import com.fincore.customer.enums.TipoDocumento;
import com.fincore.customer.exception.ClienteNoEncontradoException;
import com.fincore.customer.kafka.ClienteEventProducer;
import com.fincore.customer.repository.ClienteRepository;
import com.fincore.customer.service.ClienteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de clientes.
 *
 * Incluye:
 * - Validación de cédula ecuatoriana (algoritmo módulo 10)
 * - Proceso KYC simulado con estados
 * - Verificación AML básica
 * - Publicación de eventos Kafka
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Service
@Slf4j
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteEventProducer eventProducer;

    public ClienteServiceImpl(ClienteRepository clienteRepository,
                              ClienteEventProducer eventProducer) {
        this.clienteRepository = clienteRepository;
        this.eventProducer = eventProducer;
    }

    @Override
    public ClienteResponse crearCliente(CrearClienteRequest request) {
        log.info("Creando cliente: {} {}", request.getPrimerNombre(), request.getPrimerApellido());

        // Validar cédula si es persona natural y tipo de documento es cédula
        if (Boolean.TRUE.equals(request.getDocumentos() != null && !request.getDocumentos().isEmpty())
                && request.getDocumentos().get(0).getTipoDocumento().equals("CEDULA")) {
            String cedula = request.getDocumentos().get(0).getNumeroDocumento();
            if (!validarCedula(cedula)) {
                throw new IllegalArgumentException("Cédula inválida: " + cedula);
            }
        }

        // Verificar email único
        if (request.getEmail() != null && clienteRepository.existsByEmail(request.getEmail())) {
            throw new DataIntegrityViolationException("El email ya está registrado: " + request.getEmail());
        }

        Cliente cliente = new Cliente();
        cliente.setTipoCliente(request.getTipoCliente());
        cliente.setEstado(EstadoCliente.ACTIVO);
        cliente.setPrimerNombre(request.getPrimerNombre());
        cliente.setSegundoNombre(request.getSegundoNombre());
        cliente.setPrimerApellido(request.getPrimerApellido());
        cliente.setSegundoApellido(request.getSegundoApellido());
        cliente.setFechaNacimiento(request.getFechaNacimiento());
        cliente.setGenero(request.getGenero());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());
        cliente.setCiudad(request.getCiudad());
        cliente.setPais(request.getPais() != null ? request.getPais() : "EC");
        cliente.setFechaRegistro(LocalDate.now());

        // Documentos
        if (request.getDocumentos() != null) {
            for (CrearClienteRequest.DocumentoRequest docReq : request.getDocumentos()) {
                DocumentoIdentidad doc = new DocumentoIdentidad();
                doc.setCliente(cliente);
                doc.setTipoDocumento(TipoDocumento.valueOf(docReq.getTipoDocumento()));
                doc.setNumeroDocumento(docReq.getNumeroDocumento());
                doc.setFechaExpedicion(docReq.getFechaExpedicion());
                doc.setFechaExpiracion(docReq.getFechaExpiracion());
                doc.setPaisEmision(docReq.getPaisEmision() != null ? docReq.getPaisEmision() : "EC");
                doc.setVerificado(false);
                cliente.addDocumento(doc);
            }
        }

        // Direcciones
        if (request.getDirecciones() != null) {
            for (CrearClienteRequest.DireccionRequest dirReq : request.getDirecciones()) {
                DireccionCliente direccion = new DireccionCliente();
                direccion.setCliente(cliente);
                direccion.setTipoDireccion(dirReq.getTipoDireccion());
                direccion.setCallePrincipal(dirReq.getCallePrincipal());
                direccion.setCalleSecundaria(dirReq.getCalleSecundaria());
                direccion.setCiudad(dirReq.getCiudad());
                direccion.setProvincia(dirReq.getProvincia());
                direccion.setPais(dirReq.getPais() != null ? dirReq.getPais() : "EC");
                direccion.setCodigoPostal(dirReq.getCodigoPostal());
                cliente.addDireccion(direccion);
            }
        }

        // Contactos de emergencia
        if (request.getContactosEmergencia() != null) {
            for (CrearClienteRequest.ContactoRequest contactoReq : request.getContactosEmergencia()) {
                ContactoEmergencia contacto = new ContactoEmergencia();
                contacto.setCliente(cliente);
                contacto.setNombre(contactoReq.getNombre());
                contacto.setTelefono(contactoReq.getTelefono());
                contacto.setParentesco(contactoReq.getParentesco());
                cliente.addContactoEmergencia(contacto);
            }
        }

        Cliente saved = clienteRepository.save(cliente);

        // Publicar evento
        eventProducer.publicarClienteCreado(saved.getId(), saved.getNombreCompleto(), saved.getEmail());

        log.info("Cliente creado exitosamente: ID={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public ClienteResponse actualizarCliente(Long id, ActualizarClienteRequest request) {
        log.info("Actualizando cliente: ID={}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado: " + id));

        if (request.getPrimerNombre() != null) cliente.setPrimerNombre(request.getPrimerNombre());
        if (request.getSegundoNombre() != null) cliente.setSegundoNombre(request.getSegundoNombre());
        if (request.getPrimerApellido() != null) cliente.setPrimerApellido(request.getPrimerApellido());
        if (request.getSegundoApellido() != null) cliente.setSegundoApellido(request.getSegundoApellido());
        if (request.getEmail() != null) cliente.setEmail(request.getEmail());
        if (request.getTelefono() != null) cliente.setTelefono(request.getTelefono());
        if (request.getCiudad() != null) cliente.setCiudad(request.getCiudad());
        if (request.getEstado() != null) cliente.setEstado(request.getEstado());

        Cliente saved = clienteRepository.save(cliente);

        log.info("Cliente actualizado: ID={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse obtenerClientePorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado: " + id));
        return toResponse(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse obtenerClientePorEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado: " + email));
        return toResponse(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listarClientes() {
        return clienteRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponse> buscarClientes(String nombre, Pageable pageable) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre, pageable)
                .map(this::toResponse);
    }

    @Override
    public void bloquearCliente(Long id, String motivo) {
        log.info("Bloqueado cliente: ID={}, motivo={}", id, motivo);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado: " + id));
        cliente.setEstado(EstadoCliente.BLOQUEADO);
        clienteRepository.save(cliente);
        eventProducer.publicarClienteBloqueado(id, motivo);
    }

    @Override
    public void desbloquearCliente(Long id) {
        log.info("Desbloqueando cliente: ID={}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado: " + id));
        cliente.setEstado(EstadoCliente.ACTIVO);
        clienteRepository.save(cliente);
        eventProducer.publicarClienteDesbloqueado(id);
    }

    @Override
    public void eliminarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado: " + id));
        cliente.setEstado(EstadoCliente.INACTIVO);
        clienteRepository.save(cliente);
        eventProducer.publicarClienteDesactivado(id);
    }

    @Override
    public boolean validarCedula(String cedula) {
        if (cedula == null || cedula.length() != 10) {
            return false;
        }

        try {
            int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
            int suma = 0;
            for (int i = 0; i < 9; i++) {
                int digito = Character.getNumericValue(cedula.charAt(i));
                int producto = digito * coeficientes[i];
                if (producto >= 10) {
                    producto -= 9;
                }
                suma += producto;
            }

            int digitoVerificador = Character.getNumericValue(cedula.charAt(9));
            int resto = suma % 10;
            int resultado = (resto == 0) ? 0 : 10 - resto;

            return resultado == digitoVerificador;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public KycResponse obtenerKyc(Long idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado: " + idCliente));

        KycVerificacion kyc = cliente.getKycVerificaciones() != null && !cliente.getKycVerificaciones().isEmpty()
                ? cliente.getKycVerificaciones().get(0)
                : null;

        if (kyc == null) {
            return KycResponse.builder()
                    .idCliente(idCliente)
                    .estado(EstadoKyc.PENDIENTE)
                    .build();
        }

        return toKycResponse(kyc);
    }

    @Override
    public KycResponse actualizarKyc(Long idCliente, EstadoKyc estado, String observaciones) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado: " + idCliente));

        if (cliente.getKycVerificaciones() == null || cliente.getKycVerificaciones().isEmpty()) {
            KycVerificacion kyc = new KycVerificacion();
            kyc.setCliente(cliente);
            kyc.setEstado(estado);
            kyc.setFechaVerificacion(LocalDate.now().atStartOfDay());
            kyc.setVerificadoPor("SYSTEM");
            kyc.setObservaciones(observaciones);
            cliente.getKycVerificaciones().add(kyc);
        } else {
            KycVerificacion kyc = cliente.getKycVerificaciones().get(0);
            kyc.setEstado(estado);
            kyc.setFechaVerificacion(LocalDate.now().atStartOfDay());
            kyc.setVerificadoPor("SYSTEM");
            kyc.setObservaciones(observaciones);
        }

        Cliente saved = clienteRepository.save(cliente);
        KycVerificacion kyc = saved.getKycVerificaciones().get(0);

        if (estado == EstadoKyc.APROBADO) {
            eventProducer.publicarClienteKycAprobado(idCliente);
        } else if (estado == EstadoKyc.RECHAZADO) {
            eventProducer.publicarClienteKycRechazado(idCliente);
        }

        return toKycResponse(kyc);
    }

    private ClienteResponse toResponse(Cliente cliente) {
        ClienteResponse.ClienteResponseBuilder builder = ClienteResponse.builder()
                .id(cliente.getId())
                .tipoCliente(cliente.getTipoCliente())
                .estado(cliente.getEstado())
                .primerNombre(cliente.getPrimerNombre())
                .segundoNombre(cliente.getSegundoNombre())
                .primerApellido(cliente.getPrimerApellido())
                .segundoApellido(cliente.getSegundoApellido())
                .nombreCompleto(cliente.getNombreCompleto())
                .fechaNacimiento(cliente.getFechaNacimiento())
                .genero(cliente.getGenero())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .direccion(cliente.getDireccion())
                .ciudad(cliente.getCiudad())
                .pais(cliente.getPais())
                .fechaRegistro(cliente.getFechaRegistro() != null ? cliente.getFechaRegistro().toString() : null)
                .kycAprobado(cliente.isKycAprobado())
                .fechaCreacion(cliente.getFechaCreacion() != null ? cliente.getFechaCreacion().toString() : null)
                .fechaActualizacion(cliente.getFechaActualizacion() != null ? cliente.getFechaActualizacion().toString() : null);

        if (cliente.getDocumentos() != null) {
            builder.documentos(cliente.getDocumentos().stream()
                    .map(this::toDocumentoResponse)
                    .collect(Collectors.toList()));
        }

        if (cliente.getDirecciones() != null) {
            builder.direcciones(cliente.getDirecciones().stream()
                    .map(this::toDireccionResponse)
                    .collect(Collectors.toList()));
        }

        if (cliente.getContactosEmergencia() != null) {
            builder.contactosEmergencia(cliente.getContactosEmergencia().stream()
                    .map(this::toContactoResponse)
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }

    private ClienteResponse.DocumentoResponse toDocumentoResponse(DocumentoIdentidad doc) {
        return ClienteResponse.DocumentoResponse.builder()
                .id(doc.getId())
                .tipoDocumento(doc.getTipoDocumento().name())
                .numeroDocumento(doc.getNumeroDocumento())
                .fechaExpedicion(doc.getFechaExpedicion() != null ? doc.getFechaExpedicion().toString() : null)
                .fechaExpiracion(doc.getFechaExpiracion() != null ? doc.getFechaExpiracion().toString() : null)
                .paisEmision(doc.getPaisEmision())
                .verificado(doc.getVerificado())
                .vigente(doc.esVigente())
                .build();
    }

    private ClienteResponse.DireccionResponse toDireccionResponse(DireccionCliente dir) {
        return ClienteResponse.DireccionResponse.builder()
                .id(dir.getId())
                .tipoDireccion(dir.getTipoDireccion())
                .callePrincipal(dir.getCallePrincipal())
                .calleSecundaria(dir.getCalleSecundaria())
                .ciudad(dir.getCiudad())
                .provincia(dir.getProvincia())
                .pais(dir.getPais())
                .codigoPostal(dir.getCodigoPostal())
                .latitud(dir.getLatitud() != null ? dir.getLatitud().doubleValue() : null)
                .longitud(dir.getLongitud() != null ? dir.getLongitud().doubleValue() : null)
                .build();
    }

    private ClienteResponse.ContactoEmergenciaResponse toContactoResponse(ContactoEmergencia contacto) {
        return ClienteResponse.ContactoEmergenciaResponse.builder()
                .id(contacto.getId())
                .nombre(contacto.getNombre())
                .telefono(contacto.getTelefono())
                .parentesco(contacto.getParentesco())
                .build();
    }

    private KycResponse toKycResponse(KycVerificacion kyc) {
        return KycResponse.builder()
                .id(kyc.getId())
                .idCliente(kyc.getCliente().getId())
                .estado(kyc.getEstado())
                .fechaVerificacion(kyc.getFechaVerificacion() != null ? kyc.getFechaVerificacion().toString() : null)
                .verificadoPor(kyc.getVerificadoPor())
                .observaciones(kyc.getObservaciones())
                .fechaCreacion(kyc.getFechaCreacion() != null ? kyc.getFechaCreacion().toString() : null)
                .build();
    }
}
