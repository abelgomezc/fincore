import React from 'react';
import { Transferencia } from '@/types/transfer';
import { EvaluacionFraude } from '@/types/fraud';
import { clsx } from '@/lib/utils';

interface TransferReviewListProps {
  transferencias: Transferencia[];
  onSelect?: (transferencia: Transferencia) => void;
  isLoading?: boolean;
}

export const TransferReviewList: React.FC<TransferReviewListProps> = ({
  transferencias,
  onSelect,
  isLoading,
}) => {
  if (isLoading) {
    return (
      <div className="space-y-3">
        {Array(5).fill(0).map((_, i) => (
          <div key={i} className="bg-surface-800 rounded-lg p-4 animate-pulse h-20"></div>
        ))}
      </div>
    );
  }

  if (transferencias.length === 0) {
    return (
      <div className="text-center py-8 text-surface-500">
        No hay transferencias en revisión
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {transferencias.map((t) => (
        <div
          key={t.id}
          className="bg-surface-800 rounded-lg p-4 border border-surface-700 hover:bg-surface-700/50 cursor-pointer transition-colors"
          onClick={() => onSelect?.(t)}
        >
          <div className="flex items-center justify-between">
            <div>
              <span className="font-medium text-surface-100">{t.numeroTransferencia}</span>
              <p className="text-sm text-surface-400 mt-1">
                {t.idUsuarioOrigen} → {t.idUsuarioDestino}
              </p>
            </div>
            <div className="text-right">
              <span className="font-semibold text-surface-100">
                ${t.monto.toFixed(2)} {t.moneda}
              </span>
              <span className={clsx(
                'ml-2 px-2 py-1 rounded-full text-xs',
                t.estado === 'COMPLETADA' ? 'bg-banking-success/20 text-banking-success' :
                t.estado === 'FALLIDA' || t.estado === 'REVERTIDA' ? 'bg-banking-error/20 text-banking-error' :
                'bg-banking-warning/20 text-banking-warning'
              )}>
                {t.estado}
              </span>
            </div>
          </div>
        </div>
      ))}
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
          <div key={i} className="bg-surface-800 rounded-lg p-4 animate-pulse h-16"></div>
        ))}
      </div>
    );
  }

  const alerts = evaluaciones.filter((e) => e.score >= 30);

  if (alerts.length === 0) {
    return (
      <div className="text-center py-8 text-surface-500">
        No hay alertas de fraude en este momento
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {alerts.map((alert) => (
        <div
          key={alert.id}
          className={clsx(
            'bg-surface-800 rounded-lg p-4 border transition-colors',
            alert.score >= 70 ? 'border-banking-error' :
            alert.score >= 45 ? 'border-banking-warning' :
            'border-banking-warning'
          )}
        >
          <div className="flex items-center justify-between">
            <div>
              <span className="font-medium text-surface-100">
                Score: {alert.score}/100 — {alert.traceId}
              </span>
              <p className="text-sm text-surface-400 mt-1">
                {alert.reglasEvaluadas?.filter((r) => r.activada).length} reglas activadas
              </p>
            </div>
            <span className={clsx(
              'px-2 py-1 rounded-full text-xs font-medium',
              alert.score >= 70 ? 'bg-banking-error/20 text-banking-error' :
              'bg-banking-warning/20 text-banking-warning'
            )}>
              {alert.decision}
            </span>
          </div>
        </div>
      ))}
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
          <div key={i} className="bg-surface-800 rounded-xl p-6 animate-pulse h-32"></div>
        ))}
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div className="bg-surface-800 rounded-xl p-6 border border-surface-700">
        <h3 className="text-sm font-medium text-surface-400 mb-2">Total Transferencias</h3>
        <p className="text-2xl font-bold text-surface-100">
          {(conciliacion as any)?.total ?? 'N/A'}
        </p>
      </div>
      <div className="bg-surface-800 rounded-xl p-6 border border-surface-700">
        <h3 className="text-sm font-medium text-surface-400 mb-2">Total Débitos</h3>
        <p className="text-2xl font-bold text-banking-error">
          ${(conciliacion as any)?.totalDebitos?.toFixed(2) ?? '0.00'}
        </p>
      </div>
      <div className="bg-surface-800 rounded-xl p-6 border border-surface-700">
        <h3 className="text-sm font-medium text-surface-400 mb-2">Total Créditos</h3>
        <p className="text-2xl font-bold text-banking-success">
          ${(conciliacion as any)?.totalCreditos?.toFixed(2) ?? '0.00'}
        </p>
      </div>
    </div>
  );
};
