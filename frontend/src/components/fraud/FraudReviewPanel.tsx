import React from 'react';
import { EvaluacionFraude } from '@/types/fraud';
import { Card, Badge } from '@/components/ui';
import {
  IconAlertTriangle,
  IconCheck,
  IconX,
} from '@tabler/icons-react';
import { clsx } from '@/lib/utils';

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
          <div key={i} className="bg-slate-100 rounded-lg p-4 animate-pulse h-24 border border-slate-200" />
        ))}
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {evaluaciones.map((evaluacion, index) => {
        const scoreColor = evaluacion.score >= 70
          ? 'text-red-600 bg-red-50 border-red-200'
          : evaluacion.score >= 30
          ? 'text-amber-600 bg-amber-50 border-amber-200'
          : 'text-green-600 bg-green-50 border-green-200';

        return (
          <Card key={evaluacion.id} className="p-4 border border-slate-200">
            <div className="flex items-center justify-between mb-2">
              <div className="flex items-center space-x-2">
                <IconAlertTriangle className="w-4 h-4 text-slate-400" />
                <span className="font-medium text-slate-800">
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
              evaluacion.decision === 'APROBADO' ? 'bg-green-100 text-green-700 border border-green-200' :
              evaluacion.decision === 'RECHAZADO' ? 'bg-red-100 text-red-700 border border-red-200' :
              'bg-amber-100 text-amber-700 border border-amber-200'
            )}>
              {evaluacion.decision}
            </span>

            <div className="mt-3 space-y-1">
              {evaluacion.reglasEvaluadas
                ?.filter((r) => r.activada)
                .map((regla) => (
                  <div key={regla.codigo} className="text-xs text-slate-500">
                    ✓ {regla.descripcion}: +{regla.pesoAsignado} pts
                  </div>
                ))}
            </div>

            {evaluacion.decision === 'EN_REVISION' && (
              <div className="flex space-x-2 mt-3">
                <button
                  onClick={() => onAprobar?.(evaluacion.id)}
                  className="px-3 py-1.5 bg-green-600 text-white rounded-lg text-sm hover:bg-green-700 transition-colors focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-1"
                >
                  Aprobar
                </button>
                <button
                  onClick={() => onRechazar?.(evaluacion.id)}
                  className="px-3 py-1.5 bg-red-600 text-white rounded-lg text-sm hover:bg-red-700 transition-colors focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-1"
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
