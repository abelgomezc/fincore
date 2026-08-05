import React from 'react';
import { EvaluacionFraude } from '@/types/fraud';
import { clsx } from '@/lib/utils';
import { Card } from '@/components/ui';
import {
  AlertTriangle,
  CheckCircle,
  XCircle,
  Clock,
} from 'lucide-react';

interface FraudReviewPanelProps {
  evaluaciones: EvaluacionFraude[];
  onAprobar?: (id: number) => void;
  onRechazar?: (id: number) => void;
  isLoading?: boolean;
}

export const FraudReviewPanel: React.FC<FraudReviewPanelProps> = ({
  evaluaciones,
  onAprobar,
  onRechazar,
  isLoading,
}) => {
  if (isLoading) {
    return (
      <div className="space-y-3">
        {Array(5).fill(0).map((_, i) => (
          <div key={i} className="bg-surface-100 rounded-lg p-4 animate-pulse h-24 border border-surface-200"></div>
        ))}
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {evaluaciones.map((evaluacion, index) => {
        const scoreColor = evaluacion.score >= 70
          ? 'text-danger-600 bg-danger-50 border-danger-200'
          : evaluacion.score >= 30
          ? 'text-warning-600 bg-warning-50 border-warning-200'
          : 'text-success-600 bg-success-50 border-success-200';

        return (
          <Card key={evaluacion.id} className="p-4 border border-surface-200">
            <div className="flex items-center justify-between mb-2">
              <div className="flex items-center space-x-2">
                <AlertTriangle className="w-4 h-4 text-surface-400" />
                <span className="font-medium text-dark-500">
                  Trace: {evaluacion.traceId}
                </span>
              </div>
              <span className={clsx(
                'px-2 py-1 rounded-full text-xs font-medium',
                scoreColor
              )}>
                Score: {evaluacion.score}
              </span>
            </div>

            <span className={clsx(
              'px-2 py-1 rounded-full text-xs font-medium',
              evaluacion.decision === 'APROBADO' ? 'bg-success-100 text-success-700 border border-success-200' :
              evaluacion.decision === 'RECHAZADO' ? 'bg-danger-100 text-danger-700 border border-danger-200' :
              'bg-warning-100 text-warning-700 border border-warning-200'
            )}>
              {evaluacion.decision}
            </span>

            <div className="mt-3 space-y-1">
              {evaluacion.reglasEvaluadas
                ?.filter((r) => r.activada)
                .map((regla) => (
                  <div key={regla.codigo} className="text-xs text-surface-500">
                    ✓ {regla.descripcion}: +{regla.pesoAsignado} pts
                  </div>
                ))}
            </div>

            {evaluacion.decision === 'EN_REVISION' && (
              <div className="flex space-x-2 mt-3">
                <button
                  onClick={() => onAprobar?.(evaluacion.id)}
                  className="px-3 py-1.5 bg-success-500 text-white rounded-lg text-sm hover:bg-success-600 transition-colors focus:outline-none focus:ring-2 focus:ring-success-500 focus:ring-offset-1"
                >
                  Aprobar
                </button>
                <button
                  onClick={() => onRechazar?.(evaluacion.id)}
                  className="px-3 py-1.5 bg-danger-500 text-white rounded-lg text-sm hover:bg-danger-600 transition-colors focus:outline-none focus:ring-2 focus:ring-danger-500 focus:ring-offset-1"
                >
                  Rechazar
                </button>
              </div>
            )}
          </Card>
        );
      })}
    </div>
  );
};
