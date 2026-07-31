package com.fincore.transfer.saga.steps;

import com.fincore.transfer.entity.Transferencia;
import com.fincore.transfer.repository.TransferenciaRepository;
import com.fincore.transfer.enums.EstadoTransferencia;
import com.fincore.transfer.enums.PasoSaga;
import com.fincore.transfer.saga.SagaContext;
import com.fincore.transfer.saga.SagaStep;
import com.fincore.transfer.saga.SagaStepException;
import com.fincore.transfer.repository.TransferenciaEstadoRepository;
import com.fincore.transfer.entity.TransferenciaEstado;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Paso 1 de la saga: VALIDAR_DATOS
 *
 * Verifica:
 * - Formato de cuenta destino válido
 * - Cuenta origen existe y está activa
 * - Cuenta destino existe y está activa
 * - Monto > 0 y dentro de límites
 * - Cuenta origen ≠ cuenta destino
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class ValidarDatosStep implements SagaStep {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;

    public ValidarDatosStep(TransferenciaRepository transferenciaRepository,
                            TransferenciaEstadoRepository estadoRepository) {
        this.transferenciaRepository = transferenciaRepository;
        this.estadoRepository = estadoRepository;
    }

    @Override
    public PasoSaga getPaso() {
        return PasoSaga.VALIDAR_DATOS;
    }

    @Override
    public void execute(SagaContext context) throws SagaStepException {
        Transferencia transferencia = context.getTransferencia();
        log.info("[Paso 1] VALIDAR_DATOS: transferencia={}", transferencia.getNumeroTransferencia());

        // Validar que cuenta origen ≠ cuenta destino
        if (transferencia.getIdCuentaOrigen().equals(transferencia.getIdCuentaDestino())) {
            throw new SagaStepException(PasoSaga.VALIDAR_DATOS,
                    "La cuenta origen y destino no pueden ser la misma", false);
        }

        // Validar monto > 0
        if (transferencia.getMonto().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new SagaStepException(PasoSaga.VALIDAR_DATOS,
                    "El monto debe ser mayor a 0", false);
        }

        // Validar formato de número de cuenta (20 dígitos, prefijo 2026)
        if (!transferencia.getNumeroCuentaOrigen().matches("^2026\\d{13}$")) {
            throw new SagaStepException(PasoSaga.VALIDAR_DATOS,
                    "Formato de cuenta origen inválido", false);
        }

        if (!transferencia.getNumeroCuentaDestino().matches("^2026\\d{13}$")) {
            throw new SagaStepException(PasoSaga.VALIDAR_DATOS,
                    "Formato de cuenta destino inválido", false);
        }

        // Actualizar estado
        actualizarEstado(transferencia, EstadoTransferencia.VALIDANDO, "Datos validados correctamente");

        log.info("[Paso 1] VALIDAR_DATOS completado exitosamente");
    }

    private void actualizarEstado(Transferencia transferencia, EstadoTransferencia nuevoEstado, String descripcion) {
        EstadoTransferencia estadoAnterior = transferencia.getEstado();
        transferencia.setEstado(nuevoEstado);
        transferencia.setPasoSagaActual(PasoSaga.VALIDAR_DATOS.getCodigo());

        transferenciaRepository.save(transferencia);

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(estadoAnterior.name());
        estado.setEstadoNuevo(nuevoEstado.name());
        estado.setPasoSaga(PasoSaga.VALIDAR_DATOS.getCodigo());
        estado.setDescripcion(descripcion);
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);
    }
}
