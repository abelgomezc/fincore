package com.fincore.ledger.grpc;

import java.util.ArrayList;
import java.util.List;

/**
 * Proto-generated stub para LedgerService — todos los mensajes.
 * Reemplaza código generado por proto — © 2026 Abel Gomez.
 */
public class LedgerProto {

    public static class LineaAsientoDTO {
        private String codigoCuenta;
        private long idCuentaBancaria;
        private String tipoMovimiento;
        private double monto;
        private String descripcion;
        public String getCodigoCuenta() { return codigoCuenta; }
        public void setCodigoCuenta(String v) { this.codigoCuenta = v; }
        public long getIdCuentaBancaria() { return idCuentaBancaria; }
        public void setIdCuentaBancaria(long v) { this.idCuentaBancaria = v; }
        public String getTipoMovimiento() { return tipoMovimiento; }
        public void setTipoMovimiento(String v) { this.tipoMovimiento = v; }
        public double getMonto() { return monto; }
        public void setMonto(double v) { this.monto = v; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String v) { this.descripcion = v; }
    }

    public static class CrearAsientoRequest {
        private String descripcion;
        private long idReferencia;
        private String tipoReferencia;
        private String idUsuario;
        private String ipOrigen;
        private String traceId;
        private List<LineaAsientoDTO> lineas = new ArrayList<>();
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String v) { this.descripcion = v; }
        public long getIdReferencia() { return idReferencia; }
        public void setIdReferencia(long v) { this.idReferencia = v; }
        public String getTipoReferencia() { return tipoReferencia; }
        public void setTipoReferencia(String v) { this.tipoReferencia = v; }
        public String getIdUsuario() { return idUsuario; }
        public void setIdUsuario(String v) { this.idUsuario = v; }
        public String getIpOrigen() { return ipOrigen; }
        public void setIpOrigen(String v) { this.ipOrigen = v; }
        public String getTraceId() { return traceId; }
        public void setTraceId(String v) { this.traceId = v; }
        public List<LineaAsientoDTO> getLineasList() { return lineas; }
        public void setLineas(List<LineaAsientoDTO> v) { this.lineas = v; }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final CrearAsientoRequest i = new CrearAsientoRequest();
            public Builder setDescripcion(String v) { i.setDescripcion(v); return this; }
            public Builder setIdReferencia(long v) { i.setIdReferencia(v); return this; }
            public Builder setTipoReferencia(String v) { i.setTipoReferencia(v); return this; }
            public Builder setIdUsuario(String v) { i.setIdUsuario(v); return this; }
            public Builder setIpOrigen(String v) { i.setIpOrigen(v); return this; }
            public Builder setTraceId(String v) { i.setTraceId(v); return this; }
            public Builder addLineas(LineaAsientoDTO v) { i.getLineasList().add(v); return this; }
            public Builder addAllLineas(Iterable<LineaAsientoDTO> vs) { for (LineaAsientoDTO v : vs) i.getLineasList().add(v); return this; }
            public CrearAsientoRequest build() { return i; }
        }
    }

    public static class CrearAsientoResponse {
        private boolean exito;
        private String numeroAsiento;
        private long idAsiento;
        private String mensajeError;
        private String traceId;
        public boolean getExito() { return exito; }
        public void setExito(boolean v) { this.exito = v; }
        public String getNumeroAsiento() { return numeroAsiento; }
        public void setNumeroAsiento(String v) { this.numeroAsiento = v; }
        public long getIdAsiento() { return idAsiento; }
        public void setIdAsiento(long v) { this.idAsiento = v; }
        public String getMensajeError() { return mensajeError; }
        public void setMensajeError(String v) { this.mensajeError = v; }
        public String getTraceId() { return traceId; }
        public void setTraceId(String v) { this.traceId = v; }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final CrearAsientoResponse i = new CrearAsientoResponse();
            public Builder setExito(boolean v) { i.setExito(v); return this; }
            public Builder setNumeroAsiento(String v) { i.setNumeroAsiento(v); return this; }
            public Builder setIdAsiento(long v) { i.setIdAsiento(v); return this; }
            public Builder setMensajeError(String v) { i.setMensajeError(v); return this; }
            public Builder setTraceId(String v) { i.setTraceId(v); return this; }
            public CrearAsientoResponse build() { return i; }
        }
    }

    public static class ObtenerSaldoCuentaRequest {
        private String codigoCuenta;
        private long idCuentaBancaria;
        public String getCodigoCuenta() { return codigoCuenta; }
        public void setCodigoCuenta(String v) { this.codigoCuenta = v; }
        public long getIdCuentaBancaria() { return idCuentaBancaria; }
        public void setIdCuentaBancaria(long v) { this.idCuentaBancaria = v; }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final ObtenerSaldoCuentaRequest i = new ObtenerSaldoCuentaRequest();
            public Builder setCodigoCuenta(String v) { i.setCodigoCuenta(v); return this; }
            public Builder setIdCuentaBancaria(long v) { i.setIdCuentaBancaria(v); return this; }
            public ObtenerSaldoCuentaRequest build() { return i; }
        }
    }

    public static class ObtenerSaldoCuentaResponse {
        private boolean exito;
        private String codigoCuenta;
        private double saldoNeto;
        private double totalDebitos;
        private double totalCreditos;
        private String mensajeError;
        public boolean getExito() { return exito; }
        public void setExito(boolean v) { this.exito = v; }
        public String getCodigoCuenta() { return codigoCuenta; }
        public void setCodigoCuenta(String v) { this.codigoCuenta = v; }
        public double getSaldoNeto() { return saldoNeto; }
        public void setSaldoNeto(double v) { this.saldoNeto = v; }
        public double getTotalDebitos() { return totalDebitos; }
        public void setTotalDebitos(double v) { this.totalDebitos = v; }
        public double getTotalCreditos() { return totalCreditos; }
        public void setTotalCreditos(double v) { this.totalCreditos = v; }
        public String getMensajeError() { return mensajeError; }
        public void setMensajeError(String v) { this.mensajeError = v; }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final ObtenerSaldoCuentaResponse i = new ObtenerSaldoCuentaResponse();
            public Builder setExito(boolean v) { i.setExito(v); return this; }
            public Builder setCodigoCuenta(String v) { i.setCodigoCuenta(v); return this; }
            public Builder setSaldoNeto(double v) { i.setSaldoNeto(v); return this; }
            public Builder setTotalDebitos(double v) { i.setTotalDebitos(v); return this; }
            public Builder setTotalCreditos(double v) { i.setTotalCreditos(v); return this; }
            public Builder setMensajeError(String v) { i.setMensajeError(v); return this; }
            public ObtenerSaldoCuentaResponse build() { return i; }
        }
    }

    public static class VerificarEquibradoRequest {
        public static VerificarEquibradoRequest getDefaultInstance() { return new VerificarEquibradoRequest(); }
    }

    public static class VerificarEquilibrioResponse {
        private boolean exito;
        private double diferencia;
        private long totalAsientos;
        private String mensajeError;
        public boolean getExito() { return exito; }
        public void setExito(boolean v) { this.exito = v; }
        public double getDiferencia() { return diferencia; }
        public void setDiferencia(double v) { this.diferencia = v; }
        public long getTotalAsientos() { return totalAsientos; }
        public void setTotalAsientos(long v) { this.totalAsientos = v; }
        public String getMensajeError() { return mensajeError; }
        public void setMensajeError(String v) { this.mensajeError = v; }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final VerificarEquilibrioResponse i = new VerificarEquilibrioResponse();
            public Builder setExito(boolean v) { i.setExito(v); return this; }
            public Builder setDiferencia(double v) { i.setDiferencia(v); return this; }
            public Builder setTotalAsientos(long v) { i.setTotalAsientos(v); return this; }
            public Builder setMensajeError(String v) { i.setMensajeError(v); return this; }
            public VerificarEquilibrioResponse build() { return i; }
        }
    }

    public static class MovimientoExtractoDTO {
        private String numeroAsiento;
        private String fechaAsiento;
        private String codigoCuenta;
        private String nombreCuenta;
        private String tipoMovimiento;
        private double monto;
        private String descripcion;
        public String getNumeroAsiento() { return numeroAsiento; }
        public void setNumeroAsiento(String v) { this.numeroAsiento = v; }
        public String getFechaAsiento() { return fechaAsiento; }
        public void setFechaAsiento(String v) { this.fechaAsiento = v; }
        public String getCodigoCuenta() { return codigoCuenta; }
        public void setCodigoCuenta(String v) { this.codigoCuenta = v; }
        public String getNombreCuenta() { return nombreCuenta; }
        public void setNombreCuenta(String v) { this.nombreCuenta = v; }
        public String getTipoMovimiento() { return tipoMovimiento; }
        public void setTipoMovimiento(String v) { this.tipoMovimiento = v; }
        public double getMonto() { return monto; }
        public void setMonto(double v) { this.monto = v; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String v) { this.descripcion = v; }
    }

    public static class ObtenerExtractoRequest {
        private long idCuentaBancaria;
        private String fechaDesde;
        private String fechaHasta;
        public long getIdCuentaBancaria() { return idCuentaBancaria; }
        public void setIdCuentaBancaria(long v) { this.idCuentaBancaria = v; }
        public String getFechaDesde() { return fechaDesde; }
        public void setFechaDesde(String v) { this.fechaDesde = v; }
        public String getFechaHasta() { return fechaHasta; }
        public void setFechaHasta(String v) { this.fechaHasta = v; }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final ObtenerExtractoRequest i = new ObtenerExtractoRequest();
            public Builder setIdCuentaBancaria(long v) { i.setIdCuentaBancaria(v); return this; }
            public Builder setFechaDesde(String v) { i.setFechaDesde(v); return this; }
            public Builder setFechaHasta(String v) { i.setFechaHasta(v); return this; }
            public ObtenerExtractoRequest build() { return i; }
        }
    }

    public static class ObtenerExtractoResponse {
        private boolean exito;
        private List<MovimientoExtractoDTO> movimientos = new ArrayList<>();
        private String mensajeError;
        public boolean getExito() { return exito; }
        public void setExito(boolean v) { this.exito = v; }
        public List<MovimientoExtractoDTO> getMovimientosList() { return movimientos; }
        public void setMovimientos(List<MovimientoExtractoDTO> v) { this.movimientos = v; }
        public String getMensajeError() { return mensajeError; }
        public void setMensajeError(String v) { this.mensajeError = v; }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final ObtenerExtractoResponse i = new ObtenerExtractoResponse();
            public Builder setExito(boolean v) { i.setExito(v); return this; }
            public Builder addAllMovimientos(Iterable<MovimientoExtractoDTO> vs) { for (MovimientoExtractoDTO v : vs) i.getMovimientosList().add(v); return this; }
            public Builder setMensajeError(String v) { i.setMensajeError(v); return this; }
            public ObtenerExtractoResponse build() { return i; }
        }
    }

    public static class ReversarAsientoRequest {
        private String numeroAsiento;
        private String descripcionRevision;
        private String idUsuario;
        private String traceId;
        public String getNumeroAsiento() { return numeroAsiento; }
        public void setNumeroAsiento(String v) { this.numeroAsiento = v; }
        public String getDescripcionRevision() { return descripcionRevision; }
        public void setDescripcionRevision(String v) { this.descripcionRevision = v; }
        public String getIdUsuario() { return idUsuario; }
        public void setIdUsuario(String v) { this.idUsuario = v; }
        public String getTraceId() { return traceId; }
        public void setTraceId(String v) { this.traceId = v; }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final ReversarAsientoRequest i = new ReversarAsientoRequest();
            public Builder setNumeroAsiento(String v) { i.setNumeroAsiento(v); return this; }
            public Builder setDescripcionRevision(String v) { i.setDescripcionRevision(v); return this; }
            public Builder setIdUsuario(String v) { i.setIdUsuario(v); return this; }
            public Builder setTraceId(String v) { i.setTraceId(v); return this; }
            public ReversarAsientoRequest build() { return i; }
        }
    }

    public static class ReversarAsientoResponse {
        private boolean exito;
        private String numeroAsientoReversado;
        private String nuevoNumeroAsiento;
        private String mensajeError;
        private String traceId;
        public boolean getExito() { return exito; }
        public void setExito(boolean v) { this.exito = v; }
        public String getNumeroAsientoReversado() { return numeroAsientoReversado; }
        public void setNumeroAsientoReversado(String v) { this.numeroAsientoReversado = v; }
        public String getNuevoNumeroAsiento() { return nuevoNumeroAsiento; }
        public void setNuevoNumeroAsiento(String v) { this.nuevoNumeroAsiento = v; }
        public String getMensajeError() { return mensajeError; }
        public void setMensajeError(String v) { this.mensajeError = v; }
        public String getTraceId() { return traceId; }
        public void setTraceId(String v) { this.traceId = v; }
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final ReversarAsientoResponse i = new ReversarAsientoResponse();
            public Builder setExito(boolean v) { i.setExito(v); return this; }
            public Builder setNumeroAsientoReversado(String v) { i.setNumeroAsientoReversado(v); return this; }
            public Builder setNuevoNumeroAsiento(String v) { i.setNuevoNumeroAsiento(v); return this; }
            public Builder setMensajeError(String v) { i.setMensajeError(v); return this; }
            public Builder setTraceId(String v) { i.setTraceId(v); return this; }
            public ReversarAsientoResponse build() { return i; }
        }
    }

    public static class ObtenerCuentaRequest {
        private long idCuenta;
        private String numeroCuenta;
        public long getIdCuenta() { return idCuenta; }
        public void setIdCuenta(long v) { this.idCuenta = v; }
        public String getNumeroCuenta() { return numeroCuenta; }
        public void setNumeroCuenta(String v) { this.numeroCuenta = v; }
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
        private long idCuenta;
        private String numeroCuenta;
        private long idCliente;
        private String estado;
        private String moneda;
        private double saldoContable, saldoDisponible, saldoRetenido, saldoProyectado;
        private String mensajeError;
        public boolean getExito() { return exito; }
        public void setExito(boolean v) { this.exito = v; }
        public long getIdCuenta() { return idCuenta; }
        public void setIdCuenta(long v) { this.idCuenta = v; }
        public String getNumeroCuenta() { return numeroCuenta; }
        public void setNumeroCuenta(String v) { this.numeroCuenta = v; }
        public long getIdCliente() { return idCliente; }
        public void setIdCliente(long v) { this.idCliente = v; }
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
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private final ObtenerCuentaResponse i = new ObtenerCuentaResponse();
            public Builder setExito(boolean v) { i.setExito(v); return this; }
            public Builder setIdCuenta(long v) { i.setIdCuenta(v); return this; }
            public Builder setNumeroCuenta(String v) { i.setNumeroCuenta(v); return this; }
            public Builder setIdCliente(long v) { i.setIdCliente(v); return this; }
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
}
