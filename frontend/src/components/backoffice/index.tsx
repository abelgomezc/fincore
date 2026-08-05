import React from 'react';
import { Transferencia } from '@/types/transfer';
import { EvaluacionFraude } from '@/types/fraud';
import { clsx } from '@/lib/utils';
import { Card, Badge } from '@/components/ui';
import { motion } from 'framer-motion';
import {
  IconClock,
  IconShieldCheck,
  IconAlertTriangle,
  IconCheck,
  IconX,
  IconRefresh,
  IconFileChart,
  IconTrendingUp,
  IconTrendingDown,
} from '@tabler/icons-react';

interface TransferReviewListProps {
  transferencias: Transferencia[];
  onSelect?: (transferencia: Transferencia) => void;
  isLoading?: boolean;
}

const estadoBadgeConfig: Record<string, { variant: 'warning' | 'danger' | 'success' | 'info' | 'neutral' | 'primary'; icon: any }> = {
  PENDIENTE: { variant: 'warning', icon: <IconClock className="w-4 h-4" /> },
  VALIDANDO: { variant: 'primary', icon: <IconShieldCheck className="w-4 h-4" /> },
  COMPLETADA: { variant: 'success', icon: <IconCheck className="w-4 h-4" /> },
  FALLIDA: { variant: 'danger', icon: <IconX className="w-4 h-4" /> },
  REVERTIDA: { variant: 'warning', icon: <IconRefresh className="w-4 h-4" /> },
  EN_REVISION: { variant: 'warning', icon: <IconAlertTriangle className="w-4 h-4" /> },
};

export const TransferReviewList: React.FC<TransferReviewListProps> = ({
  transferencias,
  onSelect,
  isLoading,
}) => {
  if (isLoading) {
    return (
      <div className="space-y-3">
        {Array(5).fill(0).map((_, i) => (
          <div key={i} className="bg-slate-100 rounded-lg p-4 animate-pulse h-20 border border-slate-200" />
        ))}
      </div>
    );
  }

  if (transferencias.length === 0) {
    return (
      <Card className="text-center py-8">
        <IconClock className="w-12 h-12 mx-auto mb-3 text-slate-300" />
        <p className="text-slate-500">No hay transferencias en revisión</p>
      </Card>
    );
  }

  return (
    <div className="space-y-3">
      {transferencias.map((t, index) => {
        const config = estadoBadgeConfig[t.estado] || estadoBadgeConfig.PENDIENTE;
        return (
          <motion.div
            key={t.id}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.05 }}
            className="bg-white rounded-lg p-4 border border-slate-200 hover:shadow-card-hover cursor-pointer transition-all"
            onClick={() => onSelect?.(t)}
          >
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-3">
                <IconRefresh className="w-5 h-5 text-blue-600" />
                <div>
                  <span className="font-medium text-slate-800">{t.numeroTransferencia}</span>
                  <p className="text-sm text-slate-500 mt-1">
                    {t.idUsuarioOrigen} → {t.idUsuarioDestino}
                  </p>
                </div>
              </div>
              <div className="text-right">
                <span className="font-semibold text-slate-800">
                  ${t.monto.toFixed(2)} {t.moneda}
                </span>
                <div className="mt-1">
                  <Badge variant={config.variant} size="sm">
                    {config.icon}
                    <span className="ml-1">{t.estado}</span>
                  </Badge>
                </div>
              </div>
            </div>
          </motion.div>
        );
      })}
    </div>
  );
};

interface FraudAlertListProps {
  evaluaciones: EvaluacionFraude[];
  isLoading?: boolean;
}

export const FraudAlertList: React.FC<FraudAlertListProps> = ({ evaluaciones, isLoading }) => {
  if (isLoading) {
    return (
      <div className="space-y-3">
        {Array(5).fill(0).map((_, i) => (
          <div key={i} className="bg-slate-100 rounded-lg p-4 animate-pulse h-16 border border-slate-200" />
        ))}
      </div>
    );
  }

  const alerts = evaluaciones.filter((e) => e.score >= 30);

  if (alerts.length === 0) {
    return (
      <Card className="text-center py-8">
        <IconShieldCheck className="w-12 h-12 mx-auto mb-3 text-slate-300" />
        <p className="text-slate-500">No hay alertas de fraude en este momento</p>
      </Card>
    );
  }

  return (
    <div className="space-y-3">
      {alerts.map((alert, index) => {
        const scoreColor = alert.score >= 70 ? 'text-red-600' :
          alert.score >= 45 ? 'text-amber-600' : 'text-green-600';
        const scoreBg = alert.score >= 70 ? 'bg-red-50 border-red-200' :
          alert.score >= 45 ? 'bg-amber-50 border-amber-200' : 'bg-green-50 border-green-200';

        return (
          <motion.div
            key={alert.id}
            initial={{ opacity: 0, x: -10 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: index * 0.05 }}
            className={clsx(
              'bg-white rounded-lg p-4 border transition-colors',
              scoreBg
            )}
          >
            <div className="flex items-center justify-between">
              <div>
                <span className="font-medium text-slate-800 flex items-center space-x-2">
                  <IconAlertTriangle className="w-4 h-4" />
                  <span>Score: {alert.score}/100</span>
                </span>
                <p className="text-xs text-slate-500 mt-1 font-mono">
                  {alert.traceId}
                </p>
                <div className="mt-2 text-xs text-slate-500">
                  {alert.reglasEvaluadas?.filter((r) => r.activada).length} reglas activadas
                </div>
              </div>
              <div className="text-right">
                <span className={clsx('px-2 py-1 rounded-full text-xs font-medium', scoreColor)}>
                  {alert.decision}
                </span>
              </div>
            </div>
          </motion.div>
        );
      })}
    </div>
  );
};

interface ReportsPanelProps {
  conciliacion?: Record<string, unknown>;
  isLoading?: boolean;
}

export const ReportsPanel: React.FC<ReportsPanelProps> = ({ conciliacion, isLoading }) => {
  if (isLoading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {Array(3).fill(0).map((_, i) => (
          <div key={i} className="bg-slate-100 rounded-xl p-6 animate-pulse h-32 border border-slate-200" />
        ))}
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      <Card className="text-center p-4">
        <IconFileChart className="w-8 h-8 text-blue-600 mx-auto mb-2" />
        <h3 className="text-sm font-medium text-slate-500 mb-2">Total Transferencias</h3>
        <p className="text-2xl font-bold text-slate-800">
          {(conciliacion as any)?.total ?? '0'}
        </p>
      </Card>
      <Card className="text-center p-4">
        <IconTrendingDown className="w-8 h-8 text-red-600 mx-auto mb-2" />
        <h3 className="text-sm font-medium text-slate-500 mb-2">Total Débitos</h3>
        <p className="text-2xl font-bold text-red-700">
          ${(conciliacion as any)?.totalDebitos?.toFixed(2) ?? '0.00'}
        </p>
      </Card>
      <Card className="text-center p-4">
        <IconTrendingUp className="w-8 h-8 text-green-600 mx-auto mb-2" />
        <h3 className="text-sm font-medium text-slate-500 mb-2">Total Créditos</h3>
        <p className="text-2xl font-bold text-green-700">
          ${(conciliacion as any)?.totalCreditos?.toFixed(2) ?? '0.00'}
        </p>
      </Card>
    </div>
  );
};
