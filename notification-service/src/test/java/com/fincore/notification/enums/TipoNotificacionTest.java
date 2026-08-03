package com.fincore.notification.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TipoNotificacionTest {

    @Test
    void fromTopico_debeRetornarTipoCorrecto() {
        assertEquals(TipoNotificacion.TRANSFERENCIA_INICIADA, TipoNotificacion.fromTopico("transferencia.iniciada"));
        assertEquals(TipoNotificacion.TRANSFERENCIA_COMPLETADA, TipoNotificacion.fromTopico("transferencia.completada"));
        assertEquals(TipoNotificacion.TRANSFERENCIA_FALLIDA, TipoNotificacion.fromTopico("transferencia.fallida"));
        assertEquals(TipoNotificacion.TRANSFERENCIA_REVERTIDA, TipoNotificacion.fromTopico("transferencia.revertida"));
    }

    @Test
    void fromTopico_debeRetornarNullParaTopicoDesconocido() {
        assertNull(TipoNotificacion.fromTopico("topico.desconocido"));
    }

    @Test
    void getTitulo_debeRetornarTituloCorrecto() {
        assertEquals("Transferencia iniciada", TipoNotificacion.TRANSFERENCIA_INICIADA.getTitulo());
        assertEquals("Transferencia completada", TipoNotificacion.TRANSFERENCIA_COMPLETADA.getTitulo());
    }
}
