package com.fincore.fraud.service.rules;

/**
 * Resultado de una regla individual de fraude.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class RuleResult {
    private final String codigo;
    private final int puntos;
    private final boolean activada;
    private final String descripcion;

    public RuleResult(String codigo, int puntos, boolean activada, String descripcion) {
        this.codigo = codigo;
        this.puntos = puntos;
        this.activada = activada;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getPuntos() {
        return puntos;
    }

    public boolean isActivada() {
        return activada;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
