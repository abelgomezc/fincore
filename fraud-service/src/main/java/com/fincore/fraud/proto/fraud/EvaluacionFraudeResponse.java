package com.fincore.fraud.proto.fraud;

import java.util.ArrayList;
import java.util.List;

/**
 * Respuesta de evaluación de fraude.
 * Reemplaza código generado por proto — © 2026 Abel Gomez.
 */
public class EvaluacionFraudeResponse {
    private int score;
    private String decision;
    private String mensaje;
    private List<String> reglasActivadas = new ArrayList<>();
    private int tiempoEvaluacionMs;

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public List<String> getReglasActivadas() { return reglasActivadas; }
    public void setReglasActivadas(List<String> reglasActivadas) { this.reglasActivadas = reglasActivadas; }

    public int getTiempoEvaluacionMs() { return tiempoEvaluacionMs; }
    public void setTiempoEvaluacionMs(int tiempoEvaluacionMs) { this.tiempoEvaluacionMs = tiempoEvaluacionMs; }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static EvaluacionFraudeResponse getDefaultInstance() {
        return new EvaluacionFraudeResponse();
    }

    public static class Builder {
        private final EvaluacionFraudeResponse instance = new EvaluacionFraudeResponse();

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

        public Builder addAllReglasActivadas(Iterable<String> reglas) {
            for (String r : reglas) {
                instance.getReglasActivadas().add(r);
            }
            return this;
        }

        public Builder setTiempoEvaluacionMs(int tiempo) {
            instance.setTiempoEvaluacionMs(tiempo);
            return this;
        }

        public EvaluacionFraudeResponse build() {
            return instance;
        }
    }
}
