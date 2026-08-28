package com.fincore.document.service.impl;

import com.fincore.document.dto.request.CrearPlantillaRequest;
import com.fincore.document.dto.response.PlantillaResponse;
import com.fincore.document.enums.TipoDocumento;
import com.fincore.document.repository.DocumentoPlantillaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentoServiceImplTest {

    @Mock
    private DocumentoPlantillaRepository plantillaRepository;

    @InjectMocks
    private DocumentoServiceImpl service;

    @Test
    void crearPlantilla_deberiaGuardarYRetornar() {
        CrearPlantillaRequest request = new CrearPlantillaRequest(
                TipoDocumento.CONTRATO_PRESTAMO,
                "Contrato Demo",
                "Plantilla de prueba",
                "<html><body>Contrato</body></html>"
        );

        when(plantillaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PlantillaResponse response = service.crearPlantilla(request);

        assertNotNull(response);
        assertEquals("Contrato Demo", response.getNombre());
        assertEquals(TipoDocumento.CONTRATO_PRESTAMO.name(), response.getTipoDocumento());
        verify(plantillaRepository, times(1)).save(any());
    }

    @Test
    void listarPlantillas_deberiaRetornarLista() {
        when(plantillaRepository.findByActivoTrue()).thenReturn(List.of());

        List<PlantillaResponse> response = service.listarPlantillas();

        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(plantillaRepository, times(1)).findByActivoTrue();
    }
}
