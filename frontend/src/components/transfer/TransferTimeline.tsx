import React from 'react';
import { PasoTransferencia, EstadoTransferencia, TransferenciaEstadoDetalle } from '@/types/transfer';
import { clsx } from '@/lib/utils';
import { motion } from 'framer-motion';
import {
  Clock,
  Search,
  CheckCircle,
  AlertCircle,
  Lock,
  Loader2,
  ArrowRightLeft,
  CheckCircle2,
  XCircle,
  RotateCcw,
  AlertOctagon,
} from 'lucide-react';

interface StepConfig {
  id: PasoTransferencia;
  label: string;
  icon: React.ElementType;
}

const STEPS: StepConfig[] = [
  { id: 'VALIDAR_DATOS', label: 'Validando datos', icon: Search },
  { id: 'VERIFICAR_KYC', label: 'Verificando KYC', icon: Search },
  { id: 'VALIDAR_LIMITES', label: 'Validando límites', icon: Search },
  { id: 'EVALUAR_FRAUDE', label: 'Evaluación de fraude', icon: AlertCircle },
  { id: 'RESERVAR_FONDOS', label: 'Reservando fondos', icon: Lock },
  { id: 'CREAR_ASIENTO_DEBITO', label: 'Creando asiento débito', icon: Lock },
  { id: 'APLICAR_DEBITO', label: 'Aplicando débito', icon: ArrowRightLeft },
  { id: 'APLICAR_CREDITO', label: 'Aplicando crédito', icon: ArrowRightLeft },
  { id: 'LIBERAR_RESERVA', label: 'Liberando reserva', icon: Lock },
  { id: 'REGISTRAR_AUDITORIA', label: 'Registrando auditoría', icon: Search },
  { id: 'COBRAR_COMISION', label: 'Cobrando comisión', icon: Search },
  { id: 'NOTIFICAR', label: 'Notificando', icon: Search },
];

type EstadoInfo = {
  icon: React.ElementType;
  color: string;
  label: string;
  animate?: boolean;
  bgColor?: string;
  borderColor?: string;
  textColor?: string;
};

const ESTADO_MAP: Record<string, EstadoInfo> = {
  PENDIENTE: { icon: Clock, color: 'text-surface-400', label: 'Pendiente', bgColor: 'bg-surface-100', borderColor: 'border-surface-200', textColor: 'text-surface-500' },
  VALIDANDO: { icon: Search, color: 'text-primary-500', label: 'Validando', bgColor: 'bg-primary-50', borderColor: 'border-primary-200', textColor: 'text-primary-600', animate: true },
  VALIDADA: { icon: CheckCircle, color: 'text-primary-500', label: 'Autorizada', bgColor: 'bg-primary-50', borderColor: 'border-primary-200', textColor: 'text-primary-600' },
  AUTORIZADA: { icon: CheckCircle, color: 'text-primary-500', label: 'Autorizada', bgColor: 'bg-primary-50', borderColor: 'border-primary-200', textColor: 'text-primary-600' },
  EN_REVISION: { icon: AlertCircle, color: 'text-warning-500', label: 'En revisión', bgColor: 'bg-warning-50', borderColor: 'border-warning-200', textColor: 'text-warning-600', animate: true },
  RESERVANDO: { icon: Lock, color: 'text-warning-500', label: 'Reservando fondos', bgColor: 'bg-warning-50', borderColor: 'border-warning-200', textColor: 'text-warning-600' },
  RESERVANDO_FONDOS: { icon: Lock, color: 'text-warning-500', label: 'Reservando fondos', bgColor: 'bg-warning-50', borderColor: 'border-warning-200', textColor: 'text-warning-600' },
  FONDOS_RESERVADOS: { icon: Lock, color: 'text-primary-500', label: 'Fondos reservados', bgColor: 'bg-primary-50', borderColor: 'border-primary-200', textColor: 'text-primary-600' },
  PROCESANDO: { icon: Loader2, color: 'text-primary-500', label: 'Procesando', bgColor: 'bg-primary-50', borderColor: 'border-primary-200', textColor: 'text-primary-600', animate: true },
  EJECUTANDO_DEBITO: { icon: Loader2, color: 'text-primary-500', label: 'Ejecutando débito', bgColor: 'bg-primary-50', borderColor: 'border-primary-200', textColor: 'text-primary-600', animate: true },
  DEBITO_APLICADO: { icon: ArrowRightLeft, color: 'text-primary-500', label: 'Débito aplicado', bgColor: 'bg-primary-50', borderColor: 'border-primary-200', textColor: 'text-primary-600' },
  EJECUTANDO_CREDITO: { icon: ArrowRightLeft, color: 'text-success-500', label: 'Acreditando', bgColor: 'bg-success-50', borderColor: 'border-success-200', textColor: 'text-success-600' },
  ACREDITANDO: { icon: ArrowRightLeft, color: 'text-success-500', label: 'Acreditando', bgColor: 'bg-success-50', borderColor: 'border-success-200', textColor: 'text-success-600' },
  CREDITO_APLICADO: { icon: ArrowRightLeft, color: 'text-success-500', label: 'Crédito aplicado', bgColor: 'bg-success-50', borderColor: 'border-success-200', textColor: 'text-success-600' },
  COMPLETADA: { icon: CheckCircle2, color: 'text-success-500', label: 'Completada', bgColor: 'bg-success-50', borderColor: 'border-success-200', textColor: 'text-success-600' },
  RECHAZADA: { icon: XCircle, color: 'text-danger-500', label: 'Rechazada', bgColor: 'bg-danger-50', borderColor: 'border-danger-200', textColor: 'text-danger-600' },
  FALLIDA: { icon: XCircle, color: 'text-danger-500', label: 'Fallida', bgColor: 'bg-danger-50', borderColor: 'border-danger-200', textColor: 'text-danger-600' },
  REVERTIDA: { icon: RotateCcw, color: 'text-warning-500', label: 'Revertida', bgColor: 'bg-warning-50', borderColor: 'border-warning-200', textColor: 'text-warning-600' },
  CANCELADA: { icon: XCircle, color: 'text-surface-500', label: 'Cancelada', bgColor: 'bg-surface-100', borderColor: 'border-surface-200', textColor: 'text-surface-500' },
  ERROR: { icon: AlertOctagon, color: 'text-danger-500', label: 'Error', bgColor: 'bg-danger-50', borderColor: 'border-danger-200', textColor: 'text-danger-600' },
};

