package com.fincore.ledger.service.impl;

import com.fincore.ledger.dto.LineaAsientoDTO;
import com.fincore.ledger.service.AsientoFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la fábrica de asientos contables.
 *
 * Cada método genera las líneas de asiento (débito/crédito) para
 * una operación bancaria específica, respetando la ecuación
 * contable: débitos = créditos.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
public class AsientoFactoryImpl implements AsientoFactory {

    // Códigos de cuentas contables del plan de cuentas
    private static final String CAJA = "1001";
    private static final String DEPOSITOS_CORRIENTE = "2001";
    private static final String DEPOSITOS_AHORROS = "2002";
    private static final String DEPOSITOS_PLAZO = "2010";
    private static final String FONDOS_TRANSITO = "2100";
    private static final String RETENCIONES = "2200";
    private static final String INGRESOS_COMISIONES = "4020";
    private static final String GASTOS_OPERATIVOS = "5010";

    @Override
    public List<LineaAsientoDTO> crearAsientoDeposito(Long idCuenta, BigDecimal monto) {
        List<LineaAsientoDTO> lineas = new ArrayList<>();
        lineas.add(LineaAsientoDTO.builder()
                .codigoCuenta(CAJA)
                .idCuentaBancaria(idCuenta)
                .tipoMovimiento("DEBITO")
                .monto(monto)
                .descripcion("Depósito inicial en apertura de cuenta")
                .build());
        lineas.add(LineaAsientoDTO.builder()
                .codigoCuenta(DEPOSITOS_AHORROS)
                .idCuentaBancaria(idCuenta)
                .tipoMovimiento("CREDITO")
                .monto(monto)
                .descripcion("Depósito inicial en apertura de cuenta")
                .build());
        return lineas;
    }

    @Override
    public List<LineaAsientoDTO> crearAsientoTransferencia(Long idCuentaOrigen, Long idCuentaDestino, BigDecimal monto) {
        List<LineaAsientoDTO> lineas = new ArrayList<>();

        lineas.add(LineaAsientoDTO.builder()
                .codigoCuenta(DEPOSITOS_AHORROS)
                .idCuentaBancaria(idCuentaOrigen)
                .tipoMovimiento("DEBITO")
                .monto(monto)
                .descripcion("Débito por transferencia enviada")
                .build());

        lineas.add(LineaAsientoDTO.builder()
                .codigoCuenta(DEPOSITOS_AHORROS)
                .idCuentaBancaria(idCuentaDestino)
                .tipoMovimiento("CREDITO")
                .monto(monto)
                .descripcion("Crédito por transferencia recibida")
                .build());

        return lineas;
    }

    @Override
    public List<LineaAsientoDTO> crearAsientoRetencion(Long idCuentaOrigen, BigDecimal monto) {
        List<LineaAsientoDTO> lineas = new ArrayList<>();

        lineas.add(LineaAsientoDTO.builder()
                .codigoCuenta(DEPOSITOS_AHORROS)
                .idCuentaBancaria(idCuentaOrigen)
                .tipoMovimiento("DEBITO")
                .monto(monto)
                .descripcion("Retención de fondos — transferencia en proceso")
                .build());

        lineas.add(LineaAsientoDTO.builder()
                .codigoCuenta(FONDOS_TRANSITO)
                .idCuentaBancaria(idCuentaOrigen)
                .tipoMovimiento("CREDITO")
                .monto(monto)
                .descripcion("Fondos en tránsito — retención de transferencia")
                .build());

        return lineas;
    }

    @Override
    public List<LineaAsientoDTO> crearAsientoLiberacion(Long idCuentaDestino, BigDecimal monto) {
        List<LineaAsientoDTO> lineas = new ArrayList<>();

        lineas.add(LineaAsientoDTO.builder()
                .codigoCuenta(FONDOS_TRANSITO)
                .idCuentaBancaria(idCuentaDestino)
                .tipoMovimiento("DEBITO")
                .monto(monto)
                .descripcion("Liberación de fondos en tránsito")
                .build());

        lineas.add(LineaAsientoDTO.builder()
                .codigoCuenta(DEPOSITOS_AHORROS)
                .idCuentaBancaria(idCuentaDestino)
                .tipoMovimiento("CREDITO")
                .monto(monto)
                .descripcion("Crédito liberado — transferencia completada")
                .build());

        return lineas;
    }

    @Override
    public List<LineaAsientoDTO> crearAsientoComision(Long idCuentaOrigen, BigDecimal monto) {
        List<LineaAsientoDTO> lineas = new ArrayList<>();

        lineas.add(LineaAsientoDTO.builder()
                .codigoCuenta(DEPOSITOS_AHORROS)
                .idCuentaBancaria(idCuentaOrigen)
                .tipoMovimiento("DEBITO")
                .monto(monto)
                .descripcion("Comisión por transferencia")
                .build());

        lineas.add(LineaAsientoDTO.builder()
                .codigoCuenta(INGRESOS_COMISIONES)
                .idCuentaBancaria(null)
                .tipoMovimiento("CREDITO")
                .monto(monto)
                .descripcion("Ingreso por comisión de transferencia")
                .build());

        return lineas;
    }

    @Override
    public List<LineaAsientoDTO> crearAsientoReversionTransferencia(Long idCuentaOrigen, Long idCuentaDestino, BigDecimal monto) {
        List<LineaAsientoDTO> lineas = new ArrayList<>();

        lineas.add(LineaAsientoDTO.builder()
                .codigoCuenta(DEPOSITOS_AHORROS)
                .idCuentaBancaria(idCuentaDestino)
                .tipoMovimiento("DEBITO")
                .monto(monto)
                .descripcion("Reversión crédito — transferencia revertida")
                .build());

        lineas.add(LineaAsientoDTO.builder()
                .codigoCuenta(DEPOSITOS_AHORROS)
                .idCuentaBancaria(idCuentaOrigen)
                .tipoMovimiento("CREDITO")
                .monto(monto)
                .descripcion("Reversión débito — transferencia revertida")
                .build());

        return lineas;
    }

    @Override
    public List<LineaAsientoDTO> crearAsientoIntereses(Long idCuenta, BigDecimal monto) {
        List<LineaAsientoDTO> lineas = new ArrayList<>();

        lineas.add(LineaAsientoDTO.builder()
                .codigoCuenta(GASTOS_OPERATIVOS)
                .idCuentaBancaria(null)
                .tipoMovimiento("DEBITO")
                .monto(monto)
                .descripcion("Intereses pagados en depósitos")
                .build());

        lineas.add(LineaAsientoDTO.builder()
                .codigoCuenta(DEPOSITOS_AHORROS)
                .idCuentaBancaria(idCuenta)
                .tipoMovimiento("CREDITO")
                .monto(monto)
                .descripcion("Intereses acreditados a cuenta de ahorros")
                .build());

        return lineas;
    }
}
