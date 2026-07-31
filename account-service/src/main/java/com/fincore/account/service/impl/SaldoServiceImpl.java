package com.fincore.account.service.impl;

import com.fincore.account.command.ActualizarSaldoCommand;
import com.fincore.account.command.LiberarReservaCommand;
import com.fincore.account.command.ReservarFondosCommand;
import com.fincore.account.entity.Cuenta;
import com.fincore.account.exception.CuentaBloqueadaException;
import com.fincore.account.exception.CuentaNoEncontradaException;
import com.fincore.account.kafka.AccountEventProducer;
import com.fincore.account.repository.CuentaRepository;
import com.fincore.account.service.SaldoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Implementación del servicio de saldos.
 *
 * Coordenada las actualizaciones de los 4 tipos de saldo:
 * contable, disponible, retenido, proyectado.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Service
@Slf4j
@Transactional
public class SaldoServiceImpl implements SaldoService {

    private final CuentaRepository cuentaRepository;
    private final AccountEventProducer eventProducer;

    public SaldoServiceImpl(CuentaRepository cuentaRepository,
                            AccountEventProducer eventProducer) {
        this.cuentaRepository = cuentaRepository;
        this.eventProducer = eventProducer;
    }

    @Override
    public Cuenta reservarFondos(Long idCuenta, BigDecimal monto, String traceId) {
        log.info("Reservando fondos: cuenta={}, monto={}, traceId={}", idCuenta, monto, traceId);

        Cuenta cuenta = cuentaRepository.findByIdWithLock(idCuenta)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada: " + idCuenta));

        if (!cuenta.esTransferible()) {
            throw new CuentaBloqueadaException("Cuenta no disponible: " + cuenta.getNumeroCuenta());
        }

        cuenta.reservarFondos(monto);
        Cuenta saved = cuentaRepository.save(cuenta);

        eventProducer.publicarSaldoActualizado(saved.getId(), saved.getNumeroCuenta(),
                saved.getSaldoContable(), saved.getSaldoDisponible(), saved.getSaldoRetenido());

        return saved;
    }

    @Override
    public Cuenta liberarRetencion(Long idCuenta, BigDecimal monto, String traceId) {
        log.info("Liberando retención: cuenta={}, monto={}, traceId={}", idCuenta, monto, traceId);

        Cuenta cuenta = cuentaRepository.findByIdWithLock(idCuenta)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada: " + idCuenta));

        cuenta.liberarRetencion(monto);
        Cuenta saved = cuentaRepository.save(cuenta);

        eventProducer.publicarSaldoActualizado(saved.getId(), saved.getNumeroCuenta(),
                saved.getSaldoContable(), saved.getSaldoDisponible(), saved.getSaldoRetenido());

        return saved;
    }

    @Override
    public Cuenta aplicarDebito(Long idCuenta, BigDecimal monto, String traceId) {
        log.info("Aplicando débito: cuenta={}, monto={}, traceId={}", idCuenta, monto, traceId);

        Cuenta cuenta = cuentaRepository.findByIdWithLock(idCuenta)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada: " + idCuenta));

        if (!cuenta.esTransferible()) {
            throw new CuentaBloqueadaException("Cuenta bloqueada: " + cuenta.getNumeroCuenta());
        }

        cuenta.aplicarDebito(monto);
        Cuenta saved = cuentaRepository.save(cuenta);

        eventProducer.publicarSaldoActualizado(saved.getId(), saved.getNumeroCuenta(),
                saved.getSaldoContable(), saved.getSaldoDisponible(), saved.getSaldoRetenido());

        return saved;
    }

    @Override
    public Cuenta aplicarCredito(Long idCuenta, BigDecimal monto, String traceId) {
        log.info("Aplicando crédito: cuenta={}, monto={}, traceId={}", idCuenta, monto, traceId);

        Cuenta cuenta = cuentaRepository.findByIdWithLock(idCuenta)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada: " + idCuenta));

        cuenta.aplicarCredito(monto);
        Cuenta saved = cuentaRepository.save(cuenta);

        eventProducer.publicarSaldoActualizado(saved.getId(), saved.getNumeroCuenta(),
                saved.getSaldoContable(), saved.getSaldoDisponible(), saved.getSaldoRetenido());

        return saved;
    }

    @Override
    public Cuenta aplicarRetencion(Long idCuenta, BigDecimal monto, String traceId) {
        log.info("Aplicando retención: cuenta={}, monto={}, traceId={}", idCuenta, monto, traceId);
        return reservarFondos(idCuenta, monto, traceId);
    }

    @Override
    public Cuenta aplicarLiberacion(Long idCuenta, BigDecimal monto, String traceId) {
        log.info("Aplicando liberación: cuenta={}, monto={}, traceId={}", idCuenta, monto, traceId);
        return liberarRetencion(idCuenta, monto, traceId);
    }

    @Override
    public Cuenta aplicarComision(Long idCuenta, BigDecimal monto, String traceId) {
        log.info("Aplicando comisión: cuenta={}, monto={}, traceId={}", idCuenta, monto, traceId);

        Cuenta cuenta = cuentaRepository.findByIdWithLock(idCuenta)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada: " + idCuenta));

        if (!cuenta.esTransferible()) {
            throw new CuentaBloqueadaException("Cuenta bloqueada: " + cuenta.getNumeroCuenta());
        }

        // Comisión: débito directo al saldo
        cuenta.aplicarDebito(monto);
        Cuenta saved = cuentaRepository.save(cuenta);

        eventProducer.publicarSaldoActualizado(saved.getId(), saved.getNumeroCuenta(),
                saved.getSaldoContable(), saved.getSaldoDisponible(), saved.getSaldoRetenido());

        return saved;
    }

    @Override
    public Cuenta revertirDebito(Long idCuenta, BigDecimal monto, String traceId) {
        log.info("Revirtiendo débito: cuenta={}, monto={}, traceId={}", idCuenta, monto, traceId);

        Cuenta cuenta = cuentaRepository.findByIdWithLock(idCuenta)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada: " + idCuenta));

        cuenta.revertirDebito(monto);
        Cuenta saved = cuentaRepository.save(cuenta);

        eventProducer.publicarSaldoActualizado(saved.getId(), saved.getNumeroCuenta(),
                saved.getSaldoContable(), saved.getSaldoDisponible(), saved.getSaldoRetenido());

        return saved;
    }

    @Override
    public Cuenta revertirCredito(Long idCuenta, BigDecimal monto, String traceId) {
        log.info("Revirtiendo crédito: cuenta={}, monto={}, traceId={}", idCuenta, monto, traceId);

        Cuenta cuenta = cuentaRepository.findByIdWithLock(idCuenta)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada: " + idCuenta));

        cuenta.revertirCredito(monto);
        Cuenta saved = cuentaRepository.save(cuenta);

        eventProducer.publicarSaldoActualizado(saved.getId(), saved.getNumeroCuenta(),
                saved.getSaldoContable(), saved.getSaldoDisponible(), saved.getSaldoRetenido());

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarSaldoSuficiente(Long idCuenta, BigDecimal monto) {
        return cuentaRepository.findById(idCuenta)
                .map(cuenta -> cuenta.getSaldoDisponible().compareTo(monto) >= 0)
                .orElse(false);
    }
}
