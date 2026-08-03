package com.fincore.transfer.proto.account;

/**
 * Solicitud de cuenta simplificada.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class CuentaRequest {
    private long idCuenta;

    public long getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(long idCuenta) {
        this.idCuenta = idCuenta;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private CuentaRequest instance = new CuentaRequest();

        public Builder setIdCuenta(long idCuenta) {
            instance.setIdCuenta(idCuenta);
            return this;
        }

        public CuentaRequest build() {
            return instance;
        }
    }
}
