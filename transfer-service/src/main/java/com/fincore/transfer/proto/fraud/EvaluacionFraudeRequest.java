package com.fincore.transfer.proto.fraud;

/**
 * Solicitud de evaluación de fraude simplificada.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class EvaluacionFraudeRequest {
    private long idTransferencia;
    private long idCuentaOrigen;
    private String monto;
    private String ipOrigen;
    private String dispositivo;
    private String traceId;

    public long getIdTransferencia() {
        return idTransferencia;
    }

    public void setIdTransferencia(long idTransferencia) {
        this.idTransferencia = idTransferencia;
    }

    public long getIdCuentaOrigen() {
        return idCuentaOrigen;
    }

    public void setIdCuentaOrigen(long idCuentaOrigen) {
        this.idCuentaOrigen = idCuentaOrigen;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private EvaluacionFraudeRequest instance = new EvaluacionFraudeRequest();

        public Builder setIdTransferencia(long idTransferencia) {
            instance.setIdTransferencia(idTransferencia);
            return this;
        }

        public Builder setIdCuentaOrigen(long idCuentaOrigen) {
            instance.setIdCuentaOrigen(idCuentaOrigen);
            return this;
        }

        public Builder setMonto(String monto) {
            instance.setMonto(monto);
            return this;
        }

        public Builder setIpOrigen(String ipOrigen) {
            instance.setIpOrigen(ipOrigen);
            return this;
        }

        public Builder setDispositivo(String dispositivo) {
            instance.setDispositivo(dispositivo);
            return this;
        }

        public Builder setTraceId(String traceId) {
            instance.setTraceId(traceId);
            return this;
        }

        public EvaluacionFraudeRequest build() {
            return instance;
        }
    }
}
