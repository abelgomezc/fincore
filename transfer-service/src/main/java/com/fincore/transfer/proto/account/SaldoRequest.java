package com.fincore.transfer.proto.account;

/**
 * Solicitud de saldo simplificada.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class SaldoRequest {
    private long idCuenta;
    private String monto;

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

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private SaldoRequest instance = new SaldoRequest();

        public Builder setIdCuenta(long idCuenta) {
            instance.setIdCuenta(idCuenta);
            return this;
        }

        public Builder setMonto(String monto) {
            instance.setMonto(monto);
            return this;
        }

        public SaldoRequest build() {
            return instance;
        }
    }
}
