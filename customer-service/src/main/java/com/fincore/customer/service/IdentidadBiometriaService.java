package com.fincore.customer.service;

import com.fincore.customer.dto.request.IniciarBiometriaRequest;
import com.fincore.customer.dto.request.ValidarIdentidadRequest;
import com.fincore.customer.dto.response.SesionBiometricaResponse;
import com.fincore.customer.dto.response.ValidacionIdentidadResponse;

import java.util.List;

/**
 * Servicio de validación de identidad y biometría.
 *
 * Gestiona:
 * - Validaciones de identidad (documento, OTP, etc.)
 * - Sesiones biométricas (facial, huella, voz)
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface IdentidadBiometriaService {

    ValidacionIdentidadResponse validarIdentidad(ValidarIdentidadRequest request);

    List<ValidacionIdentidadResponse> consultarValidaciones(Long idCliente);

    SesionBiometricaResponse iniciarBiometria(IniciarBiometriaRequest request);

    SesionBiometricaResponse finalizarBiometria(Long idSesion, String estado, Integer puntajeConfianza, String detalle);

    List<SesionBiometricaResponse> consultarBiometrias(Long idCliente);
}
