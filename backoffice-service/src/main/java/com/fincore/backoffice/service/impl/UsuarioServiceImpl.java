package com.fincore.backoffice.service.impl;

import com.fincore.backoffice.entity.UsuarioSistema;
import com.fincore.backoffice.repository.UsuarioSistemaRepository;
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

    public UsuarioServiceImpl(UsuarioSistemaRepository repository,
                              PasswordEncoder passwordEncoder,
                              ObjectMapper objectMapper) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
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
        usuario.setEsActivo(true);
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
    public boolean validarCredenciales(String username, String password) {
        return repository.findByUsername(username)
                .map(u -> passwordEncoder.matches(password, u.getPasswordHash()))
                .orElse(false);
    }
}
