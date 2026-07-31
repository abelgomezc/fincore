package com.fincore.ledger.service;

import com.fincore.ledger.dto.AsientoDTO;
import com.fincore.ledger.dto.LineaAsientoDTO;
import com.fincore.ledger.dto.response.AsientoResponse;
import com.fincore.ledger.dto.response.BalanceGeneralResponse;
import com.fincore.ledger.dto.response.EstadoCuentaResponse;
import com.fincore.ledger.dto.response.ExtractoResponse;
import com.fincore.ledger.entity.AsientoContable;
import com.fincore.ledger.entity.LineaAsiento;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz del servicio de ledger.
 *
 * El ledger es el núcleo contable del banco. Gestiona:
 * - Creación de asientos contables (doble partida)
 * - Validación de equilibrio (débitos = créditos)
 * - Reversión de asientos
 * - Estados de cuenta
 * - Extractos contables
 * - Balance general
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface LedgerService {

    AsientoResponse crearAsiento(AsientoDTO dto);

    AsientoResponse crearAsiento(List<LineaAsientoDTO> lineas, String descripcion,
                                 String tipoReferencia, Long idReferencia,
                                 String idUsuario, String ipOrigen, String traceId);

    AsientoContable obtenerAsiento(Long idAsiento);

    AsientoContable obtenerAsientoPorNumero(String numeroAsiento);

    List<LineaAsiento> obtenerLineasDeAsiento(Long idAsiento);

    AsientoResponse reversarAsiento(String numeroAsiento, String descripcion, String idUsuario, String traceId);

    EstadoCuentaResponse obtenerEstadoCuenta(String codigoCuenta);

    EstadoCuentaResponse obtenerEstadoCuentaBancaria(Long idCuentaBancaria);

    ExtractoResponse obtenerExtracto(Long idCuentaBancaria, LocalDate desde, LocalDate hasta);

    ExtractoResponse obtenerExtractoPorCodigo(String codigoCuenta, LocalDate desde, LocalDate hasta);

    BalanceGeneralResponse verificarEquilibrio();

    BalanceGeneralResponse obtenerBalanceGeneral();

    List<AsientoContable> obtenerAsientosPorReferencia(Long idReferencia, String tipoReferencia);
}
