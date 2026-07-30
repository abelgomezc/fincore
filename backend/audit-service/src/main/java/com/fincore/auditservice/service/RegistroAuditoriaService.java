package com.fincore.auditservice.service;

import com.fincore.auditservice.domain.dto.RegistroAuditoriaRequest;
import com.fincore.auditservice.domain.dto.RegistroAuditoriaResponse;

public interface RegistroAuditoriaService {
    RegistroAuditoriaResponse registrar(RegistroAuditoriaRequest request);
}
