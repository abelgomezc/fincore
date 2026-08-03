package com.fincore.transfer.proto.ledger;

/**
 * Solicitud para crear asiento contable simplificada.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class CrearAsientoRequest {
    private long idCuentaOrigen;
    private String montoOrigen;
    private String monedaOrigen;
    private long idCuentaDestino;
    private String montoDestino;
    private String monedaDestino;
    private String concepto;
    private String idUsuario;
    private String ipOrigen;
    private String traceId;

    public long getIdCuentaOrigen() {
        return idCuentaOrigen;
    }

    public void setIdCuentaOrigen(long idCuentaOrigen) {
        this.idCuentaOrigen = idCuentaOrigen;
    }

    public String getMontoOrigen() {
        return montoOrigen;
    }

    public void setMontoOrigen(String montoOrigen) {
        this.montoOrigen = montoOrigen;
    }

    public String getMonedaOrigen() {
        return monedaOrigen;
    }

    public void setMonedaOrigen(String monedaOrigen) {
        this.monedaOrigen = monedaOrigen;
    }

    public long getIdCuentaDestino() {
        return idCuentaDestino;
    }

    public void setIdCuentaDestino(long idCuentaDestino) {
        this.idCuentaDestino = idCuentaDestino;
    }

    public String getMontoDestino() {
        return montoDestino;
    }

    public void setMontoDestino(String montoDestino) {
        this.montoDestino = montoDestino;
    }

    public String getMonedaDestino() {
        return monedaDestino;
    }

    public void setMonedaDestino(String monedaDestino) {
        this.monedaDestino = monedaDestino;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private CrearAsientoRequest instance = new CrearAsientoRequest();

        public Builder setIdCuentaOrigen(long idCuentaOrigen) {
            instance.setIdCuentaOrigen(idCuentaOrigen);
            return this;
        }

        public Builder setMontoOrigen(String montoOrigen) {
            instance.setMontoOrigen(montoOrigen);
            return this;
        }

        public Builder setMonedaOrigen(String monedaOrigen) {
            instance.setMonedaOrigen(monedaOrigen);
            return this;
        }

        public Builder setIdCuentaDestino(long idCuentaDestino) {
            instance.setIdCuentaDestino(idCuentaDestino);
            return this;
        }

        public Builder setMontoDestino(String montoDestino) {
            instance.setMontoDestino(montoDestino);
            return this;
        }

        public Builder setMonedaDestino(String monedaDestino) {
            instance.setMonedaDestino(monedaDestino);
            return this;
        }

        public Builder setConcepto(String concepto) {
            instance.setConcepto(concepto);
            return this;
        }

        public Builder setIdUsuario(String idUsuario) {
            instance.setIdUsuario(idUsuario);
            return this;
        }

        public Builder setIpOrigen(String ipOrigen) {
            instance.setIpOrigen(ipOrigen);
            return this;
        }

        public Builder setTraceId(String traceId) {
            instance.setTraceId(traceId);
            return this;
        }

        public CrearAsientoRequest build() {
            return instance;
        }
    }
}
