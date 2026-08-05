import React from 'react';
import { EvaluacionFraude, ReglaFraudeDetalle } from '@/types/fraud';
import { Card, Badge } from '@/components/ui';
import {
  Shield,
  TrendingUp,
  TrendingDown,
  CheckCircle,
  XCircle,
  AlertTriangle,
  Info,
} from 'lucide-react';
import { motion } from 'framer-motion';

interface FraudScoreCardProps {
  evaluacion?: EvaluacionFraude;
  isLoading?: boolean;
}

const getScoreColor = (score: number) => {
  if (score >= 70) return { bg: 'bg-danger-500', text: 'text-danger-600', border: 'border-danger-200', light: 'bg-danger-50' };
  if (score >= 30) return { bg: 'bg-warning-500', text: 'text-warning-600', border: 'border-warning-200', light: 'bg-warning-50' };
  return { bg: 'bg-success-500', text: 'text-success-600', border: 'border-success-200', light: 'bg-success-50' };
};

const getDecisionIcon = (decision: string) => {
  switch (decision) {
    case 'APROBADO': return <CheckCircle className="w-5 h-5 text-success-500" />;
    case 'RECHAZADO': return <XCircle className="w-5 h-5 text-danger-500" />;
    case 'EN_REVISION': return <AlertTriangle className="w-5 h-5 text-warning-500" />;
    default: return <Info className="w-5 h-5 text-surface-400" />;
  }
};

export const FraudScoreCard: React.FC<FraudScoreCardProps> = ({ evaluacion, isLoading }) => {
  if (isLoading || !evaluacion) {
    return (
      <Card className="animate-pulse">
        <div className="h-4 bg-surface-200 rounded w-3/4 mb-4"></div>
        <div className="h-8 bg-surface-200 rounded w-1/2 mb-2"></div>
        <div className="h-4 bg-surface-200 rounded w-full"></div>
      </Card>
    );
  }

  const colors = getScoreColor(evaluacion.score);
  const percentage = Math.min(evaluacion.score, 100);
  const arcOffset = 180 - (percentage / 100) * 180;

  const reglasActivadas = evaluacion.reglasEvaluadas?.filter((r) => r.activada) ?? [];
  const reglasInactivas = evaluacion.reglasEvaluadas?.filter((r) => !r.activada) ?? [];

  const gaugeColor = colors.bg.replace('bg-', 'stroke-').replace('-500', '');

  return (
    <Card
      title="Análisis de Fraude"
      icon={<Shield className="w-5 h-5 text-primary-500" />}
      footer={
        <div className="flex items-center justify-between text-sm">
          <span className="text-surface-500">Reglas activadas</span>
          <span className="font-medium text-dark-500">
            {reglasActivadas.length} de {evaluacion.reglasEvaluadas?.length ?? 0}
          </span>
        </div>
      }
    >
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-lg font-semibold text-dark-500">Score de Riesgo</h3>
            <p className="text-sm text-surface-500 mt-1">
              {evaluacion.score < 30 ? 'Transacción segura' :
               evaluacion.score < 70 ? 'Requiere revisión' :
               'Alto riesgo detectado'}
            </p>
          </div>
          <div className="flex items-center space-x-2">
            {getDecisionIcon(evaluacion.decision)}
            <span className={colors.text + ' font-bold text-xl'}>
              {evaluacion.score}
            </span>
            <span className="text-surface-500">/ 100</span>
          </div>
        </div>

        <div className="relative">
          <svg width="100%" height="120" viewBox="0 0 200 100" style={{ overflow: 'visible' }}>
            <path
              d="M 20 80 A 80 80 0 0 1 180 80"
              fill="none"
              stroke="currentColor"
              strokeWidth="12"
              strokeDasharray="200"
              className="text-surface-200"
              style={{ strokeDashoffset: 0 }}
            />
            <path
              d="M 20 80 A 80 80 0 0 1 180 80"
              fill="none"
              strokeWidth="12"
              strokeDasharray="200"
              strokeDashoffset={200 - (percentage / 100) * 200}
              className={colors.bg.replace('bg-', 'text-')}
              strokeLinecap="round"
              style={{ transition: 'stroke-dashoffset 0.8s ease-out' }}
            />
          </svg>
          <div className="absolute -bottom-8 left-1/2 -translate-x-1/2 flex items-center space-x-2">
            <TrendingDown className="w-4 h-4 text-success-500" />
            <span className="text-xs text-surface-500">Bajo riesgo (&lt;30)</span>
            <span className="text-xs text-surface-300">—</span>
            <TrendingUp className="w-4 h-4 text-warning-500" />
            <span className="text-xs text-surface-500">Medio riesgo (30-70)</span>
            <span className="text-xs text-surface-300">—</span>
            <AlertTriangle className="w-4 h-4 text-danger-500" />
            <span className="text-xs text-surface-500">Alto riesgo (&gt;70)</span>
          </div>
        </div>

        <Badge variant={evaluacion.decision === 'APROBADO' ? 'success' :
          evaluacion.decision === 'RECHAZADO' ? 'danger' : 'warning'} size="md">
          {evaluacion.decision}
        </Badge>

        <div className="mt-4 space-y-2 max-h-48 overflow-y-auto">
          <h4 className="text-sm font-medium text-dark-500 mb-2">Reglas Activadas</h4>
          {reglasActivadas.map((regla) => (
            <motion.div
              key={regla.codigo}
              initial={{ opacity: 0, x: -10 }}
              animate={{ opacity: 1, x: 0 }}
              className={colors.light + ' rounded-lg p-3 border ' + colors.border}
            >
              <div className="flex items-center justify-between">
                <div>
                  <span className="font-medium text-dark-500 text-sm">
                    {regla.descripcion}
                  </span>
                  {regla.detalle && (
                    <p className="text-xs text-surface-500 mt-1">{regla.detalle}</p>
                  )}
                </div>
                <Badge variant="danger" size="sm">
                  +{regla.pesoAsignado} pts
                </Badge>
              </div>
            </motion.div>
          ))}

          {reglasInactivas.map((regla) => (
            <motion.div
              key={regla.codigo}
              initial={{ opacity: 0, x: -10 }}
              animate={{ opacity: 1, x: 0 }}
              className="bg-surface-50 rounded-lg p-3 border border-surface-200"
            >
              <div className="flex items-center justify-between">
                <span className="font-medium text-surface-400 text-sm">
                  {regla.descripcion}
                </span>
                <Badge variant="neutral" size="sm">
                  {regla.pesoAsignado} pts
                </Badge>
              </div>
            </motion.div>
          ))}
        </div>
      </div>
    </Card>
  );
};
