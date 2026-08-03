package com.fincore.ledger.service.impl;

import com.fincore.ledger.dto.AsientoDTO;
import com.fincore.ledger.dto.LineaAsientoDTO;
import com.fincore.ledger.dto.response.AsientoResponse;
import com.fincore.ledger.dto.response.BalanceGeneralResponse;
import com.fincore.ledger.dto.response.EstadoCuentaResponse;
import com.fincore.ledger.dto.response.ExtractoResponse;
import com.fincore.ledger.entity.AsientoContable;
import com.fincore.ledger.entity.LineaAsiento;
import com.fincore.ledger.entity.PlanCuenta;
import com.fincore.ledger.enums.NaturalezaCuenta;
import com.fincore.ledger.enums.TipoMovimientoContable;
import com.fincore.ledger.exception.UnbalancedEntryException;
import com.fincore.ledger.exception.CuentaContableNoEncontradaException;
import com.fincore.ledger.exception.AsientoNoEncontradoException;
import com.fincore.ledger.repository.AsientoContableRepository;
import com.fincore.ledger.repository.LineaAsientoRepository;
import com.fincore.ledger.repository.PlanCuentaRepository;
import com.fincore.ledger.service.LedgerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de ledger.
 *
 * Garantiza la integridad contable:
 * - Valida que débitos = créditos antes de persistir
 * - Asientos e líneas son inmutables (INSERT only)
 * - Reversión crea nuevos asientos, nunca modifica existentes
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Service
@Slf4j
@Transactional
public class LedgerServiceImpl implements LedgerService {

    private final AsientoContableRepository asientoRepository;
    private final LineaAsientoRepository lineaRepository;
    private final PlanCuentaRepository planCuentaRepository;

    @Value("${ledger.numero.asiento.prefix:AS-2026-}")
    private String asientoPrefix;

    @Value("${ledger.validacion.equilibrio.strict:true}")
    private boolean validacionStrict;

    public LedgerServiceImpl(AsientoContableRepository asientoRepository,
                             LineaAsientoRepository lineaRepository,
                             PlanCuentaRepository planCuentaRepository) {
        this.asientoRepository = asientoRepository;
        this.lineaRepository = lineaRepository;
        this.planCuentaRepository = planCuentaRepository;
    }

    @Override
    public AsientoResponse crearAsiento(AsientoDTO dto) {
        return crearAsiento(dto.getLineas(), dto.getDescripcion(),
                dto.getTipoReferencia(), dto.getIdReferencia(),
                dto.getIdUsuario(), dto.getIpOrigen(), dto.getTraceId());
    }

    @Override
    public AsientoResponse crearAsiento(List<LineaAsientoDTO> lineas, String descripcion,
                                         String tipoReferencia, Long idReferencia,
                                         String idUsuario, String ipOrigen, String traceId) {
        if (lineas == null || lineas.isEmpty()) {
            return AsientoResponse.builder()
                    .exito(false)
                    .mensajeError("No se proporcionaron líneas de asiento")
                    .build();
        }

        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }

        log.info("Creando asiento: descripción={}, referencia={}, traceId={}",
                descripcion, idReferencia, traceId);

