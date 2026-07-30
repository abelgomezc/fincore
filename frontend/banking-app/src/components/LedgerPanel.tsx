import { motion } from 'framer-motion'
import type { LedgerEntry } from '../types'

interface LedgerPanelProps {
  entries: LedgerEntry[]
}

/**
 * Panel de asientos contables
 * Muestra los registros contables generados por las transacciones
 */
function LedgerPanel({ entries }: LedgerPanelProps) {
  const formatCurrency = (amount: number, currency: string = 'USD') => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency
    }).format(amount)
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="card-banking p-6"
    >
      <h3 className="text-lg font-semibold text-slate-900 mb-4">Asientos Contables</h3>

      {entries.length === 0 ? (
        <p className="text-sm text-slate-500 text-center py-8">
          No hay asientos contables generados aún.
        </p>
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-slate-200">
                <th className="text-left py-2 px-2 font-medium text-slate-500">Cuenta</th>
                <th className="text-right py-2 px-2 font-medium text-slate-500">Débito</th>
                <th className="text-right py-2 px-2 font-medium text-slate-500">Crédito</th>
                <th className="text-left py-2 px-2 font-medium text-slate-500">Descripción</th>
                <th className="text-right py-2 px-2 font-medium text-slate-500">Fecha</th>
              </tr>
            </thead>
            <tbody>
              {entries.map((entry, index) => (
                <motion.tr
                  key={entry.id}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.05 }}
                  className="border-b border-slate-100 last:border-b-0 hover:bg-slate-50"
                >
                  <td className="py-3 px-2 font-mono text-slate-700">{entry.accountCode}</td>
                  <td className="py-3 px-2 text-right font-medium text-emerald-600">
                    {entry.debit > 0 ? formatCurrency(entry.debit, entry.currency) : '-'}
                  </td>
                  <td className="py-3 px-2 text-right font-medium text-red-600">
                    {entry.credit > 0 ? formatCurrency(entry.credit, entry.currency) : '-'}
                  </td>
                  <td className="py-3 px-2 text-slate-600">{entry.description}</td>
                  <td className="py-3 px-2 text-right text-slate-400 text-xs">
                    {new Date(entry.postedAt).toLocaleString('es-ES')}
                  </td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </motion.div>
  )
}

export default LedgerPanel
