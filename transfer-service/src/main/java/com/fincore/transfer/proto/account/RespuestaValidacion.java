package com.fincore.transfer.proto.account;

/**
 * Respuesta de validación simplificada (reemplaza proto-generated).
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class RespuestaValidacion {
    private boolean exito = true;
    private String mensaje = "";

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

    public static RespuestaValidacion getDefaultInstance() {
        return new RespuestaValidacion();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private RespuestaValidacion instance = new RespuestaValidacion();

        public Builder setExito(boolean exito) {
            instance.setExito(exito);
            return this;
        }

        public Builder setMensaje(String mensaje) {
            instance.setMensaje(mensaje);
            return this;
        }

        public RespuestaValidacion build() {
            return instance;
        }
    }
}
