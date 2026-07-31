package com.fincore.transfer.websocket;

import com.fincore.transfer.entity.Transferencia;
import com.fincore.transfer.dto.response.TransferenciaResponse;
import com.fincore.transfer.dto.response.TransferenciaResponse.EstadoTransferenciaDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuración y handler de WebSocket para notificaciones de transferencias.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig extends TextWebSocketHandler implements WebSocketConfigurer, WebSocketService {

    private final java.util.Map<String, org.springframework.web.socket.WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(this, "/ws/transferencias").setAllowedOrigins("*");
    }

    @Override
    public void afterConnectionEstablished(org.springframework.web.socket.WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] kv = param.split("=");
                if ("idUsuario".equals(kv[0])) {
                    session.getAttributes().put("idUsuario", kv[1]);
                    break;
                }
            }
        }
        sessions.put(session.getId(), session);
        log.info("WebSocket conectado: session={}, usuario={}",
                session.getId(), session.getAttributes().get("idUsuario"));
    }

    @Override
    public void afterConnectionClosed(org.springframework.web.socket.WebSocketSession session,
                                      org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        log.info("WebSocket desconectado: session={}", session.getId());
    }

    @Override
    public void notificarTransferenciaCompletada(Transferencia transferencia) {
        try {
            String json = objectMapper.writeValueAsString(convertToResponse(transferencia));
            broadcastToUser(transferencia.getIdUsuario(), json);
            log.info("Notificación WebSocket completada enviada: transferencia={}",
                    transferencia.getNumeroTransferencia());
        } catch (Exception e) {
            log.error("Error enviando notificación WebSocket: {}", e.getMessage(), e);
        }
    }

    @Override
    public void notificarCambioEstado(Transferencia transferencia) {
        try {
            String json = objectMapper.writeValueAsString(convertToResponse(transferencia));
            broadcastToUser(transferencia.getIdUsuario(), json);
            log.info("Notificación WebSocket cambio estado enviada: transferencia={}, estado={}",
                    transferencia.getNumeroTransferencia(), transferencia.getEstado());
        } catch (Exception e) {
            log.error("Error enviando notificación WebSocket: {}", e.getMessage(), e);
        }
    }

    @Override
    public void notificarError(Transferencia transferencia, String mensajeError) {
        try {
            TransferenciaResponse response = convertToResponse(transferencia);
            String json = objectMapper.writeValueAsString(response);
            broadcastToUser(transferencia.getIdUsuario(), json);
            log.info("Notificación WebSocket error enviada: transferencia={}, error={}",
                    transferencia.getNumeroTransferencia(), mensajeError);
        } catch (Exception e) {
            log.error("Error enviando notificación WebSocket de error: {}", e.getMessage(), e);
        }
    }

    private void broadcastToUser(String idUsuario, String message) {
        if (idUsuario == null) return;
        sessions.values().stream()
                .filter(session -> idUsuario.equals(session.getAttributes().get("idUsuario")))
                .forEach(session -> {
                    if (session.isOpen()) {
                        try {
                            session.sendMessage(new org.springframework.web.socket.TextMessage(message));
                        } catch (Exception e) {
                            log.error("Error enviando mensaje WebSocket: {}", e.getMessage(), e);
                        }
                    }
                });
    }

    private TransferenciaResponse convertToResponse(Transferencia t) {
        TransferenciaResponse response = new TransferenciaResponse();
        response.setId(t.getId());
        response.setNumeroTransferencia(t.getNumeroTransferencia());
        response.setIdCuentaOrigen(t.getIdCuentaOrigen());
        response.setNumeroCuentaOrigen(t.getNumeroCuentaOrigen());
        response.setIdCuentaDestino(t.getIdCuentaDestino());
        response.setNumeroCuentaDestino(t.getNumeroCuentaDestino());
        response.setNombreBeneficiario(t.getNombreBeneficiario());
        response.setMonto(t.getMonto());
        response.setMoneda(t.getMoneda());
        response.setComision(t.getComision());
        response.setConcepto(t.getConcepto());
        response.setEstado(t.getEstado());
        response.setPasoSagaActual(t.getPasoSagaActual());
        response.setIntentosSaga(t.getIntentosSaga());
        response.setScoreFraude(t.getScoreFraude());
        response.setDecisionFraude(t.getDecisionFraude());
        response.setIdUsuario(t.getIdUsuario());
        response.setIpOrigen(t.getIpOrigen());
        response.setDispositivo(t.getDispositivo());
        response.setTraceId(t.getTraceId());
        response.setFechaIniciada(t.getFechaIniciada());
        response.setFechaCompletada(t.getFechaCompletada());
        response.setFechaRevertida(t.getFechaRevertida());
        response.setMotivoRechazo(t.getMotivoRechazo());
        response.setHistorialEstados(List.of());
        return response;
    }
}
