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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Paso 3 de la saga: VALIDAR_LIMITES
 *
 * Verifica:
 * - Límite diario de transferencias
 * - Límite por transacción
 * - Límite mensual acumulado
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
@Slf4j
public class ValidarLimitesStep implements SagaStep {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaEstadoRepository estadoRepository;

    @Value("${banking.transfer.limit.daily:5000.00}")
    private BigDecimal limiteDiario;

    @Value("${banking.transfer.limit.per.transaction:2000.00}")
    private BigDecimal limitePorTransaccion;

    @Value("${banking.transfer.limit.monthly:20000.00}")
    private BigDecimal limiteMensual;

    public ValidarLimitesStep(TransferenciaRepository transferenciaRepository,
                              TransferenciaEstadoRepository estadoRepository) {
        this.transferenciaRepository = transferenciaRepository;
        this.estadoRepository = estadoRepository;
    }

    @Override
    public PasoSaga getPaso() {
        return PasoSaga.VALIDAR_LIMITES;
    }

    @Override
    public void execute(SagaContext context) throws SagaStepException {
        Transferencia transferencia = context.getTransferencia();
        log.info("[Paso 3] VALIDAR_LIMITES: transferencia={}, monto={}",
                transferencia.getNumeroTransferencia(), transferencia.getMonto());

        BigDecimal monto = transferencia.getMonto();

        // Validar límite por transacción
        if (monto.compareTo(limitePorTransaccion) > 0) {
            throw new SagaStepException(PasoSaga.VALIDAR_LIMITES,
                    "El monto excede el límite por transacción: " + limitePorTransaccion, false);
        }

        // Validar límite diario acumulado
        BigDecimal acumuladoDiario = calcularAcumuladoDiario(transferencia);
        if (acumuladoDiario.add(monto).compareTo(limiteDiario) > 0) {
            throw new SagaStepException(PasoSaga.VALIDAR_LIMITES,
                    "El monto excede el límite diario. Acumulado: " + acumuladoDiario + ", disponible: " +
                    limiteDiario.subtract(acumuladoDiario), false);
        }

        // Validar límite mensual acumulado
        BigDecimal acumuladoMensual = calcularAcumuladoMensual(transferencia);
        if (acumuladoMensual.add(monto).compareTo(limiteMensual) > 0) {
            throw new SagaStepException(PasoSaga.VALIDAR_LIMITES,
                    "El monto excede el límite mensual. Acumulado: " + acumuladoMensual + ", disponible: " +
                    limiteMensual.subtract(acumuladoMensual), false);
        }

        actualizarEstado(transferencia, EstadoTransferencia.AUTORIZADA,
                "Límites validados. Diario acumulado: " + acumuladoDiario + ", Mensual: " + acumuladoMensual);

        log.info("[Paso 3] VALIDAR_LIMITES completado exitosamente");
    }

    private BigDecimal calcularAcumuladoDiario(Transferencia transferencia) {
        LocalDateTime inicioDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        var transferencias = transferenciaRepository.findByFechaIniciadaBetweenAndIdCuentaOrigen(
                inicioDia, LocalDateTime.now(), transferencia.getIdCuentaOrigen());

        return transferencias.stream()
                .filter(t -> t.getEstado() == EstadoTransferencia.COMPLETADA ||
                             t.getEstado() == EstadoTransferencia.PENDIENTE ||
                             t.getEstado() == EstadoTransferencia.AUTORIZADA)
                .map(Transferencia::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularAcumuladoMensual(Transferencia transferencia) {
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        var transferencias = transferenciaRepository.findByFechaIniciadaBetweenAndIdCuentaOrigen(
                inicioMes, LocalDateTime.now(), transferencia.getIdCuentaOrigen());

        return transferencias.stream()
                .filter(t -> t.getEstado() == EstadoTransferencia.COMPLETADA ||
                             t.getEstado() == EstadoTransferencia.PENDIENTE ||
                             t.getEstado() == EstadoTransferencia.AUTORIZADA)
                .map(Transferencia::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void actualizarEstado(Transferencia transferencia, EstadoTransferencia nuevoEstado, String descripcion) {
        EstadoTransferencia estadoAnterior = transferencia.getEstado();
        transferencia.setEstado(nuevoEstado);
        transferencia.setPasoSagaActual(PasoSaga.VALIDAR_LIMITES.getCodigo());

        transferenciaRepository.save(transferencia);

        TransferenciaEstado estado = new TransferenciaEstado();
        estado.setIdTransferencia(transferencia.getId());
        estado.setEstadoAnterior(estadoAnterior.name());
        estado.setEstadoNuevo(nuevoEstado.name());
        estado.setPasoSaga(PasoSaga.VALIDAR_LIMITES.getCodigo());
        estado.setDescripcion(descripcion);
        estado.setFechaCambio(LocalDateTime.now());
        estadoRepository.save(estado);
    }
}
