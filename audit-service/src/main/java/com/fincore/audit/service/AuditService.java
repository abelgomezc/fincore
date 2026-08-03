package com.fincore.audit.service;

import com.fincore.audit.entity.RegistroAuditoria;
import com.fincore.audit.enums.AccionAuditoria;
import com.fincore.audit.enums.ResultadoAuditoria;

/**
 * Servicio de auditoría — registra todas las operaciones del sistema.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface AuditService {

    /**
     * Registra un evento auditado.
     *
     * @param traceId         Trace ID de la operación
     * @param servicio        Servicio que origina el evento
     * @param endpoint        Endpoint HTTP
     * @param metodoHttp      Método HTTP
     * @param idUsuario       ID del usuario
     * @param rolUsuario      Rol del usuario
     * @param ipOrigen        IP de origen
     * @param idRecurso       ID del recurso afectado
     * @param tipoRecurso     Tipo de recurso
     * @param accion          Acción realizada
     * @param resultado       Resultado (EXITOSO, FALLIDO, RECHAZADO)
     * @param requestBody     Body de la request (JSON)
     * @param responseCodigo  Código de respuesta HTTP
     * @param tiempoMs        Tiempo de respuesta en ms
     * @param detalle         Detalle adicional
     */
    void registrarEvento(String traceId, String servicio, String endpoint, String metodoHttp,
                         String idUsuario, String rolUsuario, String ipOrigen,
                         String idRecurso, String tipoRecurso, AccionAuditoria accion,
                         ResultadoAuditoria resultado, String requestBody,
                         Integer responseCodigo, Integer tiempoMs, String detalle);

    /**
     * Busca el historial auditivo de un trace ID.
     */
    Iterable<RegistroAuditoria> buscarPorTraceId(String traceId);
}
