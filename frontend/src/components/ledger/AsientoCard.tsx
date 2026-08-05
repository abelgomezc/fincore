import React from 'react';
import { clsx, formatCurrency, formatDate } from '@/lib/utils';
import { Card, Badge } from '@/components/ui';
import { IconBook, IconCheck, IconX } from '@tabler/icons-react';
import { motion } from 'framer-motion';

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
  const isBalanced = Math.abs(totalDebitos - totalCreditos) < 0.01;

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      className="bg-white rounded-xl border border-slate-200 p-6 shadow-card"
    >
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center space-x-3">
          <IconBook className="w-5 h-5 text-blue-600" />
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

      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-slate-200">
              <th className="text-left py-2 px-3 text-xs font-medium text-slate-500 uppercase">Código</th>
              <th className="text-left py-2 px-3 text-xs font-medium text-slate-500 uppercase">Cuenta</th>
              <th className="text-right py-2 px-3 text-xs font-medium text-slate-500 uppercase">Débito</th>
              <th className="text-right py-2 px-3 text-xs font-medium text-slate-500 uppercase">Crédito</th>
            </tr>
          </thead>
          <tbody>
            {lineas.map((linea, i) => (
              <motion.tr
                key={i}
                initial={{ opacity: 0, x: -10 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: i * 0.03 }}
                className="border-b border-slate-100 hover:bg-slate-50 transition-colors"
              >
                <td className="py-2 px-3 font-mono text-xs text-slate-500">{linea.codigoCuenta}</td>
                <td className="py-2 px-3 text-slate-700">{linea.nombreCuenta}</td>
                <td className={clsx(
                  'py-2 px-3 text-right font-medium',
                  linea.tipoMovimiento === 'DEBITO' ? 'text-green-600' : 'text-slate-400'
                )}>
                  {linea.tipoMovimiento === 'DEBITO' ? formatCurrency(linea.monto) : '—'}
                </td>
                <td className={clsx(
                  'py-2 px-3 text-right font-medium',
                  linea.tipoMovimiento === 'CREDITO' ? 'text-red-600' : 'text-slate-400'
                )}>
                  {linea.tipoMovimiento === 'CREDITO' ? formatCurrency(linea.monto) : '—'}
                </td>
              </motion.tr>
            ))}
            <tr className={clsx('font-bold border-t-2', isBalanced ? 'border-blue-200 bg-blue-50' : 'border-red-200 bg-red-50')}>
              <td colSpan={2} className="py-3 px-3 text-slate-700">
                TOTAL
              </td>
              <td className="py-3 px-3 text-right text-green-600">
                {formatCurrency(totalDebitos)}
              </td>
              <td className="py-3 px-3 text-right text-red-600">
                {formatCurrency(totalCreditos)}
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div className="mt-4 flex items-center justify-center">
        {isBalanced ? (
          <span className="flex items-center text-green-600 text-sm font-medium">
            <IconCheck className="w-4 h-4 mr-1" />
            Asiento cuadrado
          </span>
        ) : (
          <span className="flex items-center text-red-600 text-sm font-medium">
            <IconX className="w-4 h-4 mr-1" />
            Asiento descuadrado
          </span>
        )}
      </div>
    </motion.div>
  );
};
