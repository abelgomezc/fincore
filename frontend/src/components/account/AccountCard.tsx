import React from 'react';
import { Saldo } from '@/types/account';
import { formatCurrency } from '@/lib/utils';
import { clsx } from '@/lib/utils';

interface AccountCardProps {
  saldo?: Saldo;
  isLoading?: boolean;
}

export const AccountCard: React.FC<AccountCardProps> = ({ saldo, isLoading }) => {
  if (isLoading || !saldo) {
    return (
      <div className="bg-surface-800 rounded-xl p-6 animate-pulse">
        <div className="h-4 bg-surface-700 rounded w-3/4 mb-4"></div>
        <div className="h-8 bg-surface-700 rounded w-1/2 mb-2"></div>
        <div className="h-4 bg-surface-700 rounded w-full"></div>
      </div>
    );
  }

  const saldos = [
    { label: 'Disponible', value: saldo.saldoDisponible, color: 'text-banking-success' },
    { label: 'Retenido', value: saldo.saldoRetenido, color: 'text-banking-warning' },
    { label: 'Contable', value: saldo.saldoContable, color: 'text-primary-400' },
    { label: 'Proyectado', value: saldo.saldoProyectado, color: 'text-surface-300' },
  ];

  return (
    <div className="bg-surface-800 rounded-xl p-6 border border-surface-700">
      <div className="flex items-center justify-between mb-4">
        <div>
          <h3 className="text-sm font-medium text-surface-400">Cuenta {saldo.numeroCuenta}</h3>
          <p className={clsx('text-2xl font-bold', saldo.saldoDisponible >= 0 ? 'text-banking-success' : 'text-banking-error')}>
            {formatCurrency(saldo.saldoDisponible, saldo.moneda)}
          </p>
        </div>
        <span className={clsx(
          'px-2 py-1 rounded-full text-xs font-medium',
          saldo.estado === 'ACTIVA' ? 'bg-banking-success/20 text-banking-success' :
          saldo.estado === 'BLOQUEADA' ? 'bg-banking-error/20 text-banking-error' :
          'bg-banking-warning/20 text-banking-warning'
        )}>
          {saldo.estado}
        </span>
      </div>

      <div className="grid grid-cols-2 gap-3 mt-4">
        {saldos.map((s) => (
          <div key={s.label} className="bg-surface-900 rounded-lg p-3">
            <div className="text-xs text-surface-500">{s.label}</div>
            <div className={`text-sm font-semibold ${s.color}`}>
              {formatCurrency(s.value, saldo.moneda)}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
