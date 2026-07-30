package com.fincore.batchservice.service;

import com.fincore.batchservice.dto.EjecucionBatchDTO;

public interface IEjecucionBatchService {

    EjecucionBatchDTO registrarEjecucion(EjecucionBatchDTO dto);
}
