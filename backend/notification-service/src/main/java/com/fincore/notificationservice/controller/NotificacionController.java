package com.fincore.notificationservice.controller;

import com.fincore.notificationservice.domain.dto.NotificacionRequest;
import com.fincore.notificationservice.domain.dto.NotificacionResponse;
import com.fincore.notificationservice.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * Controlador REST para la gestión de notificaciones
 */
@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "API de gestión de notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @Operation(summary = "Crear una nueva notificación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notificación creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<NotificacionResponse> crearNotificacion(@Valid @RequestBody NotificacionRequest request) {
        NotificacionResponse response = notificacionService.crearNotificacion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener todas las notificaciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificaciones obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<NotificacionResponse>> obtenerTodasLasNotificaciones() {
        List<NotificacionResponse> notificaciones = notificacionService.obtenerTodasLasNotificaciones();
        return ResponseEntity.ok(notificaciones);
    }

    @Operation(summary = "Obtener una notificación por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación encontrada"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponse> obtenerNotificacionPorId(
            @Parameter(description = "ID de la notificación", required = true) @PathVariable Long id) {
        return notificacionService.obtenerNotificacionPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener notificaciones por destinatario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificaciones obtenida exitosamente")
    })
    @GetMapping("/destinatario/{destinatario}")
    public ResponseEntity<List<NotificacionResponse>> obtenerNotificacionesPorDestinatario(
            @Parameter(description = "Destinatario de la notificación", required = true) @PathVariable String destinatario) {
        List<NotificacionResponse> notificaciones = notificacionService.obtenerNotificacionesPorDestinatario(destinatario);
        return ResponseEntity.ok(notificaciones);
    }
}
