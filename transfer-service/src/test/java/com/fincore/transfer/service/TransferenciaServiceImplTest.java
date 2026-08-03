package com.fincore.transfer.service;

import com.fincore.transfer.dto.request.CrearTransferenciaRequest;
import com.fincore.transfer.dto.response.TransferenciaResponse;
import com.fincore.transfer.entity.Transferencia;
import com.fincore.transfer.enums.EstadoTransferencia;
import com.fincore.transfer.repository.TransferenciaEstadoRepository;
import com.fincore.transfer.repository.TransferenciaRepository;
import com.fincore.transfer.saga.SagaOrchestrator;
import com.fincore.transfer.saga.SagaResult;
import com.fincore.transfer.websocket.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransferenciaServiceImplTest {

    private TransferenciaRepository transferenciaRepository;
    private TransferenciaEstadoRepository estadoRepository;
    private NumeradorService numeradorService;
    private SagaOrchestrator sagaOrchestrator;
    private WebSocketService webSocketService;
    private TransferenciaServiceImpl service;

    @BeforeEach
    void setUp() {
        transferenciaRepository = mock(TransferenciaRepository.class);
        estadoRepository = mock(TransferenciaEstadoRepository.class);
        numeradorService = mock(NumeradorService.class);
        sagaOrchestrator = mock(SagaOrchestrator.class);
        webSocketService = mock(WebSocketService.class);
        service = new TransferenciaServiceImpl(
                transferenciaRepository, estadoRepository, numeradorService,
                sagaOrchestrator, webSocketService);
    }

    @Test
    void crearTransferencia_debeGenerarNumeroYTraceId() {
        when(numeradorService.generarNumeroTransferencia()).thenReturn("TRANS-20260802-1430-000001");
        when(numeradorService.generarTraceId()).thenReturn("TRACE-202608021430000-00001");
        when(sagaOrchestrator.ejecutarSaga(any())).thenReturn(SagaResult.success());

        CrearTransferenciaRequest request = new CrearTransferenciaRequest();
        request.setIdCuentaOrigen(1L);
        request.setNumeroCuentaOrigen("12345678");
        request.setIdCuentaDestino(2L);
        request.setNumeroCuentaDestino("87654321");
        request.setMonto(BigDecimal.valueOf(1000.00));
        request.setMoneda("USD");
        request.setNombreBeneficiario("Juan Perez");
        request.setConcepto("Pago factura");

        when(transferenciaRepository.save(any(Transferencia.class))).thenAnswer(invocation -> {
            Transferencia t = invocation.getArgument(0);
            t.setId(999L);
            return t;
        });

        TransferenciaResponse response = service.crearTransferencia(request, "user-123", "127.0.0.1");

        assertNotNull(response);
        assertEquals("TRANS-20260802-1430-000001", response.getNumeroTransferencia());
        assertEquals("TRACE-202608021430000-00001", response.getTraceId());
        assertEquals(EstadoTransferencia.PENDIENTE, response.getEstado());

        ArgumentCaptor<Transferencia> captor = ArgumentCaptor.forClass(Transferencia.class);
        verify(transferenciaRepository).save(captor.capture());
        Transferencia saved = captor.getValue();
        assertEquals("TRANS-20260802-1430-000001", saved.getNumeroTransferencia());
        assertEquals(1L, saved.getIdCuentaOrigen());
        assertEquals(BigDecimal.valueOf(1000.00), saved.getMonto());
        assertEquals(EstadoTransferencia.PENDIENTE, saved.getEstado());
    }

    @Test
    void crearTransferencia_debeUsarMonedaPorDefectoUSD() {
        when(numeradorService.generarNumeroTransferencia()).thenReturn("TRANS-20260802-1430-000002");
        when(numeradorService.generarTraceId()).thenReturn("TRACE-202608021430001-00002");
        when(sagaOrchestrator.ejecutarSaga(any())).thenReturn(SagaResult.success());

        CrearTransferenciaRequest request = new CrearTransferenciaRequest();
        request.setIdCuentaOrigen(1L);
        request.setNumeroCuentaOrigen("12345678");
        request.setIdCuentaDestino(2L);
        request.setNumeroCuentaDestino("87654321");
        request.setMonto(BigDecimal.valueOf(500.00));
        request.setNombreBeneficiario("Maria Lopez");
        request.setConcepto("Pago servicios");

        when(transferenciaRepository.save(any(Transferencia.class))).thenAnswer(invocation -> {
            Transferencia t = invocation.getArgument(0);
            t.setId(100L);
            return t;
        });

        TransferenciaResponse response = service.crearTransferencia(request, "user-456", "192.168.1.1");

        ArgumentCaptor<Transferencia> captor = ArgumentCaptor.forClass(Transferencia.class);
        verify(transferenciaRepository).save(captor.capture());
        Transferencia saved = captor.getValue();
        assertEquals("USD", saved.getMoneda());
        assertEquals(BigDecimal.ZERO, saved.getComision());
    }

    @Test
    void crearTransferencia_debeGuardarEstadoInicial() {
        when(numeradorService.generarNumeroTransferencia()).thenReturn("TRANS-20260802-1430-000003");
        when(numeradorService.generarTraceId()).thenReturn("TRACE-202608021430002-00003");
        when(sagaOrchestrator.ejecutarSaga(any())).thenReturn(SagaResult.success());

        CrearTransferenciaRequest request = new CrearTransferenciaRequest();
        request.setIdCuentaOrigen(1L);
        request.setNumeroCuentaOrigen("12345678");
        request.setIdCuentaDestino(2L);
        request.setNumeroCuentaDestino("87654321");
        request.setMonto(BigDecimal.valueOf(200.00));
        request.setMoneda("US");
        request.setNombreBeneficiario("Pedro S");
        request.setConcepto("Gift");

        when(transferenciaRepository.save(any(Transferencia.class))).thenAnswer(invocation -> {
            Transferencia t = invocation.getArgument(0);
            t.setId(200L);
            return t;
        });

        TransferenciaResponse response = service.crearTransferencia(request, "user-789", "10.0.0.1");

        verify(estadoRepository).save(any());
    }

    @Test
    void crearTransferencia_debeEjecutarSagaAsincronamente() throws InterruptedException {
        when(numeradorService.generarNumeroTransferencia()).thenReturn("TRANS-20260802-1430-000004");
        when(numeradorService.generarTraceId()).thenReturn("TRACE-202608021430003-00004");

        CrearTransferenciaRequest request = new CrearTransferenciaRequest();
        request.setIdCuentaOrigen(1L);
        request.setNumeroCuentaOrigen("12345678");
        request.setIdCuentaDestino(2L);
        request.setNumeroCuentaDestino("87654321");
        request.setMonto(BigDecimal.valueOf(300.00));
        request.setMoneda("USD");
        request.setNombreBeneficiario("Test User");
        request.setConcepto("Test");

        when(transferenciaRepository.save(any(Transferencia.class))).thenAnswer(invocation -> {
            Transferencia t = invocation.getArgument(0);
            t.setId(300L);
            return t;
        });

        when(sagaOrchestrator.ejecutarSaga(any(Transferencia.class))).thenReturn(SagaResult.success());

        service.crearTransferencia(request, "user-999", "1.2.3.4");

        Thread.sleep(500);
        verify(sagaOrchestrator).ejecutarSaga(any(Transferencia.class));
    }
}
