import React from 'react';
import { EvaluacionFraude, ReglaFraudeDetalle, DecisionFraude } from '@/types/fraud';
import { formatCurrency, formatDate } from '@/lib/utils';
import { clsx } from '@/lib/utils';

interface FraudScoreCardProps {
  evaluacion?: EvaluacionFraude;
  isLoading?: boolean;
}

export const FraudScoreCard: React.FC<FraudScoreCardProps> = ({ evaluacion, isLoading }) => {
  if (isLoading || !evaluacion) {
    return (
      <div className="bg-surface-800 rounded-xl p-6 animate-pulse">
        <div className="h-4 bg-surface-700 rounded w-3/4 mb-4"></div>
        <div className="h-8 bg-surface-700 rounded w-1/2 mb-2"></div>
        <div className="h-4 bg-surface-700 rounded w-full"></div>
      </div>
    );
  }

  const scoreColor = evaluacion.score >= 70 ? 'text-banking-error' :
                     evaluacion.score >= 30 ? 'text-banking-warning' :
                     'text-banking-success';

  const decisionColor = evaluacion.decision === 'APROBADO' ? 'bg-banking-success/20 text-banking-success' :
                         evaluacion.decision === 'RECHAZADO' ? 'bg-banking-error/20 text-banking-error' :
                         'bg-banking-warning/20 text-banking-warning';

  const reglasActivadas = evaluacion.reglasEvaluadas?.filter((r) => r.activada) ?? [];

  return (
    <div className="bg-surface-800 rounded-xl p-6 border border-surface-700">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold text-surface-100">Score de Fraude</h3>
        <span className={clsx('px-2 py-1 rounded-full text-xs font-medium', decisionColor)}>
          {evaluacion.decision}
        </span>
      </div>

      <div className="mb-4">
        <div className="flex items-baseline space-x-2">
          <span className={clsx('text-4xl font-bold', scoreColor)}>
            {evaluacion.score}
          </span>
          <span className="text-sm text-surface-500">/ 100</span>
        </div>
        <div className="mt-2 h-2 bg-surface-700 rounded-full overflow-hidden">
          <div
            className={clsx(
              'h-full rounded-full transition-all',
              evaluacion.score >= 70 ? 'bg-banking-error' :
              evaluacion.score >= 30 ? 'bg-banking-warning' :
              'bg-banking-success'
            )}
            style={{ width: `${Math.min(evaluacion.score, 100)}%` }}
          />
        </div>
      </div>

      <div className="mb-4">
        <span className="text-sm text-surface-400">
          {reglasActivadas.length} de {evaluacion.reglasEvaluadas?.length ?? 0} reglas activadas
        </span>
      </div>

      <div className="space-y-2 max-h-48 overflow-y-auto">
        {evaluacion.reglasEvaluadas?.map((regla) => (
          <div
            key={regla.codigo}
            className={clsx(
              'p-3 rounded-lg border text-sm',
              regla.activada
                ? 'bg-banking-error/10 border-banking-error/30'
                : 'bg-surface-900 border-surface-700'
            )}
          >
            <div className="flex items-center justify-between">
              <span className="font-medium text-surface-200">{regla.descripcion}</span>
              {regla.activada && (
                <span className="text-banking-error text-xs font-medium">
                  +{regla.pesoAsignado} pts
                </span>
              )}
            </div>
            {regla.activada && regla.detalle && (
              <p className="text-xs text-surface-400 mt-1">{regla.detalle}</p>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};
