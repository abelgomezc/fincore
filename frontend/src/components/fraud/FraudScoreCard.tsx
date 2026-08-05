import React from 'react';
import { EvaluacionFraude, ReglaFraudeDetalle } from '@/types/fraud';
import { Card, Badge } from '@/components/ui';
import { ProgressBar } from '@tremor/react';
import {
  IconShield,
  IconTrendingUp,
  IconTrendingDown,
  IconCheck,
  IconX,
  IconAlertTriangle,
  IconInfoCircle,
} from '@tabler/icons-react';
import { motion } from 'framer-motion';

interface FraudScoreCardProps {
  evaluacion?: EvaluacionFraude;
  isLoading?: boolean;
}

const getScoreColor = (score: number) => {
  if (score >= 70) return { bg: 'bg-red-500', text: 'text-red-600', border: 'border-red-200', light: 'bg-red-50' };
  if (score >= 30) return { bg: 'bg-amber-500', text: 'text-amber-600', border: 'border-amber-200', light: 'bg-amber-50' };
  return { bg: 'bg-green-500', text: 'text-green-600', border: 'border-green-200', light: 'bg-green-50' };
};

const getDecisionIcon = (decision: string) => {
  switch (decision) {
    case 'APROBADO': return <IconCheck className="w-5 h-5 text-green-600" />;
    case 'RECHAZADO': return <IconX className="w-5 h-5 text-red-600" />;
    case 'EN_REVISION': return <IconAlertTriangle className="w-5 h-5 text-amber-600" />;
    default: return <IconInfoCircle className="w-5 h-5 text-slate-400" />;
  }
};

export const FraudScoreCard: React.FC<FraudScoreCardProps> = ({ evaluacion, isLoading }) => {
  if (isLoading || !evaluacion) {
    return (
      <div className="bg-slate-100 rounded-2xl p-6 animate-pulse h-64 border border-slate-200" />
    );
  }

  const colors = getScoreColor(evaluacion.score);
  const percentage = Math.min(evaluacion.score, 100);

  const reglasActivadas = evaluacion.reglasEvaluadas?.filter((r) => r.activada) ?? [];
  const reglasInactivas = evaluacion.reglasEvaluadas?.filter((r) => !r.activada) ?? [];

  return (
    <Card
      title="Análisis de Fraude"
      icon={<IconShield className="w-5 h-5 text-blue-600" />}
      footer={
        <div className="flex items-center justify-between text-sm">
          <span className="text-slate-500">Reglas activadas</span>
          <span className="font-medium text-slate-700">
            {reglasActivadas.length} de {evaluacion.reglasEvaluadas?.length ?? 0}
          </span>
        </div>
      }
    >
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-lg font-semibold text-slate-800">Score de Riesgo</h3>
            <p className="text-sm text-slate-500 mt-1">
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
            <span className="text-slate-500">/ 100</span>
          </div>
        </div>

        <div>
          <ProgressBar
            value={percentage}
            color={evaluacion.score >= 70 ? 'red' : evaluacion.score >= 30 ? 'amber' : 'green'}
            className="mt-2"
          />
          <div className="flex justify-between mt-2 text-xs text-slate-500">
            <span>Bajo riesgo (&lt;30)</span>
            <span>Medio riesgo (30-70)</span>
            <span>Alto riesgo (&gt;70)</span>
          </div>
        </div>

        <Badge variant={evaluacion.decision === 'APROBADO' ? 'success' :
          evaluacion.decision === 'RECHAZADO' ? 'danger' : 'warning'} size="md">
          {evaluacion.decision}
        </Badge>

        <div className="mt-4 space-y-2 max-h-48 overflow-y-auto">
          <h4 className="text-sm font-medium text-slate-700 mb-2">Reglas Activadas</h4>
          {reglasActivadas.map((regla) => (
            <motion.div
              key={regla.codigo}
              initial={{ opacity: 0, x: -10 }}
              animate={{ opacity: 1, x: 0 }}
              className={colors.light + ' rounded-xl p-3 border ' + colors.border}
            >
              <div className="flex items-center justify-between">
                <div>
                  <span className="font-medium text-slate-700 text-sm">
                    {regla.descripcion}
                  </span>
                  {regla.detalle && (
                    <p className="text-xs text-slate-500 mt-1">{regla.detalle}</p>
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
              className="bg-slate-50 rounded-xl p-3 border border-slate-200"
            >
              <div className="flex items-center justify-between">
                <span className="font-medium text-slate-400 text-sm">
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
