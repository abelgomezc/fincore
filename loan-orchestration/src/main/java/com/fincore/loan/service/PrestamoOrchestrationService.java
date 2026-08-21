package com.fincore.loan.service;

import com.fincore.loan.dto.request.AprobarRechazarRequest;
import com.fincore.loan.dto.request.CrearSolicitudPrestamoRequest;
import com.fincore.loan.dto.request.EvaluarRiesgoRequest;
import com.fincore.loan.dto.response.EvaluacionRiesgoResponse;
import com.fincore.loan.dto.response.SolicitudPrestamoResponse;

import java.util.List;

/**
 * Servicio de orquestación de préstamos.
 *
 * Gestiona el flujo completo:
 * 1. Crear solicitud
 * 2. Validar identidad
 * 3. Consultar buró de crédito
 * 4. Evaluar riesgo
 * 5. Generar documentos
 * 6. Enviar a firmar
 * 7. Notificar
 * 8. Desembolsar (contabilizar)
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface PrestamoOrchestrationService {

    SolicitudPrestamoResponse crearSolicitud(CrearSolicitudPrestamoRequest request);

    SolicitudPrestamoResponse iniciarFlujo(Long idSolicitud);

    SolicitudPrestamoResponse validarIdentidad(Long idSolicitud);

    SolicitudPrestamoResponse consultarBuro(Long idSolicitud);

    EvaluacionRiesgoResponse evaluarRiesgo(EvaluarRiesgoRequest request);

    SolicitudPrestamoResponse aprobarSolicitud(AprobarRechazarRequest request);

    SolicitudPrestamoResponse rechazarSolicitud(AprobarRechazarRequest request);

    SolicitudPrestamoResponse generarContrato(Long idSolicitud);

    SolicitudPrestamoResponse enviarContratoAFirmar(Long idSolicitud);

    SolicitudPrestamoResponse desembolsar(Long idSolicitud);

    SolicitudPrestamoResponse obtenerSolicitud(Long idSolicitud);

    List<SolicitudPrestamoResponse> listarSolicitudes();
}
