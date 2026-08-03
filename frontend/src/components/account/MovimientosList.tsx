import React from 'react';
import { Movimiento } from '@/types/account';
import { formatCurrency, formatDateRelative } from '@/lib/utils';
import { clsx } from '@/lib/utils';

interface MovimientosListProps {
  movimientos: Movimiento[];
  isLoading?: boolean;
}

export const MovimientosList: React.FC<MovimientosListProps> = ({ movimientos, isLoading }) => {
  if (isLoading) {
    return (
      <div className="space-y-3">
        {Array(5).fill(0).map((_, i) => (
          <div key={i} className="bg-surface-800 rounded-lg p-4 animate-pulse">
            <div className="h-4 bg-surface-700 rounded w-full mb-2"></div>
            <div className="h-3 bg-surface-700 rounded w-3/4"></div>
          </div>
        ))}
      </div>
    );
  }

  if (movimientos.length === 0) {
    return (
      <div className="text-center py-8 text-surface-500">
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
            className="bg-surface-800 rounded-lg p-4 border border-surface-700 hover:bg-surface-700/50 transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-3">
                <div className={clsx(
                  'w-10 h-10 rounded-full flex items-center justify-center',
                  isDebito ? 'bg-banking-error/20 text-banking-error' : 'bg-banking-success/20 text-banking-success'
                )}>
                  {sign}
                </div>
                <div>
                  <div className="font-medium text-surface-100">{movimiento.descripcion}</div>
                  <div className="text-sm text-surface-500">{movimiento.referencia}</div>
                </div>
              </div>
              <div className="text-right">
                <div className={clsx(
                  'font-semibold',
                  isDebito ? 'text-banking-error' : 'text-banking-success'
                )}>
                  {sign}{formatCurrency(movimiento.monto)}
                </div>
                <div className="text-xs text-surface-500">
                  {formatDateRelative(movimiento.fechaCreacion)}
                </div>
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
};
