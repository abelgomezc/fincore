package com.fincore.transfer.proto.fraud;

/**
 * Respuesta de evaluación de fraude simplificada.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class EvaluacionFraudeResponse {
    private int score = 0;
    private String decision = "APROBADO";
    private String mensaje = "";

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
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
        private EvaluacionFraudeResponse instance = new EvaluacionFraudeResponse();

        public Builder setScore(int score) {
            instance.setScore(score);
            return this;
        }

        public Builder setDecision(String decision) {
            instance.setDecision(decision);
            return this;
        }

        public Builder setMensaje(String mensaje) {
            instance.setMensaje(mensaje);
            return this;
        }

        public EvaluacionFraudeResponse build() {
            return instance;
        }
    }
}
