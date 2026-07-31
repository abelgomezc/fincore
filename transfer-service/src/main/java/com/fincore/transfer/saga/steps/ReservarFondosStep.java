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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Paso 5 de la saga: RESERVAR_FONDOS
 *
 * Reserva los fondos en la cuenta origen:
 * - Incrementa saldo_retenido
 * - Decrementa saldo_disponible
 * - Crea asiento contable de retención
 *
 * Compensación: LiberarReservaCompensation
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class ReservarFondosStep implements SagaStep {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;
    private final com.fincore.transfer.client.AccountServiceGrpcClient accountClient;
    private final com.fincore.transfer.client.LedgerServiceGrpcClient ledgerClient;

    public ReservarFondosStep(TransferenciaRepository transferenciaRepository,
                              TransferenciaEstadoRepository estadoRepository,
                              com.fincore.transfer.client.AccountServiceGrpcClient accountClient,
                              com.fincore.transfer.client.LedgerServiceGrpcClient ledgerClient) {
        this.transferenciaRepository = transferenciaRepository;
        this.estadoRepository = estadoRepository;
        this.accountClient = accountClient;
        this.ledgerClient = ledgerClient;
    }

    @Override
    public PasoSaga getPaso() {
        return PasoSaga.RESERVAR_FONDOS;
    }

    @Override
    public void execute(SagaContext context) throws SagaStepException {
        Transferencia transferencia = context.getTransferencia();
        log.info("[Paso 5] RESERVAR_FONDOS: cuenta={}, monto={}",
                transferencia.getNumeroCuentaOrigen(), transferencia.getMonto());

        // Reservar fondos vía gRPC al account-service
        boolean exito = accountClient.reservarFondos(
                transferencia.getIdCuentaOrigen(),
                transferencia.getMonto(),
                transferencia.getTraceId()
        );

        if (!exito) {
            actualizarEstado(transferencia, EstadoTransferencia.ERROR,
                    "Error al reservar fondos");
            throw new SagaStepException(PasoSaga.RESERVAR_FONDOS,
                    "No se pudieron reservar los fondos en la cuenta origen", true);
        }

        // Crear asiento contable de retención vía gRPC al ledger-service
        boolean asientoExitoso = ledgerClient.crearAsientoRetencion(
                transferencia.getIdCuentaOrigen(),
                transferencia.getMonto(),
                transferencia.getId(),
                "TRANSFERENCIA",
                transferencia.getIdUsuario(),
                transferencia.getIpOrigen(),
                transferencia.getTraceId()
        );

        if (!asientoExitoso) {
            // Compensar: liberar la reserva
            accountClient.liberarReserva(transferencia.getIdCuentaOrigen(),
                    transferencia.getMonto(), transferencia.getTraceId());
            actualizarEstado(transferencia, EstadoTransferencia.ERROR,
                    "Error al crear asiento de retención — reserva liberada");
            throw new SagaStepException(PasoSaga.RESERVAR_FONDOS,
                    "Error al crear asiento contable de retención", true);
        }

        actualizarEstado(transferencia, EstadoTransferencia.PROCESANDO,
                "Fondos reservados y asiento de retención creado");

        log.info("[Paso 5] RESERVAR_FONDOS completado exitosamente");
    }

    private void actualizarEstado(Transferencia transferencia, EstadoTransferencia nuevoEstado, String descripcion) {
        EstadoTransferencia estadoAnterior = transferencia.getEstado();
        transferencia.setEstado(nuevoEstado);
        transferencia.setPasoSagaActual(PasoSaga.RESERVAR_FONDOS.getCodigo());

        transferenciaRepository.save(transferencia);

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(estadoAnterior.name());
        estado.setEstadoNuevo(nuevoEstado.name());
        estado.setPasoSaga(PasoSaga.RESERVAR_FONDOS.getCodigo());
        estado.setDescripcion(descripcion);
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);
    }
}
