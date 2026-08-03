package com.fincore.account.grpc;

/**
 * Stub de AccountProto — contiene todos los tipos de mensaje.
 * Reemplaza código generado por proto — © 2026 Abel Gomez.
 */
public class AccountProto {

    public static ReservarFondosRequest getReservarFondosRequestDefaultInstance() {
        return ReservarFondosRequest.getDefaultInstance();
    }

    public static class ReservarFondosRequest {
        private long idCuenta;
        private String numeroCuenta;
        private double monto;
        private String moneda;
        private String traceId;

        public long getIdCuenta() { return idCuenta; }
        public void setIdCuenta(long idCuenta) { this.idCuenta = idCuenta; }
        public String getNumeroCuenta() { return numeroCuenta; }
        public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }
        public double getMonto() { return monto; }
        public void setMonto(double monto) { this.monto = monto; }
        public String getMoneda() { return moneda; }
        public void setMoneda(String moneda) { this.moneda = moneda; }
        public String getTraceId() { return traceId; }
        public void setTraceId(String traceId) { this.traceId = traceId; }

        public static ReservarFondosRequest getDefaultInstance() { return new ReservarFondosRequest(); }
        public static Builder newBuilder() { return new Builder(); }

        public static class Builder {
            private final ReservarFondosRequest instance = new ReservarFondosRequest();
            public Builder setIdCuenta(long v) { instance.setIdCuenta(v); return this; }
            public Builder setNumeroCuenta(String v) { instance.setNumeroCuenta(v); return this; }
            public Builder setMonto(double v) { instance.setMonto(v); return this; }
            public Builder setMoneda(String v) { instance.setMoneda(v); return this; }
            public Builder setTraceId(String v) { instance.setTraceId(v); return this; }
            public ReservarFondosRequest build() { return instance; }
        }
    }

    public static class ReservarFondosResponse {
        private boolean exito;
        private long idCuenta;
        private double saldoContable;
        private double saldoDisponible;
        private double saldoRetenido;
        private double saldoProyectado;
        private String mensajeError;
        private String traceId;

        public boolean getExito() { return exito; }
        public void setExito(boolean exito) { this.exito = exito; }
        public long getIdCuenta() { return idCuenta; }
        public void setIdCuenta(long idCuenta) { this.idCuenta = idCuenta; }
        public double getSaldoContable() { return saldoContable; }
        public void setSaldoContable(double v) { this.saldoContable = v; }
        public double getSaldoDisponible() { return saldoDisponible; }
        public void setSaldoDisponible(double v) { this.saldoDisponible = v; }
        public double getSaldoRetenido() { return saldoRetenido; }
        public void setSaldoRetenido(double v) { this.saldoRetenido = v; }
        public double getSaldoProyectado() { return saldoProyectado; }
        public void setSaldoProyectado(double v) { this.saldoProyectado = v; }
        public String getMensajeError() { return mensajeError; }
        public void setMensajeError(String v) { this.mensajeError = v; }
        public String getTraceId() { return traceId; }
        public void setTraceId(String v) { this.traceId = v; }
        public static ReservarFondosResponse getDefaultInstance() { return new ReservarFondosResponse(); }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final ReservarFondosResponse i = new ReservarFondosResponse();
            public Builder setExito(boolean v) { i.setExito(v); return this; }
            public Builder setIdCuenta(long v) { i.setIdCuenta(v); return this; }
            public Builder setSaldoContable(double v) { i.setSaldoContable(v); return this; }
            public Builder setSaldoDisponible(double v) { i.setSaldoDisponible(v); return this; }
            public Builder setSaldoRetenido(double v) { i.setSaldoRetenido(v); return this; }
            public Builder setSaldoProyectado(double v) { i.setSaldoProyectado(v); return this; }
            public Builder setMensajeError(String v) { i.setMensajeError(v); return this; }
            public Builder setTraceId(String v) { i.setTraceId(v); return this; }
            public ReservarFondosResponse build() { return i; }
        }
    }

    public static class LiberarReservaRequest {
        private long idCuenta;
        private double monto;
        private String traceId;
        public long getIdCuenta() { return idCuenta; }
        public void setIdCuenta(long v) { this.idCuenta = v; }
        public double getMonto() { return monto; }
        public void setMonto(double v) { this.monto = v; }
        public String getTraceId() { return traceId; }
        public void setTraceId(String v) { this.traceId = v; }
        public static LiberarReservaRequest getDefaultInstance() { return new LiberarReservaRequest(); }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final LiberarReservaRequest i = new LiberarReservaRequest();
            public Builder setIdCuenta(long v) { i.setIdCuenta(v); return this; }
            public Builder setMonto(double v) { i.setMonto(v); return this; }
            public Builder setTraceId(String v) { i.setTraceId(v); return this; }
            public LiberarReservaRequest build() { return i; }
        }
    }

    public static class LiberarReservaResponse {
        private boolean exito;
        private long idCuenta;
        private double saldoContable, saldoDisponible, saldoRetenido, saldoProyectado;
        private String mensajeError, traceId;
        public boolean getExito() { return exito; }
        public void setExito(boolean v) { this.exito = v; }
        public long getIdCuenta() { return idCuenta; }
        public void setIdCuenta(long v) { this.idCuenta = v; }
        public double getSaldoContable() { return saldoContable; }
        public void setSaldoContable(double v) { this.saldoContable = v; }
        public double getSaldoDisponible() { return saldoDisponible; }
        public void setSaldoDisponible(double v) { this.saldoDisponible = v; }
        public double getSaldoRetenido() { return saldoRetenido; }
        public void setSaldoRetenido(double v) { this.saldoRetenido = v; }
        public double getSaldoProyectado() { return saldoProyectado; }
        public void setSaldoProyectado(double v) { this.saldoProyectado = v; }
        public String getMensajeError() { return mensajeError; }
        public void setMensajeError(String v) { this.mensajeError = v; }
        public String getTraceId() { return traceId; }
        public void setTraceId(String v) { this.traceId = v; }
        public static LiberarReservaResponse getDefaultInstance() { return new LiberarReservaResponse(); }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final LiberarReservaResponse i = new LiberarReservaResponse();
            public Builder setExito(boolean v) { i.setExito(v); return this; }
            public Builder setIdCuenta(long v) { i.setIdCuenta(v); return this; }
            public Builder setSaldoContable(double v) { i.setSaldoContable(v); return this; }
            public Builder setSaldoDisponible(double v) { i.setSaldoDisponible(v); return this; }
            public Builder setSaldoRetenido(double v) { i.setSaldoRetenido(v); return this; }
            public Builder setSaldoProyectado(double v) { i.setSaldoProyectado(v); return this; }
            public Builder setMensajeError(String v) { i.setMensajeError(v); return this; }
            public Builder setTraceId(String v) { i.setTraceId(v); return this; }
            public LiberarReservaResponse build() { return i; }
        }
    }

    public static class ObtenerSaldoRequest {
        private long idCuenta;
        private String numeroCuenta;
        public long getIdCuenta() { return idCuenta; }
        public void setIdCuenta(long v) { this.idCuenta = v; }
        public String getNumeroCuenta() { return numeroCuenta; }
        public void setNumeroCuenta(String v) { this.numeroCuenta = v; }
        public static ObtenerSaldoRequest getDefaultInstance() { return new ObtenerSaldoRequest(); }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final ObtenerSaldoRequest i = new ObtenerSaldoRequest();
            public Builder setIdCuenta(long v) { i.setIdCuenta(v); return this; }
            public Builder setNumeroCuenta(String v) { i.setNumeroCuenta(v); return this; }
            public ObtenerSaldoRequest build() { return i; }
        }
    }

    public static class ObtenerSaldoResponse {
        private boolean exito;
        private long idCuenta;
        private String numeroCuenta, estado, moneda, mensajeError;
        private double saldoContable, saldoDisponible, saldoRetenido, saldoProyectado;
        public boolean getExito() { return exito; }
        public void setExito(boolean v) { this.exito = v; }
        public long getIdCuenta() { return idCuenta; }
        public void setIdCuenta(long v) { this.idCuenta = v; }
        public String getNumeroCuenta() { return numeroCuenta; }
        public void setNumeroCuenta(String v) { this.numeroCuenta = v; }
        public String getEstado() { return estado; }
        public void setEstado(String v) { this.estado = v; }
        public String getMoneda() { return moneda; }
        public void setMoneda(String v) { this.moneda = v; }
        public String getMensajeError() { return mensajeError; }
        public void setMensajeError(String v) { this.mensajeError = v; }
        public double getSaldoContable() { return saldoContable; }
        public void setSaldoContable(double v) { this.saldoContable = v; }
        public double getSaldoDisponible() { return saldoDisponible; }
        public void setSaldoDisponible(double v) { this.saldoDisponible = v; }
        public double getSaldoRetenido() { return saldoRetenido; }
        public void setSaldoRetenido(double v) { this.saldoRetenido = v; }
        public double getSaldoProyectado() { return saldoProyectado; }
        public void setSaldoProyectado(double v) { this.saldoProyectado = v; }
        public static ObtenerSaldoResponse getDefaultInstance() { return new ObtenerSaldoResponse(); }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final ObtenerSaldoResponse i = new ObtenerSaldoResponse();
            public Builder setExito(boolean v) { i.setExito(v); return this; }
            public Builder setIdCuenta(long v) { i.setIdCuenta(v); return this; }
            public Builder setNumeroCuenta(String v) { i.setNumeroCuenta(v); return this; }
            public Builder setEstado(String v) { i.setEstado(v); return this; }
            public Builder setMoneda(String v) { i.setMoneda(v); return this; }
            public Builder setMensajeError(String v) { i.setMensajeError(v); return this; }
            public Builder setSaldoContable(double v) { i.setSaldoContable(v); return this; }
            public Builder setSaldoDisponible(double v) { i.setSaldoDisponible(v); return this; }
            public Builder setSaldoRetenido(double v) { i.setSaldoRetenido(v); return this; }
            public Builder setSaldoProyectado(double v) { i.setSaldoProyectado(v); return this; }
            public ObtenerSaldoResponse build() { return i; }
        }
    }

    public static class ValidarCuentaRequest {
        private long idCuenta;
        private String numeroCuenta;
        public long getIdCuenta() { return idCuenta; }
        public void setIdCuenta(long v) { this.idCuenta = v; }
        public String getNumeroCuenta() { return numeroCuenta; }
        public void setNumeroCuenta(String v) { this.numeroCuenta = v; }
        public static ValidarCuentaRequest getDefaultInstance() { return new ValidarCuentaRequest(); }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final ValidarCuentaRequest i = new ValidarCuentaRequest();
            public Builder setIdCuenta(long v) { i.setIdCuenta(v); return this; }
            public Builder setNumeroCuenta(String v) { i.setNumeroCuenta(v); return this; }
            public ValidarCuentaRequest build() { return i; }
        }
    }

    public static class ValidarCuentaResponse {
        private boolean exito, existe, estaActiva;
        private long idCuenta;
        private String numeroCuenta, estado, mensajeError;
        public boolean getExito() { return exito; }
        public void setExito(boolean v) { this.exito = v; }
        public boolean getExiste() { return existe; }
        public void setExiste(boolean v) { this.existe = v; }
        public boolean getEstaActiva() { return estaActiva; }
        public void setEstaActiva(boolean v) { this.estaActiva = v; }
        public long getIdCuenta() { return idCuenta; }
        public void setIdCuenta(long v) { this.idCuenta = v; }
        public String getNumeroCuenta() { return numeroCuenta; }
        public void setNumeroCuenta(String v) { this.numeroCuenta = v; }
        public String getEstado() { return estado; }
        public void setEstado(String v) { this.estado = v; }
        public String getMensajeError() { return mensajeError; }
        public void setMensajeError(String v) { this.mensajeError = v; }
        public static ValidarCuentaResponse getDefaultInstance() { return new ValidarCuentaResponse(); }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final ValidarCuentaResponse i = new ValidarCuentaResponse();
            public Builder setExito(boolean v) { i.setExito(v); return this; }
            public Builder setExiste(boolean v) { i.setExiste(v); return this; }
            public Builder setEstaActiva(boolean v) { i.setEstaActiva(v); return this; }
            public Builder setIdCuenta(long v) { i.setIdCuenta(v); return this; }
            public Builder setNumeroCuenta(String v) { i.setNumeroCuenta(v); return this; }
            public Builder setEstado(String v) { i.setEstado(v); return this; }
            public Builder setMensajeError(String v) { i.setMensajeError(v); return this; }
            public ValidarCuentaResponse build() { return i; }
        }
    }

    public static class ObtenerCuentaRequest {
        private long idCuenta;
        private String numeroCuenta;
        public long getIdCuenta() { return idCuenta; }
        public void setIdCuenta(long v) { this.idCuenta = v; }
        public String getNumeroCuenta() { return numeroCuenta; }
        public void setNumeroCuenta(String v) { this.numeroCuenta = v; }
        public static ObtenerCuentaRequest getDefaultInstance() { return new ObtenerCuentaRequest(); }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final ObtenerCuentaRequest i = new ObtenerCuentaRequest();
            public Builder setIdCuenta(long v) { i.setIdCuenta(v); return this; }
            public Builder setNumeroCuenta(String v) { i.setNumeroCuenta(v); return this; }
            public ObtenerCuentaRequest build() { return i; }
        }
    }

    public static class ObtenerCuentaResponse {
        private boolean exito;
        private long idCuenta, idCliente;
        private String numeroCuenta, estado, moneda, mensajeError;
        private double saldoContable, saldoDisponible, saldoRetenido, saldoProyectado;
        public boolean getExito() { return exito; }
        public void setExito(boolean v) { this.exito = v; }
        public long getIdCuenta() { return idCuenta; }
        public void setIdCuenta(long v) { this.idCuenta = v; }
        public long getIdCliente() { return idCliente; }
        public void setIdCliente(long v) { this.idCliente = v; }
        public String getNumeroCuenta() { return numeroCuenta; }
        public void setNumeroCuenta(String v) { this.numeroCuenta = v; }
        public String getEstado() { return estado; }
        public void setEstado(String v) { this.estado = v; }
        public String getMoneda() { return moneda; }
        public void setMoneda(String v) { this.moneda = v; }
        public double getSaldoContable() { return saldoContable; }
        public void setSaldoContable(double v) { this.saldoContable = v; }
        public double getSaldoDisponible() { return saldoDisponible; }
        public void setSaldoDisponible(double v) { this.saldoDisponible = v; }
        public double getSaldoRetenido() { return saldoRetenido; }
        public void setSaldoRetenido(double v) { this.saldoRetenido = v; }
        public double getSaldoProyectado() { return saldoProyectado; }
        public void setSaldoProyectado(double v) { this.saldoProyectado = v; }
        public String getMensajeError() { return mensajeError; }
        public void setMensajeError(String v) { this.mensajeError = v; }
        public static ObtenerCuentaResponse getDefaultInstance() { return new ObtenerCuentaResponse(); }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final ObtenerCuentaResponse i = new ObtenerCuentaResponse();
            public Builder setExito(boolean v) { i.setExito(v); return this; }
            public Builder setIdCuenta(long v) { i.setIdCuenta(v); return this; }
            public Builder setIdCliente(long v) { i.setIdCliente(v); return this; }
            public Builder setNumeroCuenta(String v) { i.setNumeroCuenta(v); return this; }
            public Builder setEstado(String v) { i.setEstado(v); return this; }
            public Builder setMoneda(String v) { i.setMoneda(v); return this; }
            public Builder setSaldoContable(double v) { i.setSaldoContable(v); return this; }
            public Builder setSaldoDisponible(double v) { i.setSaldoDisponible(v); return this; }
            public Builder setSaldoRetenido(double v) { i.setSaldoRetenido(v); return this; }
            public Builder setSaldoProyectado(double v) { i.setSaldoProyectado(v); return this; }
            public Builder setMensajeError(String v) { i.setMensajeError(v); return this; }
            public ObtenerCuentaResponse build() { return i; }
        }
    }

    public static class AplicarDebitoRequest {
        private long idCuenta;
        private double monto;
        private String tipoMovimiento, traceId;
        public long getIdCuenta() { return idCuenta; }
        public void setIdCuenta(long v) { this.idCuenta = v; }
        public double getMonto() { return monto; }
        public void setMonto(double v) { this.monto = v; }
        public String getTipoMovimiento() { return tipoMovimiento; }
        public void setTipoMovimiento(String v) { this.tipoMovimiento = v; }
        public String getTraceId() { return traceId; }
        public void setTraceId(String v) { this.traceId = v; }
        public static AplicarDebitoRequest getDefaultInstance() { return new AplicarDebitoRequest(); }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final AplicarDebitoRequest i = new AplicarDebitoRequest();
            public Builder setIdCuenta(long v) { i.setIdCuenta(v); return this; }
            public Builder setMonto(double v) { i.setMonto(v); return this; }
            public Builder setTipoMovimiento(String v) { i.setTipoMovimiento(v); return this; }
            public Builder setTraceId(String v) { i.setTraceId(v); return this; }
            public AplicarDebitoRequest build() { return i; }
        }
    }

    public static class AplicarDebitoResponse {
        private boolean exito;
        private long idCuenta;
        private double saldoContable, saldoDisponible, saldoRetenido, saldoProyectado;
        private String mensajeError, traceId;
        public boolean getExito() { return exito; }
        public void setExito(boolean v) { this.exito = v; }
        public long getIdCuenta() { return idCuenta; }
        public void setIdCuenta(long v) { this.idCuenta = v; }
        public double getSaldoContable() { return saldoContable; }
        public void setSaldoContable(double v) { this.saldoContable = v; }
        public double getSaldoDisponible() { return saldoDisponible; }
        public void setSaldoDisponible(double v) { this.saldoDisponible = v; }
        public double getSaldoRetenido() { return saldoRetenido; }
        public void setSaldoRetenido(double v) { this.saldoRetenido = v; }
        public double getSaldoProyectado() { return saldoProyectado; }
        public void setSaldoProyectado(double v) { this.saldoProyectado = v; }
        public String getMensajeError() { return mensajeError; }
        public void setMensajeError(String v) { this.mensajeError = v; }
        public String getTraceId() { return traceId; }
        public void setTraceId(String v) { this.traceId = v; }
        public static AplicarDebitoResponse getDefaultInstance() { return new AplicarDebitoResponse(); }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final AplicarDebitoResponse i = new AplicarDebitoResponse();
            public Builder setExito(boolean v) { i.setExito(v); return this; }
            public Builder setIdCuenta(long v) { i.setIdCuenta(v); return this; }
            public Builder setSaldoContable(double v) { i.setSaldoContable(v); return this; }
            public Builder setSaldoDisponible(double v) { i.setSaldoDisponible(v); return this; }
            public Builder setSaldoRetenido(double v) { i.setSaldoRetenido(v); return this; }
            public Builder setSaldoProyectado(double v) { i.setSaldoProyectado(v); return this; }
            public Builder setMensajeError(String v) { i.setMensajeError(v); return this; }
            public Builder setTraceId(String v) { i.setTraceId(v); return this; }
            public AplicarDebitoResponse build() { return i; }
        }
    }

    public static class AplicarCreditoRequest {
        private long idCuenta;
        private double monto;
        private String tipoMovimiento, traceId;
        public long getIdCuenta() { return idCuenta; }
        public void setIdCuenta(long v) { this.idCuenta = v; }
        public double getMonto() { return monto; }
        public void setMonto(double v) { this.monto = v; }
        public String getTipoMovimiento() { return tipoMovimiento; }
        public void setTipoMovimiento(String v) { this.tipoMovimiento = v; }
        public String getTraceId() { return traceId; }
        public void setTraceId(String v) { this.traceId = v; }
        public static AplicarCreditoRequest getDefaultInstance() { return new AplicarCreditoRequest(); }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final AplicarCreditoRequest i = new AplicarCreditoRequest();
            public Builder setIdCuenta(long v) { i.setIdCuenta(v); return this; }
            public Builder setMonto(double v) { i.setMonto(v); return this; }
            public Builder setTipoMovimiento(String v) { i.setTipoMovimiento(v); return this; }
            public Builder setTraceId(String v) { i.setTraceId(v); return this; }
            public AplicarCreditoRequest build() { return i; }
        }
    }

    public static class AplicarCreditoResponse {
        private boolean exito;
        private long idCuenta;
        private double saldoContable, saldoDisponible, saldoRetenido, saldoProyectado;
        private String mensajeError, traceId;
        public boolean getExito() { return exito; }
        public void setExito(boolean v) { this.exito = v; }
        public long getIdCuenta() { return idCuenta; }
        public void setIdCuenta(long v) { this.idCuenta = v; }
        public double getSaldoContable() { return saldoContable; }
        public void setSaldoContable(double v) { this.saldoContable = v; }
        public double getSaldoDisponible() { return saldoDisponible; }
        public void setSaldoDisponible(double v) { this.saldoDisponible = v; }
        public double getSaldoRetenido() { return saldoRetenido; }
        public void setSaldoRetenido(double v) { this.saldoRetenido = v; }
        public double getSaldoProyectado() { return saldoProyectado; }
        public void setSaldoProyectado(double v) { this.saldoProyectado = v; }
        public String getMensajeError() { return mensajeError; }
        public void setMensajeError(String v) { this.mensajeError = v; }
        public String getTraceId() { return traceId; }
        public void setTraceId(String v) { this.traceId = v; }
        public static AplicarCreditoResponse getDefaultInstance() { return new AplicarCreditoResponse(); }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final AplicarCreditoResponse i = new AplicarCreditoResponse();
            public Builder setExito(boolean v) { i.setExito(v); return this; }
            public Builder setIdCuenta(long v) { i.setIdCuenta(v); return this; }
            public Builder setSaldoContable(double v) { i.setSaldoContable(v); return this; }
            public Builder setSaldoDisponible(double v) { i.setSaldoDisponible(v); return this; }
            public Builder setSaldoRetenido(double v) { i.setSaldoRetenido(v); return this; }
            public Builder setSaldoProyectado(double v) { i.setSaldoProyectado(v); return this; }
            public Builder setMensajeError(String v) { i.setMensajeError(v); return this; }
            public Builder setTraceId(String v) { i.setTraceId(v); return this; }
            public AplicarCreditoResponse build() { return i; }
        }
    }
}
