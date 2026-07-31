package com.fincore.ledger.service;

import com.fincore.ledger.dto.LineaAsientoDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Fábrica de asientos contables.
 *
 * Crea asientos para cada tipo de operación financiera:
 * - Depósito inicial
 * - Transferencia (débito + crédito)
 * - Retención de fondos
 * - Liberación de retención
 * - Comisión por transferencia
 * - Reversión de transferencia
 * - Intereses (batch nocturno)
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface AsientoFactory {

    List<LineaAsientoDTO> crearAsientoDeposito(Long idCuenta, BigDecimal monto);

    List<LineaAsientoDTO> crearAsientoTransferencia(Long idCuentaOrigen, Long idCuentaDestino, BigDecimal monto);

    List<LineaAsientoDTO> crearAsientoRetencion(Long idCuentaOrigen, BigDecimal monto);

    List<LineaAsientoDTO> crearAsientoLiberacion(Long idCuentaDestino, BigDecimal monto);

    List<LineaAsientoDTO> crearAsientoComision(Long idCuentaOrigen, BigDecimal monto);

    List<LineaAsientoDTO> crearAsientoReversionTransferencia(Long idCuentaOrigen, Long idCuentaDestino, BigDecimal monto);

    List<LineaAsientoDTO> crearAsientoIntereses(Long idCuenta, BigDecimal monto);
}
