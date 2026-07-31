package com.fincore.account.service.impl;

import com.fincore.account.dto.response.CuentaResponse;
import com.fincore.account.dto.response.SaldoResponse;
import com.fincore.account.entity.Cuenta;
import com.fincore.account.query.ObtenerMovimientosQuery;
import com.fincore.account.query.ObtenerSaldoQuery;
import com.fincore.account.repository.CuentaRepository;
import com.fincore.account.service.CuentaQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de consultas de cuentas (CQRS — Query side).
 *
 * Usa caché Redis (TTL 300s) para consultas frecuentes de saldos.
 * El read model está optimizado para lecturas rápidas.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class CuentaQueryServiceImpl implements CuentaQueryService {

    private final CuentaRepository cuentaRepository;

    public CuentaQueryServiceImpl(CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    @Override
    @Cacheable(value = "saldos", key = "#query.idCuenta")
    public SaldoResponse obtenerSaldo(ObtenerSaldoQuery query) {
        log.debug("Obteniendo saldo para cuenta: {}", query.getIdCuenta());

        Cuenta cuenta;
        if (query.getIdCuenta() != null) {
            cuenta = cuentaRepository.findById(query.getIdCuenta())
                    .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + query.getIdCuenta()));
        } else if (query.getNumeroCuenta() != null) {
            cuenta = cuentaRepository.findByNumeroCuenta(query.getNumeroCuenta())
                    .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + query.getNumeroCuenta()));
        } else {
            throw new IllegalArgumentException("Debe especificar idCuenta o numeroCuenta");
        }

        return toSaldoResponse(cuenta);
    }

    @Override
    @Cacheable(value = "cuentas", key = "#idCuenta")
    public CuentaResponse obtenerCuenta(Long idCuenta) {
        log.debug("Obteniendo cuenta: {}", idCuenta);

        Cuenta cuenta = cuentaRepository.findById(idCuenta)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + idCuenta));

        return toCuentaResponse(cuenta);
    }

    @Override
    public CuentaResponse obtenerCuentaPorNumero(String numeroCuenta) {
        log.debug("Obteniendo cuenta por número: {}", numeroCuenta);

        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + numeroCuenta));

        return toCuentaResponse(cuenta);
    }

    @Override
    public List<CuentaResponse> obtenerCuentasPorCliente(Long idCliente) {
        log.debug("Obteniendo cuentas para cliente: {}", idCliente);
        List<Cuenta> cuentas = cuentaRepository.findByIdCliente(idCliente);
        return cuentas.stream()
                .map(this::toCuentaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SaldoResponse> obtenerMovimientos(ObtenerMovimientosQuery query) {
        log.debug("Obteniendo movimientos para cuenta: {}", query.getIdCuenta());
        // Los movimientos reales vienen del ledger-service.
        // Aquí se retorna el snapshot de saldos históricos si existe.
        // En una implementación completa, se consumiría del ledger via gRPC.
        Cuenta cuenta = cuentaRepository.findById(query.getIdCuenta())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + query.getIdCuenta()));

        return List.of(toSaldoResponse(cuenta));
    }

    @Override
    public boolean validarCuenta(Long idCuenta) {
        if (idCuenta == null || idCuenta <= 0) {
            throw new InvalidDataAccessApiUsageException("ID de cuenta inválido: " + idCuenta);
        }
        return cuentaRepository.findById(idCuenta)
                .map(cuenta -> cuenta.esTransferible())
                .orElse(false);
    }

    private SaldoResponse toSaldoResponse(Cuenta cuenta) {
        return SaldoResponse.builder()
                .idCuenta(cuenta.getId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .saldoContable(cuenta.getSaldoContable())
                .saldoDisponible(cuenta.getSaldoDisponible())
                .saldoRetenido(cuenta.getSaldoRetenido())
                .saldoProyectado(cuenta.getSaldoProyectado())
                .moneda(cuenta.getMoneda())
                .estado(cuenta.getEstado().name())
                .fechaActualizacion(cuenta.getFechaActualizacion() != null
                        ? cuenta.getFechaActualizacion().toString() : null)
                .build();
    }

    private CuentaResponse toCuentaResponse(Cuenta cuenta) {
        return CuentaResponse.builder()
                .id(cuenta.getId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .idCliente(cuenta.getIdCliente())
                .tipoCuenta(cuenta.getTipoCuenta() != null ? cuenta.getTipoCuenta().getCodigo() : null)
                .codigoMoneda(cuenta.getMoneda())
                .estado(cuenta.getEstado())
                .saldoContable(cuenta.getSaldoContable())
                .saldoDisponible(cuenta.getSaldoDisponible())
                .saldoRetenido(cuenta.getSaldoRetenido())
                .saldoProyectado(cuenta.getSaldoProyectado())
                .moneda(cuenta.getMoneda())
                .fechaApertura(cuenta.getFechaApertura() != null ? cuenta.getFechaApertura().toString() : null)
                .fechaUltimoMovimiento(cuenta.getFechaUltimoMovimiento() != null
                        ? cuenta.getFechaUltimoMovimiento().toString() : null)
                .motivoBloqueo(cuenta.getMotivoBloqueo())
                .build();
    }
}
