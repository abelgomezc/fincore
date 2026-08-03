package com.fincore.transfer.saga;

import com.fincore.transfer.entity.Transferencia;
import com.fincore.transfer.enums.PasoSaga;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SagaOrchestratorTest {

    @Test
    void sagaContext_debeInicializarConTransferencia() {
        Transferencia t = new Transferencia();
        SagaContext context = new SagaContext(t);

        assertSame(t, context.getTransferencia());
        assertEquals(PasoSaga.VALIDAR_DATOS, context.getPasoActual());
        assertFalse(context.isCompensando());
    }

    @Test
    void sagaContext_debePermitirCambiarPaso() {
        Transferencia t = new Transferencia();
        SagaContext context = new SagaContext(t);
        context.setPasoActual(PasoSaga.VERIFICAR_KYC);
        assertEquals(PasoSaga.VERIFICAR_KYC, context.getPasoActual());
    }

    @Test
    void sagaContext_debePermitirMarcarCompensando() {
        Transferencia t = new Transferencia();
        SagaContext context = new SagaContext(t);
        context.setCompensando(true);
        assertTrue(context.isCompensando());
    }

    @Test
    void pasosSaga_debeTener12Pasos() {
        assertEquals(12, PasoSaga.values().length);
    }

    @Test
    void pasosSaga_debeTenerOrdenesCorrectos() {
        PasoSaga[] steps = PasoSaga.values();
        for (int i = 0; i < steps.length - 1; i++) {
            assertTrue(steps[i].getOrden() < steps[i + 1].getOrden(),
                    "Step " + steps[i] + " should come before " + steps[i + 1]);
        }
    }
}
