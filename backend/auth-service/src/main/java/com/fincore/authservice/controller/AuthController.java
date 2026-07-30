package com.fincore.authservice.controller;

import com.fincore.authservice.domain.dto.CrearUsuarioRequest;
import com.fincore.authservice.domain.dto.LoginRequest;
import com.fincore.authservice.domain.dto.UsuarioResponse;
import com.fincore.authservice.service.UsuarioSistemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Autenticación y gestión de usuarios")
public class AuthController {
    private final UsuarioSistemaService usuarioSistemaService;

    public AuthController(UsuarioSistemaService usuarioSistemaService) {
        this.usuarioSistemaService = usuarioSistemaService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok("token-placeholder");
    }

    @PostMapping("/usuarios")
    @Operation(summary = "Crear usuario")
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody CrearUsuarioRequest request) {
        UsuarioResponse response = usuarioSistemaService.crearUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Listar usuarios")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(usuarioSistemaService.listarUsuarios());
    }
}
