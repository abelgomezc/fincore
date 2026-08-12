package com.fincore.backoffice.controller;

import com.fincore.backoffice.entity.AuditoriaCambioBackoffice;
import com.fincore.backoffice.entity.UsuarioSistema;
import com.fincore.backoffice.enums.EstadoUsuarioSistema;
import com.fincore.backoffice.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/backoffice")
public class BackofficeController {

    private final UsuarioService usuarioService;
    private final RestClient restClient;

    public BackofficeController(UsuarioService usuarioService, RestClient restClient) {
        this.usuarioService = usuarioService;
        this.restClient = restClient;
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioSistema> crearUsuario(@RequestBody Map<String, String> request) {
        UsuarioSistema usuario = usuarioService.crearUsuario(
                request.get("username"),
                request.get("password"),
                request.get("nombreCompleto"),
                request.get("email"),
                request.get("roles")
        );
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioSistema>> listarUsuarios() {
        List<UsuarioSistema> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/usuarios/{username}")
    public ResponseEntity<UsuarioSistema> buscarUsuario(@PathVariable String username) {
        UsuarioSistema usuario = usuarioService.buscarPorUsername(username);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioSistema> actualizarUsuario(@PathVariable Long id,
                                                             @RequestBody Map<String, String> request) {
        UsuarioSistema usuario = usuarioService.actualizarUsuario(
                id,
                request.get("nombreCompleto"),
                request.get("email"),
                request.get("roles")
        );
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/usuarios/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id,
                                               @RequestParam EstadoUsuarioSistema estado) {
        usuarioService.cambiarEstado(id, estado);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/usuarios/validar")
    public ResponseEntity<Boolean> validarCredenciales(@RequestBody Map<String, String> request) {
        boolean valido = usuarioService.validarCredenciales(
                request.get("username"),
                request.get("password")
        );
        return ResponseEntity.ok(valido);
    }

    @GetMapping("/auditoria/usuario/{id}")
    public ResponseEntity<List<AuditoriaCambioBackoffice>> consultarAuditoriaUsuario(@PathVariable Long id) {
        List<AuditoriaCambioBackoffice> auditoria = usuarioService.consultarAuditoria(id);
        return ResponseEntity.ok(auditoria);
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<Map<String, Object>>> listarClientes() {
        List<Map<String, Object>> clientes = restClient.get()
                .uri("/api/clientes")
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<Map<String, Object>>>() {});
        return ResponseEntity.ok(clientes);
    }
}

