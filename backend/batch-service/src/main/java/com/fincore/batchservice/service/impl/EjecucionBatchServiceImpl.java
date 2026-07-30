package com.fincore.batchservice.service.impl;

import com.fincore.batchservice.dto.EjecucionBatchDTO;
import com.fincore.batchservice.entity.EjecucionBatch;
import com.fincore.batchservice.enums.EstadoEjecucion;
import com.fincore.batchservice.repository.EjecucionBatchRepository;
import com.fincore.batchservice.service.IEjecucionBatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EjecucionBatchServiceImpl implements IEjecucionBatchService {

    private final EjecucionBatchRepository ejecucionBatchRepository;

    @Override
    @Transactional
    public EjecucionBatchDTO registrarEjecucion(EjecucionBatchDTO dto) {
        EjecucionBatch entity = EjecucionBatch.builder()
                .nombreJob(dto.getNombreJob())
                .estado(dto.getEstado() != null ? dto.getEstado().name() : EstadoEjecucion.INICIADO.name())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .registrosProcesados(dto.getRegistrosProcesados())
                .registrosFallidos(dto.getRegistrosFallidos())
                .mensajeError(dto.getMensajeError())
                .build();

        EjecucionBatch saved = ejecucionBatchRepository.save(entity);
        log.info("Ejecucion batch registrada con id {}", saved.getId());

        return EjecucionBatchDTO.builder()
                .id(saved.getId())
                .nombreJob(saved.getNombreJob())
                .estado(EstadoEjecucion.valueOf(saved.getEstado()))
                .fechaInicio(saved.getFechaInicio())
                .fechaFin(saved.getFechaFin())
                .registrosProcesados(saved.getRegistrosProcesados())
                .registrosFallidos(saved.getRegistrosFallidos())
                .mensajeError(saved.getMensajeError())
                .build();
    }
}
