package com.fincore.auditservice.service.impl;

import com.fincore.auditservice.domain.dto.RegistroAuditoriaRequest;
import com.fincore.auditservice.domain.dto.RegistroAuditoriaResponse;
import com.fincore.auditservice.domain.entity.RegistroAuditoria;
import com.fincore.auditservice.repository.RegistroAuditoriaRepository;
import com.fincore.auditservice.service.RegistroAuditoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistroAuditoriaServiceImpl implements RegistroAuditoriaService {

    private final RegistroAuditoriaRepository registroAuditoriaRepository;

    @Override
    @Transactional
    public RegistroAuditoriaResponse registrar(RegistroAuditoriaRequest request) {
        log.info("Registrando auditoria para entidad: {} - id: {}", request.getEntidadTipo(), request.getEntidadId());

        RegistroAuditoria entidad = RegistroAuditoria.builder()
                .entidadTipo(request.getEntidadTipo())
                .entidadId(request.getEntidadId())
                .accion(request.getAccion())
                .estadoAnterior(request.getEstadoAnterior())
                .estadoNuevo(request.getEstadoNuevo())
                .usuarioTipo(request.getUsuarioTipo())
                .usuarioId(request.getUsuarioId())
                .ipAddress(request.getIpAddress())
                .traceId(request.getTraceId())
                .build();

        RegistroAuditoria guardado = registroAuditoriaRepository.save(entidad);
        return mapToResponse(guardado);
    }

    private RegistroAuditoriaResponse mapToResponse(RegistroAuditoria entidad) {
        return RegistroAuditoriaResponse.builder()
                .id(entidad.getId())
                .entidadTipo(entidad.getEntidadTipo())
                .entidadId(entidad.getEntidadId())
                .accion(entidad.getAccion())
                .estadoAnterior(entidad.getEstadoAnterior())
                .estadoNuevo(entidad.getEstadoNuevo())
                .usuarioTipo(entidad.getUsuarioTipo())
                .usuarioId(entidad.getUsuarioId())
                .ipAddress(entidad.getIpAddress())
                .traceId(entidad.getTraceId())
                .creadoEn(entidad.getCreadoEn())
                .build();
    }
}
