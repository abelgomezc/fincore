package com.fincore.backoffice.service;

import com.fincore.backoffice.entity.UsuarioSistema;

/**
 * Servicio de gestión de usuarios del backoffice.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface UsuarioService {

    UsuarioSistema crearUsuario(String username, String password, String nombreCompleto,
                                String email, String roles);

    UsuarioSistema buscarPorUsername(String username);

    boolean validarCredenciales(String username, String password);
}