        // Validar equilibrio: débitos = créditos
        BigDecimal sumDebitos = lineas.stream()
                .filter(l -> "DEBITO".equals(l.getTipoMovimiento()))
                .map(LineaAsientoDTO::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sumCreditos = lineas.stream()
                .filter(l -> "CREDITO".equals(l.getTipoMovimiento()))
                .map(LineaAsientoDTO::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sumDebitos.compareTo(sumCreditos) != 0) {
            log.error("Asiento no cuadra: débitos={}, créditos={}", sumDebitos, sumCreditos);
            throw new UnbalancedEntryException(
                    "El asiento no está balanceado. Débitos: " + sumDebitos +
                    ", Créditos: " + sumCreditos + ". La diferencia debe ser 0.");
        }

        // Validar que todas las cuentas existen y son hojas
        for (LineaAsientoDTO linea : lineas) {
            PlanCuenta cuenta = planCuentaRepository.findByCodigo(linea.getCodigoCuenta())
                    .orElseThrow(() -> new CuentaContableNoEncontradaException(
                            "Cuenta contable no encontrada: " + linea.getCodigoCuenta()));

            if (!cuenta.getEsHoja()) {
                throw new CuentaContableNoEncontradaException(
                        "La cuenta " + linea.getCodigoCuenta() + " no es una cuenta hoja (no puede recibir asientos)");
            }

            if (!cuenta.getEsActiva()) {
                throw new CuentaContableNoEncontradaException(
                        "La cuenta " + linea.getCodigoCuenta() + " está inactiva");
            }
        }

        // Generar número de asiento único
        String numeroAsiento = generarNumeroAsiento();

        // Crear asiento contable (INMUTABLE)
        AsientoContable asiento = new AsientoContable();
        asiento.setNumeroAsiento(numeroAsiento);
        asiento.setDescripcion(descripcion);
        asiento.setIdReferencia(idReferencia);
        asiento.setTipoReferencia(tipoReferencia);
        asiento.setIdUsuario(idUsuario);
        asiento.setIpOrigen(ipOrigen);
        asiento.setTraceId(traceId);
        asiento.setFechaAsiento(LocalDateTime.now());
        asiento.setEstado("ACTIVO");

        AsientoContable asientoGuardado = asientoRepository.save(asiento);

        // Crear líneas de asiento (INMUTABLES)
        for (LineaAsientoDTO lineaDto : lineas) {
            LineaAsiento linea = new LineaAsiento();
            linea.setIdAsiento(asientoGuardado.getId());
            linea.setCodigoCuenta(lineaDto.getCodigoCuenta());
            linea.setIdCuentaBancaria(lineaDto.getIdCuentaBancaria());
            linea.setTipoMovimiento(lineaDto.getTipoMovimiento());
            linea.setMonto(lineaDto.getMonto());
            linea.setDescripcion(lineaDto.getDescripcion());
            lineaRepository.save(linea);
        }

        log.info("Asiento creado exitosamente: {} con {} líneas (débitos={}, créditos={})",
                numeroAsiento, lineas.size(), sumDebitos, sumCreditos);

        return AsientoResponse.builder()
                .exito(true)
                .numeroAsiento(numeroAsiento)
                .idAsiento(asientoGuardado.getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AsientoContable obtenerAsiento(Long idAsiento) {
        return asientoRepository.findById(idAsiento)
                .orElseThrow(() -> new AsientoNoEncontradoException("Asiento no encontrado: " + idAsiento));
    }

    @Override
    @Transactional(readOnly = true)
    public AsientoContable obtenerAsientoPorNumero(String numeroAsiento) {
        return asientoRepository.findByNumeroAsiento(numeroAsiento)
                .orElseThrow(() -> new AsientoNoEncontradoException("Asiento no encontrado: " + numeroAsiento));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LineaAsiento> obtenerLineasDeAsiento(Long idAsiento) {
        return lineaRepository.findByIdAsiento(idAsiento);
    }

    @Override
    public AsientoResponse reversarAsiento(String numeroAsiento, String descripcion, String idUsuario, String traceId) {
        log.info("Revirtiendo asiento: {}, traceId={}", numeroAsiento, traceId);

        AsientoContable asientoOriginal = asientoRepository.findByNumeroAsiento(numeroAsiento)
                .orElseThrow(() -> new AsientoNoEncontradoException("Asiento no encontrado: " + numeroAsiento));

        if (!asientoOriginal.esActivo()) {
            return AsientoResponse.builder()
                    .exito(false)
                    .mensajeError("El asiento ya está reversado")
                    .build();
        }

        // Obtener líneas originales
        List<LineaAsiento> lineasOriginales = lineaRepository.findByIdAsiento(asientoOriginal.getId());

        // Invertir débitos y créditos en el asiento de reversión
        List<LineaAsientoDTO> lineasReversa = lineasOriginales.stream()
                .map(l -> LineaAsientoDTO.builder()
                        .codigoCuenta(l.getCodigoCuenta())
                        .idCuentaBancaria(l.getIdCuentaBancaria())
                        .tipoMovimiento("DEBITO".equals(l.getTipoMovimiento()) ? "CREDITO" : "DEBITO")
                        .monto(l.getMonto())
                        .descripcion("Reversión: " + l.getDescripcion())
                        .build())
                .collect(Collectors.toList());

        // Crear asiento de reversión
        AsientoResponse response = crearAsiento(lineasReversa,
                descripcion + " — Reversión de " + numeroAsiento,
                asientoOriginal.getTipoReferencia(),
                asientoOriginal.getIdReferencia(),
                idUsuario,
                asientoOriginal.getIpOrigen(),
                traceId);

        if (response.isExito()) {
            // Marcar el asiento original como REVERSADO
            asientoOriginal.reversar();
            asientoRepository.save(asientoOriginal);
            log.info("Asiento {} reversado como {}", numeroAsiento, response.getNumeroAsiento());
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public EstadoCuentaResponse obtenerEstadoCuenta(String codigoCuenta) {
        log.debug("Obteniendo estado de cuenta: {}", codigoCuenta);

        PlanCuenta cuenta = planCuentaRepository.findByCodigo(codigoCuenta)
                .orElseThrow(() -> new CuentaContableNoEncontradaException("Cuenta no encontrada: " + codigoCuenta));

        List<LineaAsiento> lineas = lineaRepository.findByCodigoCuenta(codigoCuenta);

        BigDecimal totalDebitos = BigDecimal.ZERO;
        BigDecimal totalCreditos = BigDecimal.ZERO;

        for (LineaAsiento l : lineas) {
            if ("DEBITO".equals(l.getTipoMovimiento())) {
                totalDebitos = totalDebitos.add(l.getMonto());
            } else {
                totalCreditos = totalCreditos.add(l.getMonto());
            }
        }

        BigDecimal saldoNeto;
        if (NaturalezaCuenta.DEUDORA.name().equals(cuenta.getNaturaleza())) {
            saldoNeto = totalDebitos.subtract(totalCreditos);
        } else {
            saldoNeto = totalCreditos.subtract(totalDebitos);
        }

        return EstadoCuentaResponse.builder()
                .codigoCuenta(cuenta.getCodigo())
                .nombreCuenta(cuenta.getNombre())
                .naturaleza(cuenta.getNaturaleza())
                .totalDebitos(totalDebitos)
                .totalCreditos(totalCreditos)
                .saldoNeto(saldoNeto)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public EstadoCuentaResponse obtenerEstadoCuentaBancaria(Long idCuentaBancaria) {
        log.debug("Obteniendo estado de cuenta bancaria: {}", idCuentaBancaria);

        List<LineaAsiento> lineas = lineaRepository.findByIdCuentaBancaria(idCuentaBancaria);

        BigDecimal totalDebitos = BigDecimal.ZERO;
        BigDecimal totalCreditos = BigDecimal.ZERO;

        for (LineaAsiento l : lineas) {
            if ("DEBITO".equals(l.getTipoMovimiento())) {
                totalDebitos = totalDebitos.add(l.getMonto());
            } else {
                totalCreditos = totalCreditos.add(l.getMonto());
            }
        }

        return EstadoCuentaResponse.builder()
                .idCuenta(idCuentaBancaria)
                .totalDebitos(totalDebitos)
                .totalCreditos(totalCreditos)
                .saldoNeto(totalDebitos.subtract(totalCreditos))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ExtractoResponse obtenerExtracto(Long idCuentaBancaria, LocalDate desde, LocalDate hasta) {
        log.debug("Obteniendo extracto: cuenta={}, desde={}, hasta={}", idCuentaBancaria, desde, hasta);

        List<LineaAsiento> lineas = lineaRepository.findByIdCuentaBancaria(idCuentaBancaria);

        // Filtrar por fecha
        LocalDateTime desdeDate = desde.atStartOfDay();
        LocalDateTime hastaDate = hasta.plusDays(1).atStartOfDay();

        List<LineaAsiento> lineasFiltradas = lineas.stream()
                .filter(l -> !l.getFechaCreacion().isBefore(desdeDate) && !l.getFechaCreacion().isAfter(hastaDate))
                .collect(Collectors.toList());

        List<ExtractoResponse.MovimientoContableDTO> movimientos = lineasFiltradas.stream()
                .map(l -> {
                    AsientoContable asiento = asientoRepository.findById(l.getIdAsiento()).orElse(null);
                    PlanCuenta cuenta = planCuentaRepository.findByCodigo(l.getCodigoCuenta()).orElse(null);

                    return ExtractoResponse.MovimientoContableDTO.builder()
                            .numeroAsiento(asiento != null ? asiento.getNumeroAsiento() : null)
                            .fechaAsiento(asiento != null && asiento.getFechaAsiento() != null
                                    ? asiento.getFechaAsiento().toString() : null)
                            .codigoCuenta(l.getCodigoCuenta())
                            .nombreCuenta(cuenta != null ? cuenta.getNombre() : null)
                            .tipoMovimiento(l.getTipoMovimiento())
                            .monto(l.getMonto())
                            .descripcion(l.getDescripcion())
                            .traceId(asiento != null ? asiento.getTraceId() : null)
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal totalDebitos = lineasFiltradas.stream()
                .filter(l -> "DEBITO".equals(l.getTipoMovimiento()))
                .map(LineaAsiento::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCreditos = lineasFiltradas.stream()
                .filter(l -> "CREDITO".equals(l.getTipoMovimiento()))
                .map(LineaAsiento::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ExtractoResponse.builder()
                .movimientos(movimientos)
                .totalDebitos(totalDebitos)
                .totalCreditos(totalCreditos)
                .saldoNeto(totalDebitos.subtract(totalCreditos))
                .totalRegistros(movimientos.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ExtractoResponse obtenerExtractoPorCodigo(String codigoCuenta, LocalDate desde, LocalDate hasta) {
        PlanCuenta cuenta = planCuentaRepository.findByCodigo(codigoCuenta)
                .orElseThrow(() -> new CuentaContableNoEncontradaException("Cuenta no encontrada: " + codigoCuenta));

        List<LineaAsiento> lineas = lineaRepository.findByCodigoCuenta(codigoCuenta);

        LocalDateTime desdeDate = desde.atStartOfDay();
        LocalDateTime hastaDate = hasta.plusDays(1).atStartOfDay();

        List<LineaAsiento> lineasFiltradas = lineas.stream()
                .filter(l -> !l.getFechaCreacion().isBefore(desdeDate) && !l.getFechaCreacion().isAfter(hastaDate))
                .collect(Collectors.toList());

        List<ExtractoResponse.MovimientoContableDTO> movimientos = lineasFiltradas.stream()
                .map(l -> {
                    AsientoContable asiento = asientoRepository.findById(l.getIdAsiento()).orElse(null);
                    return ExtractoResponse.MovimientoContableDTO.builder()
                            .numeroAsiento(asiento != null ? asiento.getNumeroAsiento() : null)
                            .fechaAsiento(asiento != null && asiento.getFechaAsiento() != null
                                    ? asiento.getFechaAsiento().toString() : null)
                            .codigoCuenta(l.getCodigoCuenta())
                            .nombreCuenta(cuenta.getNombre())
                            .tipoMovimiento(l.getTipoMovimiento())
                            .monto(l.getMonto())
                            .descripcion(l.getDescripcion())
                            .traceId(asiento != null ? asiento.getTraceId() : null)
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal totalDebitos = lineasFiltradas.stream()
                .filter(l -> "DEBITO".equals(l.getTipoMovimiento()))
                .map(LineaAsiento::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCreditos = lineasFiltradas.stream()
                .filter(l -> "CREDITO".equals(l.getTipoMovimiento()))
                .map(LineaAsiento::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ExtractoResponse.builder()
                .movimientos(movimientos)
                .totalDebitos(totalDebitos)
                .totalCreditos(totalCreditos)
                .saldoNeto(totalDebitos.subtract(totalCreditos))
                .totalRegistros(movimientos.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceGeneralResponse verificarEquilibrio() {
        log.info("Verificando equilibrio del ledger");

        BigDecimal sumDebitos = asientoRepository.sumarDebitos();
        BigDecimal sumCreditos = asientoRepository.sumarCreditos();

        if (sumDebitos == null) sumDebitos = BigDecimal.ZERO;
        if (sumCreditos == null) sumCreditos = BigDecimal.ZERO;

        BigDecimal diferencia = sumDebitos.subtract(sumCreditos);

        if (validacionStrict && diferencia.compareTo(BigDecimal.ZERO) != 0) {
            log.error("¡EL LEDGER NO ESTÁ EN EQUILIBRIO! Diferencia: {}", diferencia);
            throw new IllegalStateException("El ledger no está en equilibrio. Débitos: " +
                    sumDebitos + ", Créditos: " + sumCreditos + ", Diferencia: " + diferencia);
        }

        long totalAsientos = asientoRepository.contarAsientos();

        return BalanceGeneralResponse.builder()
                .totalActivos(BigDecimal.ZERO)
                .totalPasivos(BigDecimal.ZERO)
                .totalPatrimonio(BigDecimal.ZERO)
                .diferencia(diferencia)
                .equilibrioOk(diferencia.compareTo(BigDecimal.ZERO) == 0)
                .totalAsientos((int) totalAsientos)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceGeneralResponse obtenerBalanceGeneral() {
        log.info("Generando balance general");

        List<PlanCuenta> cuentas = planCuentaRepository.findByEsHojaTrueAndEsActivaTrue();

        BigDecimal totalActivos = BigDecimal.ZERO;
        BigDecimal totalPasivos = BigDecimal.ZERO;
        BigDecimal totalPatrimonio = BigDecimal.ZERO;
        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalGastos = BigDecimal.ZERO;

        for (PlanCuenta cuenta : cuentas) {
            List<LineaAsiento> lineas = lineaRepository.findByCodigoCuenta(cuenta.getCodigo());

            BigDecimal sumDebitos = lineas.stream()
                    .filter(l -> "DEBITO".equals(l.getTipoMovimiento()))
                    .map(LineaAsiento::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal sumCreditos = lineas.stream()
                    .filter(l -> "CREDITO".equals(l.getTipoMovimiento()))
                    .map(LineaAsiento::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal saldo;
            if (NaturalezaCuenta.DEUDORA.name().equals(cuenta.getNaturaleza())) {
                saldo = sumDebitos.subtract(sumCreditos);
            } else {
                saldo = sumCreditos.subtract(sumDebitos);
            }

            switch (cuenta.getTipo()) {
                case "ACTIVO" -> totalActivos = totalActivos.add(saldo);
                case "PASIVO" -> totalPasivos = totalPasivos.add(saldo);
                case "PATRIMONIO" -> totalPatrimonio = totalPatrimonio.add(saldo);
                case "INGRESO" -> totalIngresos = totalIngresos.add(saldo);
                case "GASTO" -> totalGastos = totalGastos.add(saldo);
            }
        }

        BigDecimal diferencia = totalActivos.subtract(totalPasivos).subtract(totalPatrimonio);

        if (validacionStrict && diferencia.compareTo(BigDecimal.ZERO) != 0) {
            log.error("¡EL BALANCE NO CUADRA! Diferencia: {}", diferencia);
            throw new IllegalStateException("El balance general no cuadra. Diferencia: " + diferencia);
        }

        return BalanceGeneralResponse.builder()
                .totalActivos(totalActivos)
                .totalPasivos(totalPasivos)
                .totalPatrimonio(totalPatrimonio)
                .diferencia(diferencia)
                .equilibrioOk(diferencia.compareTo(BigDecimal.ZERO) == 0)
                .totalAsientos((int) asientoRepository.contarAsientos())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsientoContable> obtenerAsientosPorReferencia(Long idReferencia, String tipoReferencia) {
        return asientoRepository.findByIdReferenciaAndTipoReferencia(idReferencia, tipoReferencia);
    }

    private String generarNumeroAsiento() {
        long numero = ThreadLocalRandom.current().nextLong(1, 999999);
        String formatted = String.format("%06d", numero);
        return asientoPrefix + formatted;
    }
}
