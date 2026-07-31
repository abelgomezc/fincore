package com.fincore.transfer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Servicio de numeración para transferencias.
 *
 * Genera números únicos de transferencia con formato:
 * TRANS-{año}{mes}{día}-{HHmm}-{secuencia6}
 *
 * Ejemplo: TRANS-20260731-1430-000001
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class NumeradorService {

    private final AtomicLong secuencia = new AtomicLong(0);
    private final DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmm");

    /**
     * Genera un número único de transferencia.
     * Formato: TRANS-{fecha}-{secuencia}
     */
    @Transactional
    public String generarNumeroTransferencia() {
        String fechaHoy = LocalDateTime.now().format(fechaFormatter);
        long numSecuencia = secuencia.incrementAndGet();

        String numero = String.format("TRANS-%s-%06d", fechaHoy, numSecuencia);
        log.debug("Número de transferencia generado: {}", numero);
        return numero;
    }

    /**
     * Genera un número de referencia interno para la transferencia.
     * Formato: REF-{año}{mes}{día}{hora}{min}{seg}-{secuencia6}
     */
    public String generarReferencia() {
        String ahora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long numSecuencia = secuencia.incrementAndGet();
        return String.format("REF-%s-%06d", ahora, numSecuencia);
    }

    /**
     * Genera un trace ID único para rastreo distribuido.
     */
    public String generarTraceId() {
        String ahora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        long numSecuencia = secuencia.incrementAndGet();
        return String.format("TRACE-%s-%05d", ahora, numSecuencia);
    }
}
