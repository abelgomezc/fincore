package com.fincore.transferservice.service;

import com.fincore.transferservice.domain.dto.IniciarTransferenciaRequest;
import com.fincore.transferservice.domain.dto.TransferenciaResponse;
import java.util.List;

public interface TransferService {

    TransferenciaResponse iniciarTransferencia(IniciarTransferenciaRequest request);

    TransferenciaResponse obtenerTransferencia(Long id);

    List<TransferenciaResponse> listarTransferencias();
}