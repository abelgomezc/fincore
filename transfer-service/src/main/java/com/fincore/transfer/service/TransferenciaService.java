package com.fincore.transfer.service;

import com.fincore.transfer.dto.request.CrearTransferenciaRequest;
import com.fincore.transfer.dto.response.TransferenciaResponse;
import com.fincore.transfer.enums.EstadoTransferencia;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Interface del servicio de transferencias.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface TransferenciaService {

    TransferenciaResponse crearTransferencia(CrearTransferenciaRequest request, String idUsuario, String ipOrigen);

    TransferenciaResponse obtenerTransferencia(Long id);

    TransferenciaResponse obtenerTransferenciaPorNumero(String numeroTransferencia);

    Page<TransferenciaResponse> listarTransferenciasPorUsuario(String idUsuario, int page, int size);

    List<TransferenciaResponse> listarPorEstado(EstadoTransferencia estado);

    TransferenciaResponse revertirTransferencia(Long id, String motivo);
}
