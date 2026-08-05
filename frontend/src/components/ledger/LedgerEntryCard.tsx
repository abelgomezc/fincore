import React from 'react';
import { clsx, formatCurrency, formatDate } from '@/lib/utils';
import { Badge } from '@/components/ui/Badge';
import { Card } from '@/components/ui/Card';
import { IconBook, IconCheck, IconX } from '@tabler/icons-react';
import { motion } from 'framer-motion';

export interface AsientoLinea {
  codigoCuenta: string;
  nombreCuenta: string;
  tipoMovimiento: 'DEBITO' | 'CREDITO';
  monto: number;
}

export interface LedgerEntryCardProps {
  numeroAsiento: string;
  idAsiento: number;
  descripcion: string;
  fechaCreacion: string;
  lineas: AsientoLinea[];
  totalDebitos: number;
  totalCreditos: number;
  estado: 'ACTIVO' | 'REVERTIDO';
}

export const LedgerEntryCard: React.FC<LedgerEntryCardProps> = ({
  numeroAsiento,
  idAsiento,
  descripcion,
  fechaCreacion,
  lineas,
  totalDebitos,
  totalCreditos,
  estado,
}) => {
  const isBalanced = Math.abs(totalDebitos - totalCreditos) < 0.01;

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      className="bg-white rounded-xl border border-slate-200 p-6 shadow-card"
    >
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center space-x-3">
          <div className="p-2 bg-blue-50 rounded-lg">
            <IconBook className="w-5 h-5 text-blue-600" />
          </div>
          <div>
            <h3 className="text-lg font-semibold text-slate-800">{numeroAsiento}</h3>
            <p className="text-sm text-slate-500">{descripcion}</p>
          </div>
        </div>
        <Badge variant={estado === 'REVERTIDO' ? 'warning' : 'success'} size="sm">
          {estado}
        </Badge>
      </div>

      <div className="text-xs text-slate-400 mb-4">
        {formatDate(fechaCreacion)}
      </div>

      <div className="space-y-2 mb-4">
        {lineas.map((linea, i) => (
          <motion.div
            key={i}
            initial={{ opacity: 0, x: -10 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: i * 0.03 }}
            className={clsx(
              'flex items-center justify-between p-3 rounded-lg border',
              linea.tipoMovimiento === 'DEBITO'
                ? 'bg-red-50 border-red-200'
                : 'bg-green-50 border-green-200'
            )}
          >
            <div className="flex items-center space-x-3">
              <span className="font-mono text-xs text-slate-500 bg-white px-2 py-1 rounded border border-slate-200">
                {linea.codigoCuenta}
              </span>
              <span className="text-sm text-slate-700">{linea.nombreCuenta}</span>
            </div>
            <div className="flex items-center space-x-3">
              <Badge variant={linea.tipoMovimiento === 'DEBITO' ? 'danger' : 'success'} size="sm">
                {linea.tipoMovimiento}
              </Badge>
              <span className={clsx(
                'font-medium',
                linea.tipoMovimiento === 'DEBITO' ? 'text-green-600' : 'text-red-600'
              )}>
                {formatCurrency(linea.monto)}
              </span>
            </div>
          </motion.div>
        ))}
      </div>

      <div className="border-t border-slate-200 pt-3 space-y-2">
        <div className="flex justify-between text-sm">
          <span className="text-slate-500">Total Débitos</span>
          <span className="text-green-600 font-medium">{formatCurrency(totalDebitos)}</span>
        </div>
        <div className="flex justify-between text-sm">
          <span className="text-slate-500">Total Créditos</span>
          <span className="text-red-600 font-medium">{formatCurrency(totalCreditos)}</span>
        </div>
        <div className={clsx(
          'flex justify-between text-sm font-semibold pt-2 border-t',
          isBalanced ? 'border-slate-200' : 'border-red-200'
        )}>
          <span className="text-slate-600">Balance</span>
          <span className={clsx(isBalanced ? 'text-green-600' : 'text-red-600')}>
            {isBalanced ? (
              <span className="flex items-center">
                <IconCheck className="w-4 h-4 mr-1" />
                Cuadrado
              </span>
            ) : (
              <span className="flex items-center">
                <IconX className="w-4 h-4 mr-1" />
                {formatCurrency(totalDebitos - totalCreditos)}
              </span>
            )}
          </span>
        </div>
      </div>
    </motion.div>
  );
};
