import { motion } from 'framer-motion'
import type { FraudScore } from '../types'

interface FraudScoreProps {
  score: FraudScore
}

/**
 * Componente de visualización del score de fraude
 * Muestra el nivel de riesgo y los factores que lo determinan
 */
function FraudScore({ score }: FraudScoreProps) {
  const getScoreColor = (value: number) => {
    if (value < 30) return { stroke: '#10b981', text: 'text-emerald-600', ring: 'ring-emerald-200' }
    if (value < 60) return { stroke: '#f59e0b', text: 'text-amber-600', ring: 'ring-amber-200' }
    if (value < 85) return { stroke: '#f97316', text: 'text-orange-600', ring: 'ring-orange-200' }
    return { stroke: '#ef4444', text: 'text-red-600', ring: 'ring-red-200' }
  }

  const colors = getScoreColor(score.score)

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="card-banking p-6"
    >
      <h3 className="text-lg font-semibold text-slate-900 mb-4">Score de Fraude</h3>

      <div className="flex items-center justify-center mb-6">
        <div className="relative w-32 h-32">
          <svg className="w-32 h-32 transform -rotate-90" viewBox="0 0 120 120">
            <circle
              cx="60"
              cy="60"
              r="54"
              fill="none"
              stroke="#e2e8f0"
              strokeWidth="8"
            />
            <motion.circle
              cx="60"
              cy="60"
              r="54"
              fill="none"
              stroke={colors.stroke}
              strokeWidth="8"
              strokeLinecap="round"
              strokeDasharray={339.292}
              initial={{ strokeDashoffset: 339.292 }}
              animate={{ strokeDashoffset: 339.292 - (339.292 * score.score) / 100 }}
              transition={{ duration: 1, ease: 'easeOut' }}
            />
          </svg>
          <div className="absolute inset-0 flex items-center justify-center">
            <div>
              <p className={`text-3xl font-bold ${colors.text}`}>{score.score}</p>
              <p className="text-xs text-slate-500 capitalize">{score.riskLevel}</p>
            </div>
          </div>
        </div>
      </div>

      <div className="space-y-2">
        <p className="text-sm font-medium text-slate-700">Factores de riesgo:</p>
        <ul className="space-y-1">
          {score.factors.map((factor, index) => (
            <li key={index} className="text-sm text-slate-600 flex items-center gap-2">
              <span className="w-1.5 h-1.5 bg-banking-400 rounded-full" />
              {factor}
            </li>
          ))}
        </ul>
      </div>

      <div className="mt-4 p-3 bg-slate-50 rounded-lg">
        <p className="text-xs text-slate-600">{score.explanation}</p>
      </div>
    </motion.div>
  )
}

export default FraudScore
