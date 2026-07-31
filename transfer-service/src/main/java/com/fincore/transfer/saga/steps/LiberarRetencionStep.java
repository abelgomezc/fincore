package com.fincore.transfer.saga.steps;

import com.fincore.transfer.entity.Transferencia;
import com.fincore.transfer.enums.EstadoTransferencia;
import com.fincore.transfer.enums.PasoSaga;
import com.fincore.transfer.saga.SagaContext;
import com.fincore.transfer.saga.SagaStep;
import com.fincore.transfer.saga.SagaStepException;
import com.fincore.transfer.repository.TransferenciaEstadoRepository;
import com.fincore.transfer.repository.TransferenciaRepository;
import com.fincore.transfer.entity.TransferenciaEstado;
import com.fincore.transfer.client.AccountServiceGrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Paso 9 de la saga: LIBERAR_RETENCION
 *
 * Libera la retención de fondos en la cuenta origen:
 * - Decrementa saldo_retenido
 * - Incrementa saldo_disponible
 * - Limpia fondos en tránsito
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class LiberarRetencionStep implements SagaStep {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;
    private final AccountServiceGrpcClient accountClient;

    public LiberarRetencionStep(TransferenciaRepository transferenciaRepository,
                                TransferenciaEstadoRepository estadoRepository,
                                AccountServiceGrpcClient accountClient) {
        this.transferenciaRepository = transferenciaRepository;
        this.estadoRepository = estadoRepository;
        this.accountClient = accountClient;
    }

    @Override
    public PasoSaga getPaso() {
        return PasoSaga.LIBERAR_RETENCION;
    }

    @Override
    public void execute(SagaContext context) throws SagaStepException {
        Transferencia transferencia = context.getTransferencia();
        log.info("[Paso 9] LIBERAR_RETENCION: cuenta={}, monto={}",
                transferencia.getNumeroCuentaOrigen(), transferencia.getMonto());

        boolean exito = accountClient.liberarReserva(
                transferencia.getIdCuentaOrigen(),
                transferencia.getMonto(),
                transferencia.getTraceId()
        );

        if (!exito) {
            actualizarEstado(transferencia, EstadoTransferencia.ERROR,
                    "Error al liberar retención — se requiere intervención manual");
            throw new SagaStepException(PasoSaga.LIBERAR_RETENCION,
                    "Error al liberar retención de fondos. Requiere intervención manual.", false);
        }

        actualizarEstado(transferencia, EstadoTransferencia.COMPLETADA,
                "Retención liberada exitosamente");

        log.info("[Paso 9] LIBERAR_RETENCION completado exitosamente");
    }

    private void actualizarEstado(Transferencia transferencia, EstadoTransferencia nuevoEstado, String descripcion) {
        EstadoTransferencia estadoAnterior = transferencia.getEstado();
        transferencia.setEstado(nuevoEstado);
        transferencia.setPasoSagaActual(PasoSaga.LIBERAR_RETENCION.getCodigo());

        transferenciaRepository.save(transferencia);

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(estadoAnterior.name());
        estado.setEstadoNuevo(nuevoEstado.name());
        estado.setPasoSaga(PasoSaga.LIBERAR_RETENCION.getCodigo());
        estado.setDescripcion(descripcion);
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);
    }
}
