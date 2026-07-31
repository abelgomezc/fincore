package com.fincore.account.service;

import com.fincore.account.command.AbrirCuentaCommand;
import com.fincore.account.command.ActualizarSaldoCommand;
import com.fincore.account.command.BloquearCuentaCommand;
import com.fincore.account.command.LiberarReservaCommand;
import com.fincore.account.command.ReservarFondosCommand;
import com.fincore.account.entity.Cuenta;
import com.fincore.account.enums.EstadoCuenta;
import com.fincore.account.enums.TipoCuentaEnum;
import com.fincore.account.exception.CuentaBloqueadaException;
import com.fincore.account.exception.CuentaNoEncontradaException;
import com.fincore.account.kafka.AccountEventProducer;
import com.fincore.account.repository.CuentaRepository;
import com.fincore.account.repository.TipoCuentaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementación del servicio de comandos de cuentas.
 *
 * Todas las operaciones de escritura pasan por aquí.
 * Usa @Transactional con pessimistic write lock para consistencia.
 * Publica eventos Kafka en cada operación exitosa.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Service
@Slf4j
@Transactional
public class CuentaCommandServiceImpl implements CuentaCommandService {

    private final CuentaRepository cuentaRepository;
    private final TipoCuentaRepository tipoCuentaRepository;
    private final AccountEventProducer eventProducer;

    @Value("${banking.account.number.prefix:2026}")
    private String accountNumberPrefix;

    public CuentaCommandServiceImpl(CuentaRepository cuentaRepository,
                                    TipoCuentaRepository tipoCuentaRepository,
                                    AccountEventProducer eventProducer) {
        this.cuentaRepository = cuentaRepository;
        this.tipoCuentaRepository = tipoCuentaRepository;
        this.eventProducer = eventProducer;
    }

    @Override
    public Cuenta abrirCuenta(AbrirCuentaCommand command) {
        log.info("Aperturando cuenta para cliente: {}", command.getIdCliente());

        var tipoCuenta = tipoCuentaRepository.findByCodigo(command.getTipoCuenta().getCodigo())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de cuenta no encontrado: " + command.getTipoCuenta()));

        String numeroCuenta = generarNumeroCuenta();

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(numeroCuenta);
        cuenta.setIdCliente(command.getIdCliente());
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuenta.setMoneda(command.getMoneda() != null ? command.getMoneda() : "USD");
        cuenta.setSaldoContable(command.getSaldoInicial() != null ? command.getSaldoInicial() : BigDecimal.ZERO);
        cuenta.setSaldoDisponible(command.getSaldoInicial() != null ? command.getSaldoInicial() : BigDecimal.ZERO);
        cuenta.setSaldoRetenido(BigDecimal.ZERO);
        cuenta.setSaldoProyectado(command.getSaldoInicial() != null ? command.getSaldoInicial() : BigDecimal.ZERO);
        cuenta.setFechaApertura(LocalDate.now());

        Cuenta saved = cuentaRepository.save(cuenta);

        eventProducer.publicarCuentaCreada(saved.getId(), saved.getNumeroCuenta(), saved.getIdCliente());
        log.info("Cuenta creada: {} número {}", saved.getId(), saved.getNumeroCuenta());

        return saved;
    }

    @Override
    @Transactional
    public Cuenta bloquearCuenta(BloquearCuentaCommand command) {
        log.info("Bloqueando cuenta: {}", command.getIdCuenta());

        Cuenta cuenta = cuentaRepository.findById(command.getIdCuenta())
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada: " + command.getIdCuenta()));

        cuenta.setEstado(EstadoCuenta.BLOQUEADA);
        cuenta.setMotivoBloqueo(command.getMotivoBloqueo());

        Cuenta saved = cuentaRepository.save(cuenta);
        eventProducer.publicarCuentaBloqueada(saved.getId(), saved.getNumeroCuenta(), command.getMotivoBloqueo());

        return saved;
    }

    @Override
    @Transactional
    public Cuenta reservarFondos(ReservarFondosCommand command) {
        log.info("Reservando fondos: cuenta={}, monto={}", command.getIdCuenta(), command.getMonto());

        Cuenta cuenta = cuentaRepository.findByIdWithLock(command.getIdCuenta())
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada: " + command.getIdCuenta()));

        if (!cuenta.esTransferible()) {
            throw new CuentaBloqueadaException("Cuenta no disponible para reserva: " + cuenta.getNumeroCuenta());
        }

        cuenta.reservarFondos(command.getMonto());
        Cuenta saved = cuentaRepository.save(cuenta);

        eventProducer.publicarSaldoActualizado(saved.getId(), saved.getNumeroCuenta(),
                saved.getSaldoContable(), saved.getSaldoDisponible(), saved.getSaldoRetenido());

        log.info("Fondos reservados: cuenta={}, monto={}", saved.getNumeroCuenta(), command.getMonto());
        return saved;
    }

