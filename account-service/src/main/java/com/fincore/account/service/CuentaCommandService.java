package com.fincore.account.service;

import com.fincore.account.command.AbrirCuentaCommand;
import com.fincore.account.command.ActualizarSaldoCommand;
import com.fincore.account.command.BloquearCuentaCommand;
import com.fincore.account.command.LiberarReservaCommand;
import com.fincore.account.command.ReservarFondosCommand;
import com.fincore.account.entity.Cuenta;

/**
 * Interfaz del servicio de comandos de cuentas (CQRS — Command side).
 *
 * Maneja todas las operaciones de escritura:
 * - Apertura de cuentas
 * - Bloqueo de cuentas
 * - Reserva y liberación de fondos
 * - Actualización de saldos
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface CuentaCommandService {

    Cuenta abrirCuenta(AbrirCuentaCommand command);

    Cuenta bloquearCuenta(BloquearCuentaCommand command);

    Cuenta reservarFondos(ReservarFondosCommand command);

    Cuenta liberarReserva(LiberarReservaCommand command);

    Cuenta aplicarDebito(ActualizarSaldoCommand command);

    Cuenta aplicarCredito(ActualizarSaldoCommand command);

    Cuenta revertirDebito(ActualizarSaldoCommand command);

    Cuenta revertirCredito(ActualizarSaldoCommand command);
}
