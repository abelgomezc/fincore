package com.fincore.transfer.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumeradorServiceTest {

    @Test
    void generarNumeroTransferencia_debeTenerFormatoCorrecto() {
        NumeradorService service = new NumeradorService();
        String numero = service.generarNumeroTransferencia();

        assertNotNull(numero);
        assertTrue(numero.startsWith("TRANS-"));
        assertTrue(numero.length() > 4);
    }

    @Test
    void generarTraceId_debeSerUnico() {
        NumeradorService service = new NumeradorService();
        String trace1 = service.generarTraceId();
        String trace2 = service.generarTraceId();

        assertNotNull(trace1);
        assertNotNull(trace2);
        assertNotEquals(trace1, trace2);
    }
}
