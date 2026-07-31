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
 * Paso 8 de la saga: EJECUTAR_CREDITO
 *
 * Ejecuta el crédito en el ledger:
 * - Crea asiento contable CRÉDITO cuenta destino (liberando la transferencia de fondos en tránsito)
 * - Actualiza saldo_contable de destino (incrementa)
 *
 * Compensación: RevertirCreditoCompensation + RevertirDebitoCompensation + LiberarReservaCompensation
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class EjecutarCreditoStep implements SagaStep {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;
    private final AccountServiceGrpcClient accountClient;
    private final LedgerServiceGrpcClient ledgerClient;

    public EjecutarCreditoStep(TransferenciaRepository transferenciaRepository,
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
        return PasoSaga.EJECUTAR_CREDITO;
    }

    @Override
    public void execute(SagaContext context) throws SagaStepException {
        Transferencia transferencia = context.getTransferencia();
        log.info("[Paso 8] EJECUTAR_CREDITO: cuenta={}, monto={}",
                transferencia.getNumeroCuentaDestino(), transferencia.getMonto());

        // Aplicar crédito en account-service (actualiza saldos destino)
        boolean exitoCredito = accountClient.aplicarCredito(
                transferencia.getIdCuentaDestino(),
                transferencia.getMonto(),
                transferencia.getTraceId()
        );

        if (!exitoCredito) {
            // Compensar: revertir el débito ya aplicado
            accountClient.revertirDebito(transferencia.getIdCuentaOrigen(),
                    transferencia.getMonto(), transferencia.getTraceId());

            actualizarEstado(transferencia, EstadoTransferencia.ERROR,
                    "Error al aplicar crédito — débito revertido");
            throw new SagaStepException(PasoSaga.EJECUTAR_CREDITO,
                    "Error al aplicar crédito en cuenta destino", true);
        }

        // Crear asiento contable de crédito en ledger-service
        boolean asientoExitoso = ledgerClient.crearAsientoCredito(
                transferencia.getIdCuentaDestino(),
                transferencia.getMonto(),
                transferencia.getId(),
                "TRANSFERENCIA",
                transferencia.getIdUsuario(),
                transferencia.getIpOrigen(),
                transferencia.getTraceId()
        );

        if (!asientoExitoso) {
            // Compensar: revertir el crédito
            accountClient.revertirCredito(transferencia.getIdCuentaDestino(),
                    transferencia.getMonto(), transferencia.getTraceId());
            // También revertir el débito
            accountClient.revertirDebito(transferencia.getIdCuentaOrigen(),
                    transferencia.getMonto(), transferencia.getTraceId());

            actualizarEstado(transferencia, EstadoTransferencia.ERROR,
                    "Error al crear asiento de crédito — crédito y débito revertidos");
            throw new SagaStepException(PasoSaga.EJECUTAR_CREDITO,
                    "Error al crear asiento de crédito en ledger", true);
        }

        actualizarEstado(transferencia, EstadoTransferencia.COMPLETADA,
                "Crédito ejecutado exitosamente");

        log.info("[Paso 8] EJECUTAR_CREDITO completado exitosamente");
    }

    private void actualizarEstado(Transferencia transferencia, EstadoTransferencia nuevoEstado, String descripcion) {
        EstadoTransferencia estadoAnterior = transferencia.getEstado();
        transferencia.setEstado(nuevoEstado);
        transferencia.setPasoSagaActual(PasoSaga.EJECUTAR_CREDITO.getCodigo());

        transferenciaRepository.save(transferencia);

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(estadoAnterior.name());
        estado.setEstadoNuevo(nuevoEstado.name());
        estado.setPasoSaga(PasoSaga.EJECUTAR_CREDITO.getCodigo());
        estado.setDescripcion(descripcion);
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);
    }
}
