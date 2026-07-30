import { motion } from 'framer-motion'
import type { Transaction } from '../types'

interface TransferTimelineProps {
  transactions: Transaction[]
}

/**
 * Timeline de transferencias en tiempo real
 * Muestra los 10 estados del flujo FinCore
 */
const TRANSITION_STATES = [
  { key: 'INITIATED', label: 'Iniciada', description: 'Transferencia creada en el sistema' },
  { key: 'VALIDATING', label: 'Validando', description: 'Verificación de fondos y reglas' },
  { key: 'DEBIT_PENDING', label: 'Débito Pendiente', description: 'Esperando compensación' },
  { key: 'DEBITED', label: 'Debitada', description: 'Fondos retirados de origen' },
  { key: 'CREDIT_PENDING', label: 'Crédito Pendiente', description: 'Esperando liquidación' },
  { key: 'CREDITED', label: 'Acreditada', description: 'Fondos depositados en destino' },
  { key: 'FRAUD_CHECK', label: 'Fraude', description: 'Análisis de riesgo en curso' },
  { key: 'RECONCILING', label: 'Conciliando', description: 'Conciliación automática' },
  { key: 'LEDGER_POSTED', label: 'Asiento Contable', description: 'Registro en libro mayor' },
  { key: 'KAFKA_PUBLISHED', label: 'Kafka', description: 'Evento publicado en broker' },
  { key: 'SETTLED', label: 'Liquidada', description: 'Transferencia completada' }
]

function TransferTimeline({ transactions }: TransferTimelineProps) {
  if (transactions.length === 0) {
    return (
      <div className="card-banking p-6">
        <h3 className="text-lg font-semibold text-slate-900 mb-4">Timeline de Transferencias</h3>
        <p className="text-sm text-slate-500 text-center py-8">
          No hay transferencias recientes. Crea una para ver el flujo en tiempo real.
        </p>
      </div>
    )
  }

  const latestTransaction = transactions[0]

  const getCurrentIndex = (status: Transaction['status']) => {
    return TRANSITION_STATES.findIndex((s) => s.key === status)
  }

  const currentIndex = getCurrentIndex(latestTransaction.status)

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="card-banking p-6"
    >
      <h3 className="text-lg font-semibold text-slate-900 mb-2">Timeline: {latestTransaction.id}</h3>
      <p className="text-sm text-slate-500 mb-6">
        Iniciada: {new Date(latestTransaction.createdAt).toLocaleString('es-ES')}
      </p>

      <div className="relative">
        <div className="absolute top-4 left-4 right-4 h-0.5 bg-slate-200" />

        <div className="flex justify-between relative">
          {TRANSITION_STATES.map((state, index) => {
            const isCompleted = index <= currentIndex
            const isCurrent = index === currentIndex
            const isExactMatch = latestTransaction.status === state.key

            return (
              <div key={state.key} className="flex flex-col items-center flex-1">
                <motion.div
                  initial={{ scale: 0 }}
                  animate={{ scale: 1 }}
                  transition={{ delay: index * 0.05 }}
                  className={`w-8 h-8 rounded-full flex items-center justify-center border-2 z-10 transition-colors ${
                    isCompleted
                      ? 'bg-banking-600 border-banking-600 text-white'
                      : 'bg-white border-slate-300 text-slate-400'
                  } ${isCurrent ? 'ring-4 ring-banking-100' : ''}`}
                >
                  {isCompleted && (
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                    </svg>
                  )}
                </motion.div>
                <div className="mt-3 text-center">
                  <p className={`text-xs font-medium ${isCompleted ? 'text-banking-700' : 'text-slate-400'}`}>
                    {state.label}
                  </p>
                  {isExactMatch && (
                    <motion.p
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      className="text-xs text-slate-500 mt-1 max-w-[80px]"
                    >
                      {state.description}
                    </motion.p>
                    )}
                </div>
              </div>
            )
          })}
        </div>
      </div>
    </motion.div>
  )
}

export default TransferTimeline