    @Override
    @Transactional
    public Cuenta liberarReserva(LiberarReservaCommand command) {
        log.info("Liberando reserva: cuenta={}, monto={}", command.getIdCuenta(), command.getMonto());

        Cuenta cuenta = cuentaRepository.findByIdWithLock(command.getIdCuenta())
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada: " + command.getIdCuenta()));

        cuenta.liberarRetencion(command.getMonto());
        Cuenta saved = cuentaRepository.save(cuenta);

        eventProducer.publicarSaldoActualizado(saved.getId(), saved.getNumeroCuenta(),
                saved.getSaldoContable(), saved.getSaldoDisponible(), saved.getSaldoRetenido());

        log.info("Reserva liberada: cuenta={}, monto={}", saved.getNumeroCuenta(), command.getMonto());
        return saved;
    }

    @Override
    @Transactional
    public Cuenta aplicarDebito(ActualizarSaldoCommand command) {
        log.info("Aplicando débito: cuenta={}, monto={}", command.getIdCuenta(), command.getMonto());

        Cuenta cuenta = cuentaRepository.findByIdWithLock(command.getIdCuenta())
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada: " + command.getIdCuenta()));

        if (!cuenta.esTransferible()) {
            throw new CuentaBloqueadaException("Cuenta bloqueada: " + cuenta.getNumeroCuenta());
        }

        if ("RETENCION".equals(command.getTipoMovimiento())) {
            cuenta.reservarFondos(command.getMonto());
        } else {
            cuenta.aplicarDebito(command.getMonto());
        }

        Cuenta saved = cuentaRepository.save(cuenta);
        eventProducer.publicarSaldoActualizado(saved.getId(), saved.getNumeroCuenta(),
                saved.getSaldoContable(), saved.getSaldoDisponible(), saved.getSaldoRetenido());

        return saved;
    }

    @Override
    @Transactional
    public Cuenta aplicarCredito(ActualizarSaldoCommand command) {
        log.info("Aplicando crédito: cuenta={}, monto={}", command.getIdCuenta(), command.getMonto());

        Cuenta cuenta = cuentaRepository.findByIdWithLock(command.getIdCuenta())
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada: " + command.getIdCuenta()));

        if ("LIBERACION".equals(command.getTipoMovimiento())) {
            cuenta.liberarRetencion(command.getMonto());
        } else {
            cuenta.aplicarCredito(command.getMonto());
        }

        Cuenta saved = cuentaRepository.save(cuenta);
        eventProducer.publicarSaldoActualizado(saved.getId(), saved.getNumeroCuenta(),
                saved.getSaldoContable(), saved.getSaldoDisponible(), saved.getSaldoRetenido());

        return saved;
    }

    @Override
    @Transactional
    public Cuenta revertirDebito(ActualizarSaldoCommand command) {
        log.info("Revirtiendo débito: cuenta={}, monto={}", command.getIdCuenta(), command.getMonto());

        Cuenta cuenta = cuentaRepository.findByIdWithLock(command.getIdCuenta())
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada: " + command.getIdCuenta()));

        cuenta.revertirDebito(command.getMonto());
        Cuenta saved = cuentaRepository.save(cuenta);

        eventProducer.publicarSaldoActualizado(saved.getId(), saved.getNumeroCuenta(),
                saved.getSaldoContable(), saved.getSaldoDisponible(), saved.getSaldoRetenido());

        return saved;
    }

    @Override
    @Transactional
    public Cuenta revertirCredito(ActualizarSaldoCommand command) {
        log.info("Revirtiendo crédito: cuenta={}, monto={}", command.getIdCuenta(), command.getMonto());

        Cuenta cuenta = cuentaRepository.findByIdWithLock(command.getIdCuenta())
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada: " + command.getIdCuenta()));

        cuenta.revertirCredito(command.getMonto());
        Cuenta saved = cuentaRepository.save(cuenta);

        eventProducer.publicarSaldoActualizado(saved.getId(), saved.getNumeroCuenta(),
                saved.getSaldoContable(), saved.getSaldoDisponible(), saved.getSaldoRetenido());

        return saved;
    }

    private String generarNumeroCuenta() {
        long random = ThreadLocalRandom.current().nextLong(1000000000L, 9999999999L);
        return accountNumberPrefix + random;
    }
}
