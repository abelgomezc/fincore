import React from 'react';
import { AsientoCard, AsientoLinea } from '@/components/ledger/AsientoCard';
import { clsx } from '@/lib/utils';

interface LedgerExtractProps {
  asientos: Array<{
    numeroAsiento: string;
    idAsiento: number;
    descripcion: string;
    fechaCreacion: string;
    lineas: AsientoLinea[];
    totalDebitos: number;
    totalCreditos: number;
    estado: 'ACTIVO' | 'REVERTIDO';
  }>;
  isLoading?: boolean;
}

export const LedgerExtract: React.FC<LedgerExtractProps> = ({ asientos, isLoading }) => {
  if (isLoading) {
    return (
      <div className="space-y-4">
        {Array(5).fill(0).map((_, i) => (
          <div key={i} className="bg-surface-800 rounded-xl p-6 animate-pulse h-48"></div>
        ))}
      </div>
    );
  }

  if (asientos.length === 0) {
    return (
      <div className="text-center py-12 text-surface-500">
        No hay asientos contables registrados
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {asientos.map((asiento) => (
        <AsientoCard key={asiento.idAsiento} {...asiento} />
      ))}

      <div className="flex justify-center pt-4">
        <button className="px-4 py-2 text-sm text-surface-300 hover:text-surface-100 transition-colors">
          Cargar más...
        </button>
      </div>
    </div>
  );
};
