package com.fincore.transfer.saga;

import com.fincore.transfer.entity.Transferencia;
import com.fincore.transfer.enums.EstadoTransferencia;
import com.fincore.transfer.enums.PasoSaga;

/**
 * Contexto de ejecución de la saga.
 *
 * Contiene la transferencia actual, el estado del saga y
 * metadatos para el seguimiento.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class SagaContext {

    private final Transferencia transferencia;
    private PasoSaga pasoActual;
    private boolean compensando = false;

    public SagaContext(Transferencia transferencia) {
        this.transferencia = transferencia;
        this.pasoActual = PasoSaga.VALIDAR_DATOS;
    }

    public Transferencia getTransferencia() {
        return transferencia;
    }

    public PasoSaga getPasoActual() {
        return pasoActual;
    }

    public void setPasoActual(PasoSaga pasoActual) {
        this.pasoActual = pasoActual;
    }

    public boolean isCompensando() {
        return compensando;
    }

    public void setCompensando(boolean compensando) {
        this.compensando = compensando;
    }

    public EstadoTransferencia getEstadoActual() {
        return transferencia.getEstado();
    }
}
