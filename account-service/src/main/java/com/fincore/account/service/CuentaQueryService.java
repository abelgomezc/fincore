package com.fincore.account.service;

import com.fincore.account.query.ObtenerMovimientosQuery;
import com.fincore.account.query.ObtenerSaldoQuery;
import com.fincore.account.dto.response.CuentaResponse;
import com.fincore.account.dto.response.SaldoResponse;

import java.util.List;

/**
 * Interfaz del servicio de consultas de cuentas (CQRS — Query side).
 *
 * Maneja todas las operaciones de lectura:
 * - Obtención de saldos (con caché Redis)
 * - Listado de cuentas por cliente
 * - Historial de movimientos
 * - Consultas de saldo disponible (read model optimizado)
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface CuentaQueryService {

    SaldoResponse obtenerSaldo(ObtenerSaldoQuery query);

    CuentaResponse obtenerCuenta(Long idCuenta);

    CuentaResponse obtenerCuentaPorNumero(String numeroCuenta);

    List<CuentaResponse> obtenerCuentasPorCliente(Long idCliente);

    List<SaldoResponse> obtenerMovimientos(ObtenerMovimientosQuery query);

    boolean validarCuenta(Long idCuenta);
}
