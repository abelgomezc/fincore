import type { Account } from '../types'

/**
 * Tarjeta de cuenta bancaria
 * Muestra los diferentes tipos de saldos disponibles para la cuenta
 */
function AccountCard({ account }: { account: Account }) {
  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: account.currency
    }).format(amount)
  }

  const getStatusColor = (status: Account['status']) => {
    switch (status) {
      case 'active': return 'bg-emerald-100 text-emerald-700 border-emerald-200'
      case 'frozen': return 'bg-amber-100 text-amber-700 border-amber-200'
      case 'closed': return 'bg-red-100 text-red-700 border-red-200'
    }
  }

  return (
    <div className="card-banking p-6 hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between mb-4">
        <div>
          <h3 className="text-lg font-semibold text-slate-900">{account.owner}</h3>
          <p className="text-sm text-slate-500 mt-1">{account.accountNumber}</p>
        </div>
        <span className={`px-2.5 py-0.5 rounded-full text-xs font-medium border ${getStatusColor(account.status)}`}>
          {account.status.toUpperCase()}
        </span>
      </div>

      <div className="grid grid-cols-2 gap-4 mt-6">
        <div className="space-y-1">
          <p className="text-xs font-medium text-slate-500 uppercase tracking-wider">Disponible</p>
          <p className="text-2xl font-bold text-banking-700">{formatCurrency(account.availableBalance)}</p>
        </div>
        <div className="space-y-1">
          <p className="text-xs font-medium text-slate-500 uppercase tracking-wider">Retenido</p>
          <p className="text-2xl font-bold text-amber-600">{formatCurrency(account.heldBalance)}</p>
        </div>
        <div className="space-y-1">
          <p className="text-xs font-medium text-slate-500 uppercase tracking-wider">Contable</p>
          <p className="text-2xl font-bold text-slate-900">{formatCurrency(account.bookBalance)}</p>
        </div>
        <div className="space-y-1">
          <p className="text-xs font-medium text-slate-500 uppercase tracking-wider">Proyectado</p>
          <p className="text-2xl font-bold text-emerald-600">{formatCurrency(account.projectedBalance)}</p>
        </div>
      </div>

      <div className="mt-4 pt-4 border-t border-slate-100">
        <p className="text-xs text-slate-400">
          Última actualización: {new Date(account.lastUpdate).toLocaleTimeString('es-ES')}
        </p>
      </div>
    </div>
  )
}

export default AccountCard
