package com.fincore.accountservice.service.impl;

import com.fincore.accountservice.domain.dto.CrearCuentaRequest;
import com.fincore.accountservice.domain.dto.CuentaResponse;
import com.fincore.accountservice.domain.dto.MovimientoRequest;
import com.fincore.accountservice.domain.dto.MovimientoResponse;
import com.fincore.accountservice.domain.entity.Cuenta;
import com.fincore.accountservice.domain.entity.Movimiento;
import com.fincore.accountservice.repository.CuentaRepository;
import com.fincore.accountservice.repository.MovimientoRepository;
import com.fincore.accountservice.service.CuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;

    @Override
    @Transactional
    public CuentaResponse crear(CrearCuentaRequest request) {
        Cuenta entity = Cuenta.builder()
                .numeroCuenta(request.getNumeroCuenta())
                .tipoCuenta(request.getTipoCuenta())
                .moneda(request.getMoneda())
                .saldoContable(request.getSaldoContable())
                .saldoDisponible(request.getSaldoDisponible())
                .saldoRetenido(request.getSaldoRetenido())
                .saldoProyectado(request.getSaldoProyectado())
                .estado(request.getEstado())
                .build();
        Cuenta guardada = cuentaRepository.save(entity);
        return mapearACuentaResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaResponse obtenerPorId(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));
        return mapearACuentaResponse(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaResponse> obtenerTodas() {
        return cuentaRepository.findAll().stream()
                .map(this::mapearACuentaResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MovimientoResponse registrarMovimiento(MovimientoRequest request) {
        Cuenta cuenta = cuentaRepository.findById(request.getCuentaId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

        Movimiento movimiento = Movimiento.builder()
                .cuentaId(request.getCuentaId())
                .tipoMovimiento(request.getTipoMovimiento())
                .monto(request.getMonto())
                .moneda(request.getMoneda())
                .descripcion(request.getDescripcion())
                .saldoAnterior(request.getSaldoAnterior())
                .saldoPosterior(request.getSaldoPosterior())
                .referencia(request.getReferencia())
                .estado(request.getEstado())
                .createdBy(request.getCreatedBy() != null ? request.getCreatedBy() : "system")
                .build();
        Movimiento guardado = movimientoRepository.save(movimiento);
        return mapearAMovimientoResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> obtenerMovimientosPorCuenta(Long cuentaId) {
        return movimientoRepository.findByCuentaIdOrderByCreatedAtDesc(cuentaId).stream()
                .map(this::mapearAMovimientoResponse)
                .collect(Collectors.toList());
    }

    private CuentaResponse mapearACuentaResponse(Cuenta cuenta) {
        return CuentaResponse.builder()
                .id(cuenta.getId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta())
                .moneda(cuenta.getMoneda())
                .saldoContable(cuenta.getSaldoContable())
                .saldoDisponible(cuenta.getSaldoDisponible())
                .saldoRetenido(cuenta.getSaldoRetenido())
                .saldoProyectado(cuenta.getSaldoProyectado())
                .estado(cuenta.getEstado())
                .version(cuenta.getVersion())
                .createdAt(cuenta.getCreatedAt())
                .updatedAt(cuenta.getUpdatedAt())
                .build();
    }

    private MovimientoResponse mapearAMovimientoResponse(Movimiento movimiento) {
        return MovimientoResponse.builder()
                .id(movimiento.getId())
                .cuentaId(movimiento.getCuentaId())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .monto(movimiento.getMonto())
                .moneda(movimiento.getMoneda())
                .descripcion(movimiento.getDescripcion())
                .saldoAnterior(movimiento.getSaldoAnterior())
                .saldoPosterior(movimiento.getSaldoPosterior())
                .referencia(movimiento.getReferencia())
                .estado(movimiento.getEstado())
                .version(movimiento.getVersion())
                .createdAt(movimiento.getCreatedAt())
                .createdBy(movimiento.getCreatedBy())
                .updatedAt(movimiento.getUpdatedAt())
                .build();
    }
}