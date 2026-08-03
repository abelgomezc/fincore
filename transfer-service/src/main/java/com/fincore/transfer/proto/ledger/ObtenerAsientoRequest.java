package com.fincore.transfer.proto.ledger;

/**
 * Solicitud para obtener asiento simplificada.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class ObtenerAsientoRequest {
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
        private ObtenerAsientoRequest instance = new ObtenerAsientoRequest();

        public Builder setIdAsiento(long idAsiento) {
            instance.setIdAsiento(idAsiento);
            return this;
        }

        public ObtenerAsientoRequest build() {
            return instance;
        }
    }
}
