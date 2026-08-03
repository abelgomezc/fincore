package com.fincore.transfer.proto.account;

/**
 * Solicitud de transferencia simplificada.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class TransferenciaRequest {
    private long idCuenta;
    private String monto;
    private String traceId;

    public long getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(long idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
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
        private TransferenciaRequest instance = new TransferenciaRequest();

        public Builder setIdCuenta(long idCuenta) {
            instance.setIdCuenta(idCuenta);
            return this;
        }

        public Builder setMonto(String monto) {
            instance.setMonto(monto);
            return this;
        }

        public Builder setTraceId(String traceId) {
            instance.setTraceId(traceId);
            return this;
        }

        public TransferenciaRequest build() {
            return instance;
        }
    }
}
