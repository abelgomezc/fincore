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
import com.fincore.transfer.client.LedgerServiceGrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Paso 7 de la saga: EJECUTAR_DEBITO
 *
 * Ejecuta el débito en el ledger:
 * - Crea asiento contable DEBITO cuenta origen / CRÉDITO fondos en tránsito
 * - Actualiza saldo_contable de origen (decrementa)
 *
 * Compensación: RevertirDebitoCompensation + LiberarReservaCompensation
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class EjecutarDebitoStep implements SagaStep {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;
    private final AccountServiceGrpcClient accountClient;
    private final LedgerServiceGrpcClient ledgerClient;

    public EjecutarDebitoStep(TransferenciaRepository transferenciaRepository,
                              TransferenciaEstadoRepository estadoRepository,
                              AccountServiceGrpcClient accountClient,
                              LedgerServiceGrpcClient ledgerClient) {
        this.transferenciaRepository = transferenciaRepository;
        this.estadoRepository = estadoRepository;
        this.accountClient = accountClient;
        this.ledgerClient = ledgerClient;
    }

    @Override
    public PasoSaga getPaso() {
        return PasoSaga.EJECUTAR_DEBITO;
    }

    @Override
    public void execute(SagaContext context) throws SagaStepException {
        Transferencia transferencia = context.getTransferencia();
        log.info("[Paso 7] EJECUTAR_DEBITO: cuenta={}, monto={}",
                transferencia.getNumeroCuentaOrigen(), transferencia.getMonto());

        // Aplicar débito en account-service (actualiza saldos)
        boolean exitoDebito = accountClient.aplicarDebito(
                transferencia.getIdCuentaOrigen(),
                transferencia.getMonto(),
                transferencia.getTraceId()
        );

        if (!exitoDebito) {
            actualizarEstado(transferencia, EstadoTransferencia.ERROR,
                    "Error al aplicar débito en cuenta origen");
            throw new SagaStepException(PasoSaga.EJECUTAR_DEBITO,
                    "Error al aplicar débito en cuenta origen", true);
        }

        // Crear asiento contable de débito en ledger-service
        boolean asientoExitoso = ledgerClient.crearAsientoDebito(
                transferencia.getIdCuentaOrigen(),
                transferencia.getMonto(),
                transferencia.getId(),
                "TRANSFERENCIA",
                transferencia.getIdUsuario(),
                transferencia.getIpOrigen(),
                transferencia.getTraceId()
        );

        if (!asientoExitoso) {
            // Compensar: revertir el débito en account-service
            accountClient.revertirDebito(transferencia.getIdCuentaOrigen(),
                    transferencia.getMonto(), transferencia.getTraceId());
            actualizarEstado(transferencia, EstadoTransferencia.ERROR,
                    "Error al crear asiento de débito — débito revertido");
            throw new SagaStepException(PasoSaga.EJECUTAR_DEBITO,
                    "Error al crear asiento de débito en ledger", true);
        }

        // Publicar evento Kafka
        transferencia.setPasoSagaActual(PasoSaga.EJECUTAR_DEBITO.getCodigo());
        transferenciaRepository.save(transferencia);

        actualizarEstado(transferencia, EstadoTransferencia.ACREDITANDO,
                "Débito ejecutado exitosamente");

        log.info("[Paso 7] EJECUTAR_DEBITO completado exitosamente");
    }

    private void actualizarEstado(Transferencia transferencia, EstadoTransferencia nuevoEstado, String descripcion) {
        EstadoTransferencia estadoAnterior = transferencia.getEstado();
        transferencia.setEstado(nuevoEstado);
        transferencia.setPasoSagaActual(PasoSaga.EJECUTAR_DEBITO.getCodigo());

        transferenciaRepository.save(transferencia);

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(estadoAnterior.name());
        estado.setEstadoNuevo(nuevoEstado.name());
        estado.setPasoSaga(PasoSaga.EJECUTAR_DEBITO.getCodigo());
        estado.setDescripcion(descripcion);
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);
    }
}
