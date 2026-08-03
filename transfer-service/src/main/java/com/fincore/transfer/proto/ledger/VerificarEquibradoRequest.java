package com.fincore.transfer.proto.ledger;

/**
 * Solicitud para verificar equilibrio simplificada.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class VerificarEquibradoRequest {
    private long idAsiento;

    public long getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(long idAsiento) {
        this.idAsiento = idAsiento;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private VerificarEquibradoRequest instance = new VerificarEquibradoRequest();

        public Builder setIdAsiento(long idAsiento) {
            instance.setIdAsiento(idAsiento);
            return this;
        }

        public VerificarEquibradoRequest build() {
            return instance;
        }
    }
}
