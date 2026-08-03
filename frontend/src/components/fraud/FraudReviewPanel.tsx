import React from 'react';
import { EvaluacionFraude } from '@/types/fraud';

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
          <div key={i} className="bg-surface-800 rounded-lg p-4 animate-pulse h-24"></div>
        ))}
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {evaluaciones.map((evaluacion) => (
        <div
          key={evaluacion.id}
          className="bg-surface-800 rounded-lg p-4 border border-surface-700"
        >
          <div className="flex items-center justify-between mb-2">
            <div>
              <span className="font-medium text-surface-100">
                Trace: {evaluacion.traceId}
              </span>
              <span className={`ml-2 text-2xl font-bold ${
                evaluacion.score >= 70 ? 'text-banking-error' :
                evaluacion.score >= 30 ? 'text-banking-warning' :
                'text-banking-success'
              }`}>
                {evaluacion.score}
              </span>
            </div>
            <span className={`px-2 py-1 rounded-full text-xs font-medium ${
              evaluacion.decision === 'APROBADO' ? 'bg-banking-success/20 text-banking-success' :
              evaluacion.decision === 'RECHAZADO' ? 'bg-banking-error/20 text-banking-error' :
              'bg-banking-warning/20 text-banking-warning'
            }`}>
              {evaluacion.decision}
            </span>
          </div>

          {evaluacion.reglasEvaluadas
            ?.filter((r) => r.activada)
            .map((regla) => (
              <div key={regla.codigo} className="text-xs text-surface-400 mb-1">
                {regla.descripcion}: +{regla.pesoAsignado} — {regla.detalle}
              </div>
            ))}

          {evaluacion.decision === 'EN_REVISION' && (
            <div className="flex space-x-2 mt-3">
              <button
                onClick={() => onAprobar?.(evaluacion.id)}
                className="px-3 py-1 bg-banking-success text-white rounded text-sm hover:bg-banking-success/80"
              >
                Aprobar
              </button>
              <button
                onClick={() => onRechazar?.(evaluacion.id)}
                className="px-3 py-1 bg-banking-error text-white rounded text-sm hover:bg-banking-error/80"
              >
                Rechazar
              </button>
            </div>
          )}
        </div>
      ))}
    </div>
  );
};
