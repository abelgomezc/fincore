import React from 'react';
import { AsientoCard, AsientoLinea } from '@/components/ledger/AsientoCard';
import { clsx } from '@/lib/utils';
import { motion } from 'framer-motion';

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
          <div key={i} className="bg-surface-100 rounded-xl p-6 animate-pulse h-48 border border-surface-200"></div>
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
      {asientos.map((asiento, index) => (
        <motion.div
          key={asiento.idAsiento}
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: index * 0.03 }}
        >
          <AsientoCard {...asiento} />
        </motion.div>
      ))}

      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        className="flex justify-center pt-4"
      >
        <button className="px-4 py-2 text-sm text-surface-500 hover:text-primary-600 transition-colors hover:bg-surface-100 rounded-lg">
          Cargar más...
        </button>
      </motion.div>
    </div>
  );
};
