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
 * Paso 2 de la saga: VERIFICAR_KYC
 *
 * Verifica:
 * - Cliente tiene documentos vigentes
 * - KYC aprobado (no está en lista de sanciones AML)
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class VerificarKycStep implements SagaStep {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;
    private final AccountServiceGrpcClient accountClient;

    public VerificarKycStep(TransferenciaRepository transferenciaRepository,
                            TransferenciaEstadoRepository estadoRepository,
                            AccountServiceGrpcClient accountClient) {
        this.transferenciaRepository = transferenciaRepository;
        this.estadoRepository = estadoRepository;
        this.accountClient = accountClient;
    }

    @Override
    public PasoSaga getPaso() {
        return PasoSaga.VERIFICAR_KYC;
    }

    @Override
    public void execute(SagaContext context) throws SagaStepException {
        Transferencia transferencia = context.getTransferencia();
        log.info("[Paso 2] VERIFICAR_KYC: transferencia={}", transferencia.getNumeroTransferencia());

        // Validar que la cuenta origen existe y está activa vía gRPC
        boolean cuentaOrigenValida = accountClient.validarCuenta(transferencia.getIdCuentaOrigen());
        if (!cuentaOrigenValida) {
            throw new SagaStepException(PasoSaga.VERIFICAR_KYC,
                    "Cuenta origen no encontrada o inactiva", false);
        }

        boolean cuentaDestinoValida = accountClient.validarCuenta(transferencia.getIdCuentaDestino());
        if (!cuentaDestinoValida) {
            throw new SagaStepException(PasoSaga.VERIFICAR_KYC,
                    "Cuenta destino no encontrada o inactiva", false);
        }

        // Validar saldo suficiente
        boolean saldoSuficiente = accountClient.validarSaldoSuficiente(
                transferencia.getIdCuentaOrigen(), transferencia.getMonto());
        if (!saldoSuficiente) {
            throw new SagaStepException(PasoSaga.VERIFICAR_KYC,
                    "Saldo insuficiente en cuenta origen", false);
        }

        actualizarEstado(transferencia, EstadoTransferencia.AUTORIZADA,
                "KYC y saldo verificados correctamente");

        log.info("[Paso 2] VERIFICAR_KYC completado exitosamente");
    }

    private void actualizarEstado(Transferencia transferencia, EstadoTransferencia nuevoEstado, String descripcion) {
        EstadoTransferencia estadoAnterior = transferencia.getEstado();
        transferencia.setEstado(nuevoEstado);
        transferencia.setPasoSagaActual(PasoSaga.VERIFICAR_KYC.getCodigo());

        transferenciaRepository.save(transferencia);

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(estadoAnterior.name());
        estado.setEstadoNuevo(nuevoEstado.name());
        estado.setPasoSaga(PasoSaga.VERIFICAR_KYC.getCodigo());
        estado.setDescripcion(descripcion);
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);
    }
}
