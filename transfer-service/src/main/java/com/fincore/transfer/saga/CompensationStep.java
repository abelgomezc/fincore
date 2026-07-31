package com.fincore.transfer.saga;

import com.fincore.transfer.enums.PasoSaga;

/**
 * Interface para una transacción de compensación.
 *
 * Se ejecuta en orden inverso cuando un paso de la saga falla,
 * para deshacer los cambios ya realizados.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface CompensationStep {

    void compensate(SagaContext context);

    String getStepName();

    /**
     * Devuelve el PasoSaga que esta compensación reversa.
     */
    PasoSaga getPasoCompensado();
}
