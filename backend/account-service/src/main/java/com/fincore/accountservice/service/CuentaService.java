package com.fincore.accountservice.service;

import com.fincore.accountservice.domain.dto.CuentaResponse;
import com.fincore.accountservice.domain.dto.CrearCuentaRequest;
import com.fincore.accountservice.domain.dto.MovimientoRequest;
import com.fincore.accountservice.domain.dto.MovimientoResponse;
import java.util.List;

public interface CuentaService {
    CuentaResponse crear(CrearCuentaRequest request);
    CuentaResponse obtenerPorId(Long id);
    List<CuentaResponse> obtenerTodas();
    MovimientoResponse registrarMovimiento(MovimientoRequest request);
    List<MovimientoResponse> obtenerMovimientosPorCuenta(Long cuentaId);
}