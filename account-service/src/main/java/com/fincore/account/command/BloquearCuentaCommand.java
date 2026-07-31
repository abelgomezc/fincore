package com.fincore.account.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Comando para bloquear una cuenta.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloquearCuentaCommand {

    @NotNull(message = "El ID de la cuenta es obligatorio")
    private Long idCuenta;

    @NotBlank(message = "El motivo del bloqueo es obligatorio")
    private String motivoBloqueo;

    private String emailUsuario;
    private String ipOrigen;
    private String traceId;
}
