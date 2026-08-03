import React from 'react';
import { PasoTransferencia, EstadoTransferencia, TransferenciaEstadoDetalle } from '@/types/transfer';
import { clsx } from '@/lib/utils';

interface TransferTimelineProps {
  estados: TransferenciaEstadoDetalle[];
  currentEstado: EstadoTransferencia;
  isLoading?: boolean;
}

const PASOS: { id: PasoTransferencia; label: string; icon: string }[] = [
  { id: 'VALIDAR_DATOS', label: 'Validando datos', icon: '🔍' },
  { id: 'VERIFICAR_KYC', label: 'Verificando KYC', icon: '🆔' },
  { id: 'VALIDAR_LIMITES', label: 'Validando límites', icon: '📏' },
  { id: 'EVALUAR_FRAUDE', label: 'Evaluación de fraude', icon: '🛡️' },
  { id: 'RESERVAR_FONDOS', label: 'Reservando fondos', icon: '🔒' },
  { id: 'CREAR_ASIENTO_DEBITO', label: 'Creando asiento débito', icon: '📝' },
  { id: 'APLICAR_DEBITO', label: 'Aplicando débito', icon: '💸' },
  { id: 'APLICAR_CREDITO', label: 'Aplicando crédito', icon: '💰' },
  { id: 'LIBERAR_RESERVA', label: 'Liberando reserva', icon: '🔓' },
  { id: 'REGISTRAR_AUDITORIA', label: 'Registrando auditoría', icon: '📊' },
  { id: 'COBRAR_COMISION', label: 'Cobrando comisión', icon: '💳' },
  { id: 'NOTIFICAR', label: 'Notificando', icon: '🔔' },
];

export const TransferTimeline: React.FC<TransferTimelineProps> = ({ estados, currentEstado, isLoading }) => {
  const estadoMap = new Map(estados.map((e) => [e.paso, e]));

  const getCurrentStepIndex = (): number => {
    const orden = PASOS.map((p) => p.id);
    const currentPaso = estados.find((e) => e.estado !== 'PENDIENTE')?.paso;
    if (!currentPaso) return -1;
    return orden.indexOf(currentPaso as PasoTransferencia);
  };

  const currentStep = getCurrentStepIndex();

  if (isLoading) {
    return (
      <div className="space-y-3 animate-pulse">
        {PASOS.map((_, i) => (
          <div key={i} className="h-12 bg-surface-800 rounded-lg"></div>
        ))}
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {PASOS.map((paso, index) => {
        const detalle = estadoMap.get(paso.id);
        const isCompleted = detalle?.exito === true;
        const isCurrent = index === currentStep;
        const isPending = !detalle;
        const isFailed = detalle?.exito === false;

        return (
          <div
            key={paso.id}
            className={clsx(
              'flex items-center space-x-4 p-3 rounded-lg border transition-all',
              isCompleted ? 'bg-banking-success/10 border-banking-success/30' :
              isCurrent ? 'bg-primary-600/10 border-primary-600/30 animate-pulse-slow' :
              isFailed ? 'bg-banking-error/10 border-banking-error/30' :
              'bg-surface-800 border-surface-700'
            )}
          >
            <div className={clsx(
              'w-10 h-10 rounded-full flex items-center justify-center text-lg',
              isCompleted ? 'bg-banking-success text-white' :
              isCurrent ? 'bg-primary-600 text-white' :
              isFailed ? 'bg-banking-error text-white' :
              'bg-surface-700 text-surface-500'
            )}>
              {isCompleted ? '✓' : isCurrent ? '⏳' : isFailed ? '✗' : paso.icon}
            </div>
            <div className="flex-1">
              <div className="flex items-center justify-between">
                <span className={clsx(
                  'font-medium',
                  isCompleted ? 'text-banking-success' :
                  isCurrent ? 'text-primary-400' :
                  isFailed ? 'text-banking-error' :
                  'text-surface-400'
                )}>
                  {paso.label}
                </span>
                {detalle?.duracionMs !== undefined && (
                  <span className="text-xs text-surface-500">
                    {detalle.duracionMs}ms
                  </span>
                )}
              </div>
              {detalle?.mensaje && (
                <p className="text-xs text-surface-500 mt-1">{detalle.mensaje}</p>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
};
