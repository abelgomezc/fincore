import React from 'react';
import { clsx, formatCurrency, formatDate } from '@/lib/utils';

export interface AsientoLinea {
  codigoCuenta: string;
  nombreCuenta: string;
  tipoMovimiento: 'DEBITO' | 'CREDITO';
  monto: number;
}

export interface AsientoCardProps {
  numeroAsiento: string;
  idAsiento: number;
  descripcion: string;
  fechaCreacion: string;
  lineas: AsientoLinea[];
  totalDebitos: number;
  totalCreditos: number;
  estado: 'ACTIVO' | 'REVERTIDO';
}

export const AsientoCard: React.FC<AsientoCardProps> = ({
  numeroAsiento,
  idAsiento,
  descripcion,
  fechaCreacion,
  lineas,
  totalDebitos,
  totalCreditos,
  estado,
}) => {
  const isBalanced = totalDebitos === totalCreditos;

  return (
    <div className="bg-surface-800 rounded-xl border border-surface-700 p-6">
      <div className="flex items-center justify-between mb-4">
        <div>
          <h3 className="text-lg font-semibold text-surface-100">{numeroAsiento}</h3>
          <p className="text-sm text-surface-400">{descripcion}</p>
        </div>
        <span className={clsx(
          'px-2 py-1 rounded-full text-xs font-medium',
          estado === 'REVERTIDO' ? 'bg-banking-warning/20 text-banking-warning' : 'bg-banking-success/20 text-banking-success'
        )}>
          {estado}
        </span>
      </div>

      <div className="space-y-2 mb-4">
        {lineas.map((linea, i) => (
          <div key={i} className="flex items-center justify-between text-sm">
            <div className="flex items-center space-x-3">
              <span className="font-mono text-xs text-surface-500">{linea.codigoCuenta}</span>
              <span className="text-surface-300">{linea.nombreCuenta}</span>
            </div>
            <div className={clsx(
              'font-medium',
              linea.tipoMovimiento === 'DEBITO' ? 'text-banking-success' : 'text-banking-error'
            )}>
              {linea.tipoMovimiento === 'DEBITO' ? '+' : '-'} {formatCurrency(linea.monto)}
            </div>
          </div>
        ))}
      </div>

      <div className="border-t border-surface-700 pt-3">
        <div className="flex justify-between text-sm mb-1">
          <span className="text-surface-400">Total Débitos</span>
          <span className="text-banking-success">{formatCurrency(totalDebitos)}</span>
        </div>
        <div className="flex justify-between text-sm mb-1">
          <span className="text-surface-400">Total Créditos</span>
          <span className="text-banking-error">{formatCurrency(totalCreditos)}</span>
        </div>
        <div className="flex justify-between text-sm font-medium pt-2 border-t border-surface-700">
          <span className="text-surface-300">Balance</span>
          <span className={isBalanced ? 'text-banking-success' : 'text-banking-error'}>
            {isBalanced ? 'Cuadrado' : formatCurrency(totalDebitos - totalCreditos)}
          </span>
        </div>
      </div>
    </div>
  );
};
