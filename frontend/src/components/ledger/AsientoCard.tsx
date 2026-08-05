import React from 'react';
import { clsx, formatCurrency, formatDate } from '@/lib/utils';
import { Card, Badge } from '@/components/ui';
import { BookOpen, CheckCircle, XCircle } from 'lucide-react';
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
      className="bg-card-50 rounded-xl border border-surface-200 p-6 shadow-card"
    >
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center space-x-3">
          <BookOpen className="w-5 h-5 text-primary-500" />
          <div>
            <h3 className="text-lg font-semibold text-dark-500">{numeroAsiento}</h3>
            <p className="text-sm text-surface-500">{descripcion}</p>
          </div>
        </div>
        <Badge variant={estado === 'REVERTIDO' ? 'warning' : 'success'} size="sm">
          {estado}
        </Badge>
      </div>

      <div className="text-xs text-surface-400 mb-4">
        {formatDate(fechaCreacion)}
      </div>

      <div className="space-y-2 mb-4">
        {lineas.map((linea, i) => (
          <motion.div
            key={i}
            initial={{ opacity: 0, x: -10 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: i * 0.03 }}
            className="flex items-center justify-between p-2 rounded-lg bg-surface-50 border border-surface-200"
          >
            <div className="flex items-center space-x-3">
              <span className="font-mono text-xs text-surface-400 bg-surface-100 px-2 py-1 rounded">
                {linea.codigoCuenta}
              </span>
              <span className="text-sm text-dark-500">{linea.nombreCuenta}</span>
            </div>
            <div className={clsx(
              'font-medium',
              linea.tipoMovimiento === 'DEBITO' ? 'text-success-600' : 'text-danger-600'
            )}>
              <span className="text-surface-400 mr-1">
                {linea.tipoMovimiento === 'DEBITO' ? 'Débito' : 'Crédito'}
              </span>
              {formatCurrency(linea.monto)}
            </div>
          </motion.div>
        ))}
      </div>

      <div className="border-t border-surface-200 pt-3 space-y-2">
        <div className="flex justify-between text-sm">
          <span className="text-surface-500">Total Débitos</span>
          <span className="text-success-600 font-medium">{formatCurrency(totalDebitos)}</span>
        </div>
        <div className="flex justify-between text-sm">
          <span className="text-surface-500">Total Créditos</span>
          <span className="text-danger-600 font-medium">{formatCurrency(totalCreditos)}</span>
        </div>
        <div className="flex justify-between text-sm font-medium pt-2 border-t border-surface-300">
          <span className="text-surface-600">Balance</span>
          <span className={isBalanced ? 'text-success-600' : 'text-danger-600'}>
            {isBalanced ? (
              <span className="flex items-center">
                <CheckCircle className="w-4 h-4 mr-1" />
                Cuadrado
              </span>
            ) : (
              formatCurrency(totalDebitos - totalCreditos)
            )}
          </span>
        </div>
      </div>
    </motion.div>
  );
};
