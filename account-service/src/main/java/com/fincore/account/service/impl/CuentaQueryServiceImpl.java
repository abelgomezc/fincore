package com.fincore.account.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CuentaQueryServiceImpl implements CuentaQueryService {

    private final CuentaRepository cuentaRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public CuentaQueryServiceImpl(CuentaRepository cuentaRepository,
                                  RestTemplate restTemplate,
                                  ObjectMapper objectMapper) {
        this.cuentaRepository = cuentaRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
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
        String nombrePropietario = null;
        String identificacionPropietario = null;

        try {
            String url = "http://localhost:8082/api/clientes/" + cuenta.getIdCliente();
            String json = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(json);
            JsonNode nombre = root.get("nombreCompleto");
            JsonNode documentos = root.get("documentos");
            if (nombre != null) {
                nombrePropietario = nombre.asText();
            }
            if (documentos != null && documentos.isArray() && documentos.size() > 0) {
                for (JsonNode doc : documentos) {
                    String tipo = doc.get("tipoDocumento") != null ? doc.get("tipoDocumento").asText() : null;
                    if ("CEDULA".equalsIgnoreCase(tipo) || "IDENTIFICACION".equalsIgnoreCase(tipo)) {
                        JsonNode numero = doc.get("numeroDocumento");
                        if (numero != null) {
                            identificacionPropietario = numero.asText();
                        }
                        break;
                    }
                }
                if (identificacionPropietario == null && documentos.get(0).get("numeroDocumento") != null) {
                    identificacionPropietario = documentos.get(0).get("numeroDocumento").asText();
                }
            }
        } catch (Exception e) {
            log.debug("No se pudo obtener el propietario de la cuenta {}: {}", cuenta.getId(), e.getMessage());
        }

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
                .nombrePropietario(nombrePropietario)
                .identificacionPropietario(identificacionPropietario)
                .build();
    }
}
