import { useState } from 'react'
import { motion } from 'framer-motion'
import type { TransferFormData } from '../types'

interface TransferFormProps {
  accounts: { id: string; owner: string }[]
  onTransfer?: (data: TransferFormData) => void
}

/**
 * Formulario de transferencia bancaria
 * Permite enviar fondos entre cuentas del sistema FinCore
 */
function TransferForm({ accounts, onTransfer }: TransferFormProps) {
  const [formData, setFormData] = useState<TransferFormData>({
    fromAccountId: accounts[0]?.id || '',
    toAccountId: accounts[1]?.id || '',
    amount: 0,
    currency: 'USD',
    concept: ''
  })
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (formData.amount <= 0) return

    setIsSubmitting(true)
    onTransfer?.(formData)

    // Simulación de envío de transferencia
    console.log('Enviando transferencia:', formData)

    setTimeout(() => {
      setIsSubmitting(false)
      setFormData({ ...formData, amount: 0, concept: '' })
    }, 1000)
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="card-banking p-6"
    >
      <h3 className="text-lg font-semibold text-slate-900 mb-4">Nueva Transferencia</h3>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            Cuenta origen
          </label>
          <select
            value={formData.fromAccountId}
            onChange={(e) => setFormData({ ...formData, fromAccountId: e.target.value })}
            className="w-full px-3 py-2.5 rounded-lg border border-slate-300 bg-white text-sm text-slate-900 focus:outline-none focus:ring-2 focus:ring-banking-500 focus:border-transparent"
          >
            {accounts.map((acc) => (
              <option key={acc.id} value={acc.id}>
                {acc.owner} ({acc.id})
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            Cuenta destino
          </label>
          <select
            value={formData.toAccountId}
            onChange={(e) => setFormData({ ...formData, toAccountId: e.target.value })}
            className="w-full px-3 py-2.5 rounded-lg border border-slate-300 bg-white text-sm text-slate-900 focus:outline-none focus:ring-2 focus:ring-banking-500 focus:border-transparent"
          >
            {accounts.map((acc) => (
              <option key={acc.id} value={acc.id}>
                {acc.owner} ({acc.id})
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            Monto (USD)
          </label>
          <input
            type="number"
            value={formData.amount || ''}
            onChange={(e) => setFormData({ ...formData, amount: parseFloat(e.target.value) || 0 })}
            placeholder="0.00"
            min="0.01"
            step="0.01"
            className="w-full px-3 py-2.5 rounded-lg border border-slate-300 bg-white text-sm text-slate-900 focus:outline-none focus:ring-2 focus:ring-banking-500 focus:border-transparent"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            Concepto
          </label>
          <input
            type="text"
            value={formData.concept}
            onChange={(e) => setFormData({ ...formData, concept: e.target.value })}
            placeholder="Descripción de la transferencia"
            className="w-full px-3 py-2.5 rounded-lg border border-slate-300 bg-white text-sm text-slate-900 focus:outline-none focus:ring-2 focus:ring-banking-500 focus:border-transparent"
          />
        </div>

        <button
          type="submit"
          disabled={isSubmitting || formData.amount <= 0 || formData.fromAccountId === formData.toAccountId}
          className="w-full bg-banking-600 hover:bg-banking-700 disabled:bg-slate-300 disabled:cursor-not-allowed text-white font-medium py-2.5 rounded-lg transition-colors text-sm"
        >
          {isSubmitting ? 'Procesando...' : 'Iniciar Transferencia'}
        </button>
      </form>
    </motion.div>
  )
}

export default TransferForm
