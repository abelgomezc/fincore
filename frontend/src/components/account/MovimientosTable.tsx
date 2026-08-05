import React, { useState, useMemo } from 'react';
import { Movimiento } from '@/types/account';
import { formatCurrency, formatDate } from '@/lib/utils';
import { clsx } from '@/lib/utils';
import { Badge } from '@/components/ui/Badge';
import { Button } from '@/components/ui/Button';
import { motion, AnimatePresence } from 'framer-motion';
import {
  IconArrowUpRight,
  IconArrowDownLeft,
  IconReceipt,
  IconCreditCard,
  IconCoin,
  IconChevronDown,
  IconChevronUp,
  IconChevronLeft,
  IconChevronRight,
} from '@tabler/icons-react';

interface MovimientosTableProps {
  movimientos: Movimiento[];
  isLoading?: boolean;
  itemsPerPage?: number;
}

const tipoIcon: Record<string, any> = {
  DEBITO: <IconArrowUpRight className="w-4 h-4 text-red-600" />,
  RETENCION: <IconReceipt className="w-4 h-4 text-amber-600" />,
  COMISION: <IconCreditCard className="w-4 h-4 text-slate-500" />,
  CREDITO: <IconArrowDownLeft className="w-4 h-4 text-green-600" />,
  LIBERACION: <IconCoin className="w-4 h-4 text-green-600" />,
};

const tipoLabel: Record<string, string> = {
  DEBITO: 'Débito',
  RETENCION: 'Retención',
  COMISION: 'Comisión',
  CREDITO: 'Crédito',
  LIBERACION: 'Liberación',
};

