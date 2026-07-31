package com.fincore.transfer.websocket;

import com.fincore.transfer.entity.Transferencia;

/**
 * Interface para servicios WebSocket.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface WebSocketService {

    void notificarTransferenciaCompletada(Transferencia transferencia);

    void notificarCambioEstado(Transferencia transferencia);

    void notificarError(Transferencia transferencia, String mensajeError);
}
