package com.fincore.backoffice.service;

import com.fincore.backoffice.entity.AuditoriaCambioBackoffice;
import com.fincore.backoffice.entity.UsuarioSistema;
import com.fincore.backoffice.enums.EstadoUsuarioSistema;

import java.util.List;

/**
 * Servicio de gestión de usuarios del backoffice.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface UsuarioService {

    UsuarioSistema crearUsuario(String username, String password, String nombreCompleto,
                                String email, String roles);

    UsuarioSistema buscarPorUsername(String username);

    List<UsuarioSistema> listarUsuarios();

    UsuarioSistema actualizarUsuario(Long id, String nombreCompleto, String email, String roles);

    void cambiarEstado(Long id, EstadoUsuarioSistema estado);

    boolean validarCredenciales(String username, String password);

    List<AuditoriaCambioBackoffice> consultarAuditoria(Long id);
}
