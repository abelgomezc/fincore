package com.fincore.fraud.service.rules;

import com.fincore.fraud.entity.PerfilTransaccional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Contexto con los datos de la transferencia a evaluar.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class RuleContext {
    private final Long idTransferencia;
    private final Long idCuentaOrigen;
    private final Long idCliente;
    private final BigDecimal monto;
    private final String ipOrigen;
    private final String dispositivo;
    private final String numeroCuentaDestino;
    private final Optional<PerfilTransaccional> perfilOpt;
    private final long timestamp;

    public RuleContext(Long idTransferencia, Long idCuentaOrigen, Long idCliente,
                       BigDecimal monto, String ipOrigen, String dispositivo,
                       String numeroCuentaDestino, Optional<PerfilTransaccional> perfilOpt) {
        this.idTransferencia = idTransferencia;
        this.idCuentaOrigen = idCuentaOrigen;
        this.idCliente = idCliente;
        this.monto = monto;
        this.ipOrigen = ipOrigen;
        this.dispositivo = dispositivo;
        this.numeroCuentaDestino = numeroCuentaDestino;
        this.perfilOpt = perfilOpt;
        this.timestamp = System.currentTimeMillis();
    }

    public Long getIdTransferencia() {
        return idTransferencia;
    }

    public Long getIdCuentaOrigen() {
        return idCuentaOrigen;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public String getNumeroCuentaDestino() {
        return numeroCuentaDestino;
    }

    public Optional<PerfilTransaccional> getPerfilOpt() {
        return perfilOpt;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
