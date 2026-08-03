package com.fincore.transfer.proto.ledger;

/**
 * Respuesta para crear asiento contable simplificada.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class CrearAsientoResponse {
    private boolean exito = true;
    private String mensaje = "";
    private long idAsiento;

    public boolean getExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

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
        private CrearAsientoResponse instance = new CrearAsientoResponse();

        public Builder setExito(boolean exito) {
            instance.setExito(exito);
            return this;
        }

        public Builder setMensaje(String mensaje) {
            instance.setMensaje(mensaje);
            return this;
        }

        public Builder setIdAsiento(long idAsiento) {
            instance.setIdAsiento(idAsiento);
            return this;
        }

        public CrearAsientoResponse build() {
            return instance;
        }
    }
}
