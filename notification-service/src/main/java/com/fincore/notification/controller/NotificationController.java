package com.fincore.notification.controller;

import com.fincore.notification.entity.Notificacion;
import com.fincore.notification.repository.NotificacionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para consultar notificaciones.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@RestController
@RequestMapping("/api/notificaciones")
public class NotificationController {

    private final NotificacionRepository notificacionRepository;

    public NotificationController(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<Notificacion> getNotificacionesByUsuario(@PathVariable String idUsuario,
                                                         @RequestParam(defaultValue = "20") int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        List<Notificacion> notificaciones = notificacionRepository.findByIdUsuarioOrderByFechaCreacionDesc(idUsuario);
        return notificaciones.stream().limit(limit).toList();
    }

    @GetMapping("/usuario/{idUsuario}/paged")
    public ResponseEntity<Page<Notificacion>> getNotificacionesPaged(
            @PathVariable String idUsuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        Page<Notificacion> notificaciones = notificacionRepository.findByIdUsuario(idUsuario, pageable);
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/transferencia/{idTransferencia}")
    public List<Notificacion> getNotificacionesByTransferencia(@PathVariable Long idTransferencia) {
        return notificacionRepository.findByIdTransferencia(idTransferencia);
    }

    @PostMapping("/test")
    public ResponseEntity<String> enviarNotificacionTest(
            @RequestParam String idUsuario,
            @RequestParam String titulo,
            @RequestParam String mensaje) {
        return ResponseEntity.ok("Notificación de prueba programada para usuario: " + idUsuario);
    }
}
