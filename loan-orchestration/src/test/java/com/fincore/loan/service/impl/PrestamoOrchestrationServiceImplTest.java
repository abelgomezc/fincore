package com.fincore.loan.service.impl;

import com.fincore.loan.dto.request.CrearSolicitudPrestamoRequest;
import com.fincore.loan.dto.response.SolicitudPrestamoResponse;
import com.fincore.loan.enums.TipoPrestamo;
import com.fincore.loan.kafka.PrestamoEventProducer;
import com.fincore.loan.repository.EvaluacionRiesgoRepository;
import com.fincore.loan.repository.HistorialSolicitudPrestamoRepository;
import com.fincore.loan.repository.SolicitudPrestamoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestamoOrchestrationServiceImplTest {

    @Mock
    private SolicitudPrestamoRepository solicitudRepository;

    @Mock
    private EvaluacionRiesgoRepository evaluacionRiesgoRepository;

    @Mock
    private HistorialSolicitudPrestamoRepository historialRepository;

    @Mock
    private PrestamoEventProducer eventProducer;

    @InjectMocks
    private PrestamoOrchestrationServiceImpl service;

    @Test
    void crearSolicitud_deberiaGenerarNumeroYGuardar() {
        CrearSolicitudPrestamoRequest request = new CrearSolicitudPrestamoRequest(
                1L,
                TipoPrestamo.PERSONAL,
                new BigDecimal("10000"),
                24,
                new BigDecimal("18.0"),
                "test"
        );

        when(solicitudRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SolicitudPrestamoResponse response = service.crearSolicitud(request);

        assertNotNull(response);
        assertNotNull(response.getNumeroSolicitud());
        assertTrue(response.getNumeroSolicitud().startsWith("SOL-"));
        assertEquals("PERSONAL", response.getTipoPrestamo());
        assertEquals(new BigDecimal("10000"), response.getMontoSolicitado());
        verify(solicitudRepository, times(1)).save(any());
    }

    @Test
    void listarSolicitudes_deberiaRetornarLista() {
        when(solicitudRepository.findAll()).thenReturn(List.of());

        List<SolicitudPrestamoResponse> response = service.listarSolicitudes();

        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(solicitudRepository, times(1)).findAll();
    }
}
