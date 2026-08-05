import React, { useEffect } from 'react';
import { PasoTransferencia, EstadoTransferencia, TransferenciaEstadoDetalle } from '@/types/transfer';
import { clsx } from '@/lib/utils';
import { motion, AnimatePresence } from 'framer-motion';
import {
  IconClock,
  IconSearch,
  IconCircleCheck,
  IconAlertCircle,
  IconLock,
  IconLoader2,
  IconArrowsRightLeft,
  IconX,
  IconRotate,
  IconAlertOctagon,
} from '@tabler/icons-react';

interface StepConfig {
  id: PasoTransferencia;
  label: string;
  icon: any;
}

const STEPS: StepConfig[] = [
  { id: 'VALIDAR_DATOS', label: 'Validando datos', icon: IconSearch },
  { id: 'VERIFICAR_KYC', label: 'Verificando KYC', icon: IconSearch },
  { id: 'VALIDAR_LIMITES', label: 'Validando límites', icon: IconSearch },
  { id: 'EVALUAR_FRAUDE', label: 'Evaluación de fraude', icon: IconAlertCircle },
  { id: 'RESERVAR_FONDOS', label: 'Reservando fondos', icon: IconLock },
  { id: 'CREAR_ASIENTO_DEBITO', label: 'Creando asiento débito', icon: IconLock },
  { id: 'APLICAR_DEBITO', label: 'Aplicando débito', icon: IconArrowsRightLeft },
  { id: 'APLICAR_CREDITO', label: 'Aplicando crédito', icon: IconArrowsRightLeft },
  { id: 'LIBERAR_RESERVA', label: 'Liberando reserva', icon: IconLock },
  { id: 'REGISTRAR_AUDITORIA', label: 'Registrando auditoría', icon: IconSearch },
  { id: 'COBRAR_COMISION', label: 'Cobrando comisión', icon: IconSearch },
  { id: 'NOTIFICAR', label: 'Notificando', icon: IconSearch },
];

type EstadoInfo = {
  icon: any;
  color: string;
  label: string;
  animate?: boolean;
  bgColor?: string;
  borderColor?: string;
  textColor?: string;
};

const ESTADO_MAP: Record<string, EstadoInfo> = {
  PENDIENTE: { icon: IconClock, color: 'text-slate-400', label: 'Pendiente', bgColor: 'bg-slate-100', borderColor: 'border-slate-200', textColor: 'text-slate-500' },
  VALIDANDO: { icon: IconSearch, color: 'text-blue-600', label: 'Validando', bgColor: 'bg-blue-50', borderColor: 'border-blue-200', textColor: 'text-blue-700', animate: true },
  VALIDADA: { icon: IconCircleCheck, color: 'text-blue-600', label: 'Autorizada', bgColor: 'bg-blue-50', borderColor: 'border-blue-200', textColor: 'text-blue-700' },
  AUTORIZADA: { icon: IconCircleCheck, color: 'text-blue-600', label: 'Autorizada', bgColor: 'bg-blue-50', borderColor: 'border-blue-200', textColor: 'text-blue-700' },
  EN_REVISION: { icon: IconAlertCircle, color: 'text-amber-600', label: 'En revisión', bgColor: 'bg-amber-50', borderColor: 'border-amber-200', textColor: 'text-amber-700', animate: true },
  RESERVANDO: { icon: IconLock, color: 'text-amber-600', label: 'Reservando fondos', bgColor: 'bg-amber-50', borderColor: 'border-amber-200', textColor: 'text-amber-700' },
  RESERVANDO_FONDOS: { icon: IconLock, color: 'text-amber-600', label: 'Reservando fondos', bgColor: 'bg-amber-50', borderColor: 'border-amber-200', textColor: 'text-amber-700' },
  FONDOS_RESERVADOS: { icon: IconLock, color: 'text-blue-600', label: 'Fondos reservados', bgColor: 'bg-blue-50', borderColor: 'border-blue-200', textColor: 'text-blue-700' },
  PROCESANDO: { icon: IconLoader2, color: 'text-blue-600', label: 'Procesando', bgColor: 'bg-blue-50', borderColor: 'border-blue-200', textColor: 'text-blue-700', animate: true },
  EJECUTANDO_DEBITO: { icon: IconLoader2, color: 'text-blue-600', label: 'Ejecutando débito', bgColor: 'bg-blue-50', borderColor: 'border-blue-200', textColor: 'text-blue-700', animate: true },
  DEBITO_APLICADO: { icon: IconArrowsRightLeft, color: 'text-blue-600', label: 'Débito aplicado', bgColor: 'bg-blue-50', borderColor: 'border-blue-200', textColor: 'text-blue-700' },
  EJECUTANDO_CREDITO: { icon: IconArrowsRightLeft, color: 'text-green-600', label: 'Acreditando', bgColor: 'bg-green-50', borderColor: 'border-green-200', textColor: 'text-green-700' },
  ACREDITANDO: { icon: IconArrowsRightLeft, color: 'text-green-600', label: 'Acreditando', bgColor: 'bg-green-50', borderColor: 'border-green-200', textColor: 'text-green-700' },
  CREDITO_APLICADO: { icon: IconArrowsRightLeft, color: 'text-green-600', label: 'Crédito aplicado', bgColor: 'bg-green-50', borderColor: 'border-green-200', textColor: 'text-green-700' },
  COMPLETADA: { icon: IconCircleCheck, color: 'text-green-600', label: 'Completada', bgColor: 'bg-green-50', borderColor: 'border-green-200', textColor: 'text-green-700' },
  RECHAZADA: { icon: IconX, color: 'text-red-600', label: 'Rechazada', bgColor: 'bg-red-50', borderColor: 'border-red-200', textColor: 'text-red-700' },
  FALLIDA: { icon: IconX, color: 'text-red-600', label: 'Fallida', bgColor: 'bg-red-50', borderColor: 'border-red-200', textColor: 'text-red-700' },
  REVERTIDA: { icon: IconRotate, color: 'text-orange-600', label: 'Revertida', bgColor: 'bg-orange-50', borderColor: 'border-orange-200', textColor: 'text-orange-700' },
  CANCELADA: { icon: IconX, color: 'text-slate-500', label: 'Cancelada', bgColor: 'bg-slate-100', borderColor: 'border-slate-200', textColor: 'text-slate-500' },
  ERROR: { icon: IconAlertOctagon, color: 'text-red-600', label: 'Error', bgColor: 'bg-red-50', borderColor: 'border-red-200', textColor: 'text-red-700' },
};

