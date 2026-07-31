package com.fincore.transfer.saga;

import com.fincore.transfer.enums.PasoSaga;
import com.fincore.transfer.entity.Transferencia;

/**
 * Interface para un paso de la saga.
 *
 * Cada paso de la saga (12 pasos) implementa esta interfaz.
 * Si el paso falla, se lanza una SagaStepException que contiene
 * el paso fallido para que el orquestador ejecute compensaciones.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface SagaStep {

    /**
     * Ejecuta el paso de la saga.
     *
     * @param context Contexto de la saga
     * @throws SagaStepException si el paso falla
     */
    void execute(SagaContext context) throws SagaStepException;

    /**
     * Devuelve el paso de la saga que representa este step.
     */
    PasoSaga getPaso();
}
