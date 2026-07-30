package com.fincore.authservice.service.impl;

import com.fincore.authservice.domain.dto.CrearUsuarioRequest;
import com.fincore.authservice.domain.dto.UsuarioResponse;
import com.fincore.authservice.domain.entity.Rol;
import com.fincore.authservice.domain.entity.UsuarioSistema;
import com.fincore.authservice.domain.enums.RolUsuario;
import com.fincore.authservice.repository.RolRepository;
import com.fincore.authservice.repository.UsuarioSistemaRepository;
import com.fincore.authservice.service.UsuarioSistemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioSistemaServiceImpl implements UsuarioSistemaService {
    private final UsuarioSistemaRepository usuarioSistemaRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioResponse crearUsuario(CrearUsuarioRequest request) {
        UsuarioSistema usuario = UsuarioSistema.builder()
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .email(request.email())
                .activo(Boolean.TRUE)
                .build();

        usuario = usuarioSistemaRepository.save(usuario);
        return toResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioSistemaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private UsuarioResponse toResponse(UsuarioSistema usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getEmail(),
                usuario.getActivo()
        );
    }
}
