package com.fincore.notificationservice.domain.entity;

import com.fincore.notificationservice.domain.enums.CanalNotificacion;
import com.fincore.notificationservice.domain.enums.EstadoNotificacion;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/*
 * Entidad Notificacion que representa la tabla notificaciones en la base de datos
 * Contiene la información de las notificaciones enviadas a los usuarios de FinCore
 */
@Entity
@Table(name = "notificaciones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal", nullable = false, length = 20)
    private CanalNotificacion canal;

    @Column(name = "destinatario", nullable = false, length = 150)
    private String destinatario;

    @Column(name = "asunto", nullable = false, length = 200)
    private String asunto;

    @Column(name = "cuerpo", nullable = false, length = 1000)
    private String cuerpo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoNotificacion estado;

    @Column(name = "intentos", nullable = false)
    private Integer intentos;

    @Column(name = "respuesta_externa", length = 500)
    private String respuestaExterna;

    @CreatedDate
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
