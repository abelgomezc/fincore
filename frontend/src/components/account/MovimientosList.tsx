import React from 'react';
import { Movimiento } from '@/types/account';
import { formatCurrency, formatDateRelative } from '@/lib/utils';
import { clsx } from '@/lib/utils';
import { IconArrowDown, IconArrowUp, IconCalendar } from '@tabler/icons-react';

interface MovimientosListProps {
  movimientos: Movimiento[];
  isLoading?: boolean;
}

export const MovimientosList: React.FC<MovimientosListProps> = ({ movimientos, isLoading }) => {
  if (isLoading) {
    return (
      <div className="space-y-3">
        {Array(5).fill(0).map((_, i) => (
          <div key={i} className="bg-slate-100 rounded-lg p-4 animate-pulse border border-slate-200">
            <div className="h-4 bg-slate-300 rounded w-full mb-2"></div>
            <div className="h-3 bg-slate-300 rounded w-3/4"></div>
          </div>
        ))}
      </div>
    );
  }

  if (movimientos.length === 0) {
    return (
      <div className="text-center py-8 text-slate-500">
        No hay movimientos en este momento
      </div>
    );
  }

  return (
    <div className="space-y-2">
      {movimientos.map((movimiento) => {
        const isDebito = movimiento.tipoMovimiento === 'DEBITO' || movimiento.tipoMovimiento === 'RETENCION';
        const sign = isDebito ? '-' : '+';

        return (
          <div
            key={movimiento.id}
            className="bg-white rounded-lg p-4 border border-slate-200 hover:bg-blue-50 hover:shadow-card transition-all"
          >
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-3">
                <div className={clsx(
                  'w-10 h-10 rounded-full flex items-center justify-center',
                  isDebito
                    ? 'bg-red-50 text-red-600'
                    : 'bg-green-50 text-green-600'
                )}>
                  {isDebito ? <IconArrowDown className="w-5 h-5" /> : <IconArrowUp className="w-5 h-5" />}
                </div>
                <div>
                  <div className="font-medium text-slate-800">{movimiento.descripcion}</div>
                  <div className="text-sm text-slate-500 flex items-center">
                    <IconCalendar className="w-3 h-3 mr-1" />
                    {formatDateRelative(movimiento.fechaCreacion)}
                  </div>
                </div>
              </div>
              <div className="text-right">
                <div className={clsx(
                  'font-semibold',
                  isDebito ? 'text-red-600' : 'text-green-600'
                )}>
                  {sign}{formatCurrency(movimiento.monto)}
                </div>
                <div className="text-xs text-slate-400">
                  Saldo: {formatCurrency(movimiento.saldoNuevo)}
                </div>
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
};
