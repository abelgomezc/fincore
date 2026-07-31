package com.fincore.transfer.saga;

import com.fincore.transfer.enums.PasoSaga;

/**
 * Excepción lanzada cuando un paso de la saga falla.
 *
 * Contiene información sobre:
 * - Qué paso falló
 * - El mensaje de error
 * - Si se requiere compensación
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class SagaStepException extends Exception {

    private final PasoSaga pasoFallido;
    private final boolean requiereCompensacion;

    public SagaStepException(PasoSaga pasoFallido, String mensaje, boolean requiereCompensacion) {
        super(mensaje);
        this.pasoFallido = pasoFallido;
        this.requiereCompensacion = requiereCompensacion;
    }

    public SagaStepException(PasoSaga pasoFallido, String mensaje, boolean requiereCompensacion, Throwable cause) {
        super(mensaje, cause);
        this.pasoFallido = pasoFallido;
        this.requiereCompensacion = requiereCompensacion;
    }

    public PasoSaga getPasoFallido() {
        return pasoFallido;
    }

    public boolean isRequiereCompensacion() {
        return requiereCompensacion;
    }
}