const getEstadoInfo = (estado: EstadoTransferencia): EstadoInfo => {
  return ESTADO_MAP[estado] || ESTADO_MAP.PENDIENTE;
};

interface TransferTimelineProps {
  estados: TransferenciaEstadoDetalle[];
  currentEstado: EstadoTransferencia;
  isLoading?: boolean;
}

export const TransferTimeline: React.FC<TransferTimelineProps> = ({ estados, currentEstado, isLoading }) => {
  const estadoMap = new Map(estados.map((e) => [e.paso, e]));
  const ordenPasos = STEPS.map((p) => p.id);
  const currentPaso = estados.find((e) => e.estado !== 'PENDIENTE')?.paso;
  const currentStepIdx = currentPaso ? ordenPasos.indexOf(currentPaso as PasoTransferencia) : -1;

  if (isLoading) {
    return (
      <div className="space-y-3 animate-pulse">
        {STEPS.map((_, i) => (
          <div key={i} className="h-14 bg-surface-200 rounded-lg"></div>
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
        const isAnimating = isCurrent;
        const estadoInfo = getEstadoInfo(currentEstado as EstadoTransferencia);

        return (
          <motion.div
            key={paso.id}
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: index * 0.05, duration: 0.2 }}
            className={clsx(
              'flex items-center space-x-4 p-3 rounded-lg border transition-all',
              isCompleted ? 'bg-success-50 border-success-200' :
              isCurrent ? clsx(estadoInfo.bgColor, estadoInfo.borderColor) :
              isFailed ? 'bg-danger-50 border-danger-200' :
              'bg-surface-50 border-surface-200'
            )}
          >
            <div
              className={clsx(
                'w-10 h-10 rounded-full flex items-center justify-center transition-all',
                isCompleted
                  ? 'bg-success-500 text-white'
                  : isCurrent
                  ? clsx('bg-primary-500 text-white', isAnimating && estadoInfo.animate && 'animate-pulse')
                  : isFailed
                  ? 'bg-danger-500 text-white'
                  : 'bg-surface-200 text-surface-400'
              )}
            >
              {isCompleted ? (
                <CheckCircle className="w-5 h-5" />
              ) : isCurrent ? (
                <Loader2 className={clsx('w-5 h-5', isAnimating && estadoInfo.animate && 'animate-spin')} />
              ) : isFailed ? (
                <XCircle className="w-5 h-5" />
              ) : (
                <Icon className="w-5 h-5" />
              )}
            </div>
            <div className="flex-1">
              <div className="flex items-center justify-between">
                <span className={clsx(
                  'font-medium',
                  isCompleted ? 'text-success-700' :
                  isCurrent ? estadoInfo.textColor :
                  isFailed ? 'text-danger-700' :
                  'text-surface-500'
                )}>
                  {paso.label}
                </span>
                {detalle?.duracionMs !== undefined && (
                  <span className="text-xs text-surface-400">
                    {detalle.duracionMs}ms
                  </span>
                )}
              </div>
              {detalle?.mensaje && (
                <p className="text-xs text-surface-400 mt-1">{detalle.mensaje}</p>
              )}
            </div>
          </motion.div>
        );
      })}

      {currentEstado && (
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: STEPS.length * 0.05 }}
          className={clsx(
            'p-3 rounded-lg border',
            getEstadoInfo(currentEstado as EstadoTransferencia).bgColor,
            getEstadoInfo(currentEstado as EstadoTransferencia).borderColor
          )}
        >
          <div className="flex items-center space-x-3">
            <div className="p-1.5 bg-surface-100 rounded-lg">
              {React.createElement(getEstadoInfo(currentEstado as EstadoTransferencia).icon, {
                className: clsx(
                  'w-5 h-5',
                  getEstadoInfo(currentEstado as EstadoTransferencia).color,
                  getEstadoInfo(currentEstado as EstadoTransferencia).animate && 'animate-pulse'
                ),
              })}
            </div>
            <span className={clsx(
              'font-medium',
              getEstadoInfo(currentEstado as EstadoTransferencia).textColor
            )}>
              Estado actual: {getEstadoInfo(currentEstado as EstadoTransferencia).label}
            </span>
          </div>
        </motion.div>
      )}
    </div>
  );
};