const getEstadoInfo = (estado: EstadoTransferencia): EstadoInfo => {
  return ESTADO_MAP[estado] || ESTADO_MAP.PENDIENTE;
};

interface TransferTimelineProps {
  estados: TransferenciaEstadoDetalle[];
  currentEstado: EstadoTransferencia;
  isLoading?: boolean;
  onComplete?: () => void;
  onReject?: () => void;
}

export const TransferTimeline: React.FC<TransferTimelineProps> = ({ estados, currentEstado, isLoading, onComplete, onReject }) => {
  const estadoMap = new Map(estados.map((e) => [e.paso, e]));
  const ordenPasos = STEPS.map((p) => p.id);
  const currentPaso = estados.find((e) => e.estado !== 'PENDIENTE')?.paso;
  const currentStepIdx = currentPaso ? ordenPasos.indexOf(currentPaso as PasoTransferencia) : -1;

  useEffect(() => {
    if (currentEstado === 'COMPLETADA' && onComplete) {
      onComplete();
    }
    if (currentEstado === 'FALLIDA' && onReject) {
      onReject();
    }
  }, [currentEstado, onComplete, onReject]);

  if (isLoading) {
    return (
      <div className="space-y-3">
        {STEPS.map((_, i) => (
          <div key={i} className="h-16 bg-slate-100 rounded-xl animate-pulse" />
        ))}
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {STEPS.map((paso, index) => {
        const detalle = estadoMap.get(paso.id);
        const isCompleted = detalle?.exito === true;
        const isCurrent = index === currentStepIdx;
        const isFailed = detalle?.exito === false;
        const Icon = paso.icon;
        const estadoInfo = getEstadoInfo(currentEstado);

        return (
          <motion.div
            key={paso.id}
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: index * 0.05, duration: 0.2 }}
            className={clsx(
              'flex items-center space-x-4 p-4 rounded-xl border transition-all',
              isCompleted ? 'bg-green-50 border-green-200' :
              isCurrent ? clsx(estadoInfo.bgColor, estadoInfo.borderColor) :
              isFailed ? 'bg-red-50 border-red-200' :
              'bg-slate-50 border-slate-200'
            )}
          >
            <div
              className={clsx(
                'w-10 h-10 rounded-full flex items-center justify-center transition-all',
                isCompleted
                  ? 'bg-green-500 text-white'
                  : isCurrent
                  ? clsx('bg-blue-600 text-white', estadoInfo.animate && 'animate-pulse')
                  : isFailed
                  ? 'bg-red-500 text-white'
                  : 'bg-slate-200 text-slate-400'
              )}
            >
              {isCompleted ? (
                <IconCircleCheck className="w-5 h-5" />
              ) : isCurrent ? (
                <IconLoader2 className={clsx('w-5 h-5', estadoInfo.animate && 'animate-spin')} />
              ) : isFailed ? (
                <IconX className="w-5 h-5" />
              ) : (
                <Icon className="w-5 h-5" />
              )}
            </div>
            <div className="flex-1">
              <div className="flex items-center justify-between">
                <span className={clsx(
                  'font-medium',
                  isCompleted ? 'text-green-700' :
                  isCurrent ? estadoInfo.textColor :
                  isFailed ? 'text-red-700' :
                  'text-slate-500'
                )}>
                  {paso.label}
                </span>
                {detalle?.duracionMs !== undefined && (
                  <span className="text-xs text-slate-400">
                    {detalle.duracionMs}ms
                  </span>
                )}
              </div>
              {detalle?.mensaje && (
                <p className="text-xs text-slate-500 mt-1">{detalle.mensaje}</p>
              )}
            </div>
          </motion.div>
        );
      })}

      <AnimatePresence>
        {currentEstado && (
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3 }}
            className={clsx(
              'p-4 rounded-xl border',
              currentEstado === 'COMPLETADA' ? 'bg-green-50 border-green-200 animate-pulse-green' :
              currentEstado === 'FALLIDA' ? 'bg-red-50 border-red-200' :
              getEstadoInfo(currentEstado).bgColor,
              currentEstado === 'FALLIDA' ? 'border-red-200' : getEstadoInfo(currentEstado).borderColor
            )}
          >
            <div className="flex items-center space-x-3">
              <div className={clsx(
                'p-1.5 rounded-lg',
                 currentEstado === 'COMPLETADA' ? 'bg-green-100' :
                 currentEstado === 'FALLIDA' ? 'bg-red-100' : 'bg-slate-100'
              )}>
                {React.createElement(getEstadoInfo(currentEstado).icon, {
                  className: clsx('w-5 h-5', getEstadoInfo(currentEstado).color),
                })}
              </div>
              <span className={clsx(
                'font-medium',
                getEstadoInfo(currentEstado).textColor
              )}>
                Estado actual: {getEstadoInfo(currentEstado).label}
              </span>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};
