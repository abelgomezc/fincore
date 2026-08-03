package com.fincore.transfer.proto.ledger;

/**
 * Respuesta para verificar equilibrio simplificada.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class VerificarEquibradoResponse {
    private boolean estaEquilibrado = true;
    private String mensaje = "";

    public boolean getEstaEquilibrado() {
        return estaEquilibrado;
    }

    public void setEstaEquilibrado(boolean estaEquilibrado) {
        this.estaEquilibrado = estaEquilibrado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private VerificarEquibradoResponse instance = new VerificarEquibradoResponse();

        public Builder setEstaEquilibrado(boolean estaEquilibrado) {
            instance.setEstaEquilibrado(estaEquilibrado);
            return this;
        }

        public Builder setMensaje(String mensaje) {
            instance.setMensaje(mensaje);
            return this;
        }

        public VerificarEquibradoResponse build() {
            return instance;
        }
    }
}
