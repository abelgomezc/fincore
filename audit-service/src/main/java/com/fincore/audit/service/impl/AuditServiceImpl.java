package com.fincore.audit.service.impl;

import com.fincore.audit.entity.RegistroAuditoria;
import com.fincore.audit.entity.EventoSaga;
import com.fincore.audit.enums.AccionAuditoria;
import com.fincore.audit.enums.ResultadoAuditoria;
import com.fincore.audit.repository.RegistroAuditoriaRepository;
import com.fincore.audit.repository.EventoSagaRepository;
import com.fincore.audit.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de auditoría.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Service
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final RegistroAuditoriaRepository auditoriaRepository;
    private final EventoSagaRepository eventoSagaRepository;
    private final ObjectMapper objectMapper;

    public AuditServiceImpl(RegistroAuditoriaRepository auditoriaRepository,
                            EventoSagaRepository eventoSagaRepository,
                            ObjectMapper objectMapper) {
        this.auditoriaRepository = auditoriaRepository;
        this.eventoSagaRepository = eventoSagaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void registrarEvento(String traceId, String servicio, String endpoint, String metodoHttp,
                                String idUsuario, String rolUsuario, String ipOrigen,
                                String idRecurso, String tipoRecurso, AccionAuditoria accion,
                                ResultadoAuditoria resultado, String requestBody,
                                Integer responseCodigo, Integer tiempoMs, String detalle) {
        RegistroAuditoria registro = new RegistroAuditoria();
        registro.setTraceId(traceId);
        registro.setServicio(servicio);
        registro.setEndpoint(endpoint);
        registro.setMetodoHttp(metodoHttp);
        registro.setIdUsuario(idUsuario);
        registro.setRolUsuario(rolUsuario);
        registro.setIpOrigen(ipOrigen);
        registro.setIdRecurso(idRecurso);
        registro.setTipoRecurso(tipoRecurso);
        registro.setAccion(accion.getCodigo());
        registro.setResultado(resultado.name());
        registro.setRequestBody(requestBody);
        registro.setResponseCodigo(responseCodigo);
        registro.setTiempoRespuestaMs(tiempoMs);
        registro.setDetalle(detalle);
        registro.setFechaCreacion(LocalDateTime.now());

        auditoriaRepository.save(registro);

        log.debug("Evento auditado registrado: traceId={}, servicio={}, accion={}, resultado={}",
                traceId, servicio, accion, resultado);
    }

    @Override
    public Iterable<RegistroAuditoria> buscarPorTraceId(String traceId) {
        List<RegistroAuditoria> registros = auditoriaRepository.findByTraceId(traceId);
        log.info("Historial auditivo encontrado: traceId={}, {} registros", traceId, registros.size());
        return registros;
    }
}
