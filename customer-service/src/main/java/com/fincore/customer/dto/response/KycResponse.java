package com.fincore.customer.dto.response;

import com.fincore.customer.enums.EstadoKyc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta con el estado de KYC del cliente.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycResponse {

    private Long id;
    private Long idCliente;
    private EstadoKyc estado;
    private String fechaVerificacion;
    private String verificadoPor;
    private String observaciones;
    private String fechaCreacion;
}
