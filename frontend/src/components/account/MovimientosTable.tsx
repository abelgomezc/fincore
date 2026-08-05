import React, { useState, useMemo } from 'react';
import { Movimiento } from '@/types/account';
import { formatCurrency, formatDate } from '@/lib/utils';
import { clsx } from '@/lib/utils';
import { Badge } from '@/components/ui/Badge';
import { Button } from '@/components/ui/Button';
import { ChevronLeft, ChevronRight, ArrowUpRight, ArrowDownLeft, Receipt, CreditCard, PiggyBank } from 'lucide-react';

interface MovimientosTableProps {
  movimientos: Movimiento[];
  isLoading?: boolean;
  itemsPerPage?: number;
}

const tipoIcon: Record<string, React.ReactNode> = {
  DEBITO: <ArrowUpRight className="w-4 h-4 text-danger-500" />,
  RETENCION: <Receipt className="w-4 h-4 text-warning-500" />,
  COMISION: <CreditCard className="w-4 h-4 text-surface-500" />,
  CREDITO: <ArrowDownLeft className="w-4 h-4 text-success-500" />,
  LIBERACION: <PiggyBank className="w-4 h-4 text-success-500" />,
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

  const totalPages = Math.ceil(movimientos.length / itemsPerPage);
  const paginated = useMemo(() => {
    const start = (currentPage - 1) * itemsPerPage;
    return movimientos.slice(start, start + itemsPerPage);
  }, [movimientos, currentPage, itemsPerPage]);

  if (isLoading) {
    return (
      <div className="space-y-3">
        {Array(5).fill(0).map((_, i) => (
          <div key={i} className="bg-surface-100 rounded-lg h-14 animate-pulse border border-surface-200"></div>
        ))}
      </div>
    );
  }

  if (movimientos.length === 0) {
    return (
      <div className="text-center py-12 text-surface-400 bg-surface-50 rounded-xl border border-surface-200">
        <Receipt className="w-12 h-12 mx-auto mb-3 text-surface-300" />
        <p>No hay movimientos en este momento</p>
      </div>
    );
  }

  return (
    <div className="overflow-x-auto">
      <table className="w-full">
        <thead>
          <tr className="border-b border-surface-200">
            <th className="text-left py-3 px-4 text-xs font-medium text-surface-500 uppercase">Fecha</th>
            <th className="text-left py-3 px-4 text-xs font-medium text-surface-500 uppercase">Tipo</th>
            <th className="text-left py-3 px-4 text-xs font-medium text-surface-500 uppercase">Descripción</th>
            <th className="text-right py-3 px-4 text-xs font-medium text-surface-500 uppercase">Monto</th>
            <th className="text-right py-3 px-4 text-xs font-medium text-surface-500 uppercase">Saldo</th>
            <th className="text-left py-3 px-4 text-xs font-medium text-surface-500 uppercase">Estado</th>
          </tr>
        </thead>
        <tbody>
          {paginated.map((mov) => {
            const isDebito = mov.tipoMovimiento === 'DEBITO' || mov.tipoMovimiento === 'RETENCION' || mov.tipoMovimiento === 'COMISION';
            const Icon = tipoIcon[mov.tipoMovimiento] || <ArrowUpRight className="w-4 h-4" />;
            return (
              <tr
                key={mov.id}
                className="border-b border-surface-200 hover:bg-surface-100 transition-colors"
              >
                <td className="py-3 px-4 text-sm text-dark-500">{formatDate(mov.fechaCreacion)}</td>
                <td className="py-3 px-4">
                  <div className="flex items-center space-x-2">
                    {Icon}
                    <Badge variant="neutral" size="sm">
                      {tipoLabel[mov.tipoMovimiento] || mov.tipoMovimiento}
                    </Badge>
                  </div>
                </td>
                <td className="py-3 px-4">
                  <div className="font-medium text-dark-500">{mov.descripcion}</div>
                  {mov.referencia && <div className="text-xs text-surface-400">{mov.referencia}</div>}
                </td>
                <td className="py-3 px-4 text-right">
                  <span className={clsx(
                    'font-semibold',
                    isDebito ? 'text-danger-600' : 'text-success-600'
                  )}>
                    {isDebito ? '-' : '+'}{formatCurrency(mov.monto)}
                  </span>
                </td>
                <td className="py-3 px-4 text-right">
                  <span className="text-sm text-dark-500 font-medium">
                    {formatCurrency(mov.saldoNuevo)}
                  </span>
                </td>
                <td className="py-3 px-4">
                  <Badge variant={mov.tipoMovimiento === 'DEBITO' || mov.tipoMovimiento === 'RETENCION' || mov.tipoMovimiento === 'COMISION' ? 'danger' : 'success'} size="sm">
                    {tipoLabel[mov.tipoMovimiento] || mov.tipoMovimiento}
                  </Badge>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>

      {totalPages > 1 && (
        <div className="flex items-center justify-between mt-4 pt-4 border-t border-surface-200">
          <span className="text-sm text-surface-500">
            Página {currentPage} de {totalPages}
          </span>
          <div className="flex items-center space-x-2">
            <Button
              variant="outline"
              size="sm"
              disabled={currentPage === 1}
              icon={<ChevronLeft className="w-4 h-4" />}
              onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
            >
              Anterior
            </Button>
            <Button
              variant="outline"
              size="sm"
              disabled={currentPage === totalPages}
              icon={<ChevronRight className="w-4 h-4" />}
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
