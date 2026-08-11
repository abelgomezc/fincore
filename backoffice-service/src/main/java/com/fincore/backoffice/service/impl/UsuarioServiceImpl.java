package com.fincore.backoffice.service.impl;

import com.fincore.backoffice.entity.UsuarioSistema;
import com.fincore.backoffice.enums.EstadoUsuarioSistema;
import com.fincore.backoffice.repository.UsuarioSistemaRepository;
import com.fincore.backoffice.repository.AuditoriaCambioBackofficeRepository;
import com.fincore.backoffice.entity.AuditoriaCambioBackoffice;
import com.fincore.backoffice.service.UsuarioService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de usuarios del backoffice.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Service
@Slf4j
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioSistemaRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final AuditoriaCambioBackofficeRepository auditoriaRepository;

    public UsuarioServiceImpl(UsuarioSistemaRepository repository,
                              PasswordEncoder passwordEncoder,
                              ObjectMapper objectMapper,
                              AuditoriaCambioBackofficeRepository auditoriaRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Override
    public UsuarioSistema crearUsuario(String username, String password, String nombreCompleto,
                                       String email, String roles) {
        if (repository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El usuario " + username + " ya existe");
        }

        UsuarioSistema usuario = new UsuarioSistema();
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setEmail(email);
        usuario.setRoles(roles);
        usuario.setEstado(EstadoUsuarioSistema.ACTIVO);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setFechaActualizacion(LocalDateTime.now());

        return repository.save(usuario);
    }

    @Override
    public UsuarioSistema buscarPorUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }

    @Override
    public List<UsuarioSistema> listarUsuarios() {
        return repository.findAll();
    }

    @Override
    public UsuarioSistema actualizarUsuario(Long id, String nombreCompleto, String email, String roles) {
        UsuarioSistema usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
        EstadoUsuarioSistema estadoAnterior = usuario.getEstado();
        if (nombreCompleto != null) usuario.setNombreCompleto(nombreCompleto);
        if (email != null) usuario.setEmail(email);
        if (roles != null) usuario.setRoles(roles);
        usuario.setFechaActualizacion(LocalDateTime.now());
        UsuarioSistema actualizado = repository.save(usuario);
        registrarCambio(actualizado, "ACTUALIZAR", null, null, estadoAnterior, actualizado.getEstado());
        return actualizado;
    }

    @Override
    public void cambiarEstado(Long id, EstadoUsuarioSistema estado) {
        UsuarioSistema usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
        EstadoUsuarioSistema estadoAnterior = usuario.getEstado();
        usuario.setEstado(estado);
        usuario.setFechaActualizacion(LocalDateTime.now());
        repository.save(usuario);
        registrarCambio(usuario, "CAMBIO_ESTADO", estadoAnterior, estado, estadoAnterior, estado);
        log.info("Usuario {} cambiado a estado: {}", usuario.getUsername(), estado);
    }

    @Override
    public boolean validarCredenciales(String username, String password) {
        return repository.findByUsername(username)
                .map(u -> passwordEncoder.matches(password, u.getPasswordHash()))
                .orElse(false);
    }

    private void registrarCambio(UsuarioSistema usuario, String accion, EstadoUsuarioSistema estadoAnterior, EstadoUsuarioSistema estadoNuevo, EstadoUsuarioSistema estadoAnteriorAudit, EstadoUsuarioSistema estadoNuevoAudit) {
        AuditoriaCambioBackoffice auditoria = new AuditoriaCambioBackoffice();
        auditoria.setIdUsuarioSistema(usuario.getId());
        auditoria.setEntidad("USUARIO_SISTEMA");
        auditoria.setIdEntidad(usuario.getId().toString());
        auditoria.setAccion(accion);
        if (estadoAnterior != null && estadoNuevo != null) {
            auditoria.setValoresAnteriores("{\"estado\":\"" + estadoAnteriorAudit.name() + "\"}");
            auditoria.setValoresNuevos("{\"estado\":\"" + estadoNuevoAudit.name() + "\"}");
        } else {
            auditoria.setValoresAnteriores(null);
            auditoria.setValoresNuevos(null);
        }
        auditoria.setIpOrigen(null);
        auditoria.setUserAgent(null);
        auditoria.setDispositivo(null);
        auditoria.setFechaCambio(LocalDateTime.now());
        auditoria.setCreadoPor("system");
        auditoria.setActualizadoPor("system");
        auditoria.setVersion(0L);
        auditoriaRepository.save(auditoria);
    }
}
