import React from 'react';
import { Saldo } from '@/types/account';
import { formatCurrency } from '@/lib/utils';
import { clsx } from '@/lib/utils';
import { Badge } from '@/components/ui/Badge';
import { motion } from 'framer-motion';
import {
  IconArrowUpCircle,
  IconLock,
  IconBook,
  IconTrendingUp,
  IconPlayerPause,
  IconCreditCard,
} from '@tabler/icons-react';

interface AccountCardProps {
  saldo?: Saldo;
  isLoading?: boolean;
}

interface SaldoItem {
  label: string;
  value: number;
  icon: any;
  color: string;
}

export const AccountCard: React.FC<AccountCardProps> = ({ saldo, isLoading }) => {
  if (isLoading || !saldo) {
    return (
      <div className="bg-slate-100 rounded-2xl p-6 animate-pulse h-48 w-full border border-slate-200" />
    );
  }

  const saldos: SaldoItem[] = [
    {
      label: 'Disponible',
      value: saldo.saldoDisponible,
      icon: <IconArrowUpCircle className="w-5 h-5 text-green-600" />,
      color: saldo.saldoDisponible >= 0 ? 'text-green-600' : 'text-red-600',
    },
    {
      label: 'Retenido',
      value: saldo.saldoRetenido,
      icon: <IconLock className="w-5 h-5 text-amber-600" />,
      color: 'text-amber-600',
    },
    {
      label: 'Contable',
      value: saldo.saldoContable,
      icon: <IconBook className="w-5 h-5 text-blue-600" />,
      color: 'text-blue-600',
    },
    {
      label: 'Proyectado',
      value: saldo.saldoProyectado,
      icon: <IconTrendingUp className="w-5 h-5 text-slate-500" />,
      color: 'text-slate-600',
    },
  ];

  const formatAccountNumber = (num: string): string => {
    const padded = num.padStart(12, '0');
    return padded.replace(/(\d{4})(\d{4})(\d{4})/, '$1 $2 $3');
  };

  const estadoConfig = {
    ACTIVA: { label: 'Activa', variant: 'success' as const },
    BLOQUEADA: { label: 'Bloqueada', variant: 'danger' as const },
    INACTIVA: { label: 'Inactiva', variant: 'neutral' as const },
    SUSPENDIDA: { label: 'Suspendida', variant: 'warning' as const },
  };

  const estado = estadoConfig[saldo.estado] || estadoConfig.ACTIVA;

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      className="bg-gradient-to-br from-blue-900 to-blue-700 rounded-2xl p-6 shadow-lg text-white"
    >
      <div className="flex items-center justify-between mb-4">
        <div>
          <div className="flex items-center space-x-2 mb-1">
            <IconCreditCard className="w-4 h-4 text-blue-200" />
            <span className="text-sm font-medium text-blue-200">Cuenta</span>
          </div>
          <div className="text-2xl font-mono font-bold text-white">
            {formatAccountNumber(saldo.numeroCuenta)}
          </div>
        </div>

        <Badge variant={estado.variant} size="sm" className="text-white border-blue-200">
          {estado.label}
        </Badge>
      </div>

      <div className="mb-4">
        <p className="text-sm text-blue-200 mb-1">Saldo Disponible</p>
        <p className={clsx(
          'text-3xl font-bold',
          saldo.saldoDisponible >= 0 ? 'text-green-300' : 'text-red-300'
        )}>
          {formatCurrency(saldo.saldoDisponible, saldo.moneda)}
        </p>
      </div>

      <div className="grid grid-cols-2 gap-3">
        {saldos.map((s) => (
          <div
            key={s.label}
            className="bg-blue-800/30 rounded-xl p-3 transition-colors hover:bg-blue-800/40"
          >
            <div className="flex items-center space-x-2 mb-1">
              {s.icon}
              <span className="text-xs text-blue-200">{s.label}</span>
            </div>
            <div className={clsx('text-sm font-semibold', s.color)}>
              {formatCurrency(s.value, saldo.moneda)}
            </div>
          </div>
        ))}
      </div>
    </motion.div>
  );
};
