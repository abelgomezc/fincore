package com.fincore.transfer.proto.ledger;

/**
 * Respuesta para obtener asiento simplificada.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class ObtenerAsientoResponse {
    private boolean exito = true;
    private String mensaje = "";
    private String detalle = "";

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

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private ObtenerAsientoResponse instance = new ObtenerAsientoResponse();

        public Builder setExito(boolean exito) {
            instance.setExito(exito);
            return this;
        }

        public Builder setMensaje(String mensaje) {
            instance.setMensaje(mensaje);
            return this;
        }

        public Builder setDetalle(String detalle) {
            instance.setDetalle(detalle);
            return this;
        }

        public ObtenerAsientoResponse build() {
            return instance;
        }
    }
}
