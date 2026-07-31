package com.fincore.account.service;

import com.fincore.account.command.ActualizarSaldoCommand;
import com.fincore.account.command.LiberarReservaCommand;
import com.fincore.account.command.ReservarFondosCommand;
import com.fincore.account.entity.Cuenta;

import java.math.BigDecimal;

/**
 * Interfaz del servicio de saldos.
 *
 * Lógica crítica de actualización de los 4 tipos de saldo.
 * Valida saldo suficiente antes de reservar.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface SaldoService {

    Cuenta reservarFondos(Long idCuenta, BigDecimal monto, String traceId);

    Cuenta liberarRetencion(Long idCuenta, BigDecimal monto, String traceId);

    Cuenta aplicarDebito(Long idCuenta, BigDecimal monto, String traceId);

    Cuenta aplicarCredito(Long idCuenta, BigDecimal monto, String traceId);

    Cuenta aplicarRetencion(Long idCuenta, BigDecimal monto, String traceId);

    Cuenta aplicarLiberacion(Long idCuenta, BigDecimal monto, String traceId);

    Cuenta aplicarComision(Long idCuenta, BigDecimal monto, String traceId);

    Cuenta revertirDebito(Long idCuenta, BigDecimal monto, String traceId);

    Cuenta revertirCredito(Long idCuenta, BigDecimal monto, String traceId);

    boolean validarSaldoSuficiente(Long idCuenta, BigDecimal monto);
}