export const MovimientosTable: React.FC<MovimientosTableProps> = ({
  movimientos,
  isLoading,
  itemsPerPage = 10,
}) => {
  const [currentPage, setCurrentPage] = useState(1);
  const [expandedRow, setExpandedRow] = useState<number | null>(null);

  const totalPages = Math.ceil(movimientos.length / itemsPerPage);
  const paginated = useMemo(() => {
    const start = (currentPage - 1) * itemsPerPage;
    return movimientos.slice(start, start + itemsPerPage);
  }, [movimientos, currentPage, itemsPerPage]);

  if (isLoading) {
    return (
      <div className="space-y-3">
        {Array(5).fill(0).map((_, i) => (
          <div key={i} className="bg-slate-200 animate-pulse rounded-xl h-16 border border-slate-200" />
        ))}
      </div>
    );
  }

  if (movimientos.length === 0) {
    return (
      <div className="text-center py-12 text-slate-400 bg-slate-50 rounded-xl border border-slate-200">
        <IconReceipt className="w-12 h-12 mx-auto mb-3 text-slate-300" />
        <p>No hay movimientos en este momento</p>
      </div>
    );
  }

  return (
    <div className="overflow-x-auto">
      <table className="w-full">
        <thead>
          <tr className="border-b border-slate-200">
            <th className="text-left py-3 px-4 text-xs font-medium text-slate-500 uppercase">Fecha</th>
            <th className="text-left py-3 px-4 text-xs font-medium text-slate-500 uppercase">Tipo</th>
            <th className="text-left py-3 px-4 text-xs font-medium text-slate-500 uppercase">Descripción</th>
            <th className="text-right py-3 px-4 text-xs font-medium text-slate-500 uppercase">Monto</th>
            <th className="text-right py-3 px-4 text-xs font-medium text-slate-500 uppercase">Saldo</th>
          </tr>
        </thead>
        <tbody>
          {paginated.map((mov) => {
            const isDebito = mov.tipoMovimiento === 'DEBITO' || mov.tipoMovimiento === 'RETENCION' || mov.tipoMovimiento === 'COMISION';
            const Icon = tipoIcon[mov.tipoMovimiento] || <IconArrowUpRight className="w-4 h-4" />;
            const isExpanded = expandedRow === mov.id;

            return (
              <React.Fragment key={mov.id}>
                <motion.tr
                  className="border-b border-slate-200 hover:bg-blue-50 transition-colors cursor-pointer"
                  onClick={() => setExpandedRow(isExpanded ? null : mov.id)}
                  whileHover={{ backgroundColor: 'rgb(239 246 255)' }}
                >
                  <td className="py-3 px-4 text-sm text-slate-700">{formatDate(mov.fechaCreacion)}</td>
                  <td className="py-3 px-4">
                    <div className="flex items-center space-x-2">
                      {Icon}
                      <Badge variant="neutral" size="sm">
                        {tipoLabel[mov.tipoMovimiento] || mov.tipoMovimiento}
                      </Badge>
                    </div>
                  </td>
                  <td className="py-3 px-4">
                    <div className="font-medium text-slate-800">{mov.descripcion}</div>
                    {mov.referencia && <div className="text-xs text-slate-500">{mov.referencia}</div>}
                  </td>
                  <td className="py-3 px-4 text-right">
                    <span className={clsx('font-semibold', isDebito ? 'text-red-600' : 'text-green-600')}>
                      {isDebito ? '-' : '+'}{formatCurrency(mov.monto)}
                    </span>
                  </td>
                  <td className="py-3 px-4 text-right">
                    <span className="text-sm text-slate-700 font-medium">
                      {formatCurrency(mov.saldoNuevo)}
                    </span>
                  </td>
                </motion.tr>
                <AnimatePresence>
                  {isExpanded && (
                    <motion.tr
                      initial={{ opacity: 0, height: 0 }}
                      animate={{ opacity: 1, height: 'auto' }}
                      exit={{ opacity: 0, height: 0 }}
                      transition={{ duration: 0.3 }}
                    >
                      <td colSpan={5} className="p-0">
                        <motion.div
                          initial={{ opacity: 0 }}
                          animate={{ opacity: 1 }}
                          className="px-4 py-3 bg-slate-50 border-b border-slate-200"
                        >
                          <div className="grid grid-cols-2 gap-4 text-sm">
                            <div>
                              <span className="text-slate-500">Referencia:</span>
                              <span className="ml-2 text-slate-700">{mov.referencia || 'N/A'}</span>
                            </div>
                            <div>
                              <span className="text-slate-500">Tipo:</span>
                              <span className="ml-2 text-slate-700">{tipoLabel[mov.tipoMovimiento] || mov.tipoMovimiento}</span>
                            </div>
                            <div>
                              <span className="text-slate-500">Saldo Anterior:</span>
                              <span className="ml-2 text-slate-700">{formatCurrency(mov.saldoAnterior)}</span>
                            </div>
                            <div>
                              <span className="text-slate-500">Saldo Nuevo:</span>
                              <span className="ml-2 text-slate-700 font-medium">{formatCurrency(mov.saldoNuevo)}</span>
                            </div>
                          </div>
                        </motion.div>
                      </td>
                    </motion.tr>
                  )}
                </AnimatePresence>
              </React.Fragment>
            );
          })}
        </tbody>
      </table>

      {totalPages > 1 && (
        <div className="flex items-center justify-between mt-4 pt-4 border-t border-slate-200">
          <span className="text-sm text-slate-500">
            Página {currentPage} de {totalPages}
          </span>
          <div className="flex items-center space-x-2">
            <Button
              variant="outline"
              size="sm"
              disabled={currentPage === 1}
              icon={<IconChevronLeft className="w-4 h-4" />}
              onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
            >
              Anterior
            </Button>
            <Button
              variant="outline"
              size="sm"
              disabled={currentPage === totalPages}
              icon={<IconChevronRight className="w-4 h-4" />}
              onClick={() => setCurrentPage(Math.min(totalPages, currentPage + 1))}
            >
              Siguiente
            </Button>
          </div>
        </div>
      )}
    </div>
  );
};
