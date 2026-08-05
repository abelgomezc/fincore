import React from 'react';
import { Saldo } from '@/types/account';
import { formatCurrency } from '@/lib/utils';
import { clsx } from '@/lib/utils';
import { Badge } from '@/components/ui/Badge';
import { Card } from '@/components/ui/Card';
import {
  ArrowUpCircle,
  Lock,
  BookOpen,
  TrendingUp,
  PauseCircle,
  CreditCard,
} from 'lucide-react';
import { motion } from 'framer-motion';

interface AccountCardProps {
  saldo?: Saldo;
  isLoading?: boolean;
}

interface SaldoItem {
  label: string;
  value: number;
  icon: React.ReactNode;
  color: string;
}

export const AccountCard: React.FC<AccountCardProps> = ({ saldo, isLoading }) => {
  if (isLoading || !saldo) {
    return (
      <Card className="animate-pulse">
        <div className="h-4 bg-surface-200 rounded w-3/4 mb-4"></div>
        <div className="h-8 bg-surface-200 rounded w-1/2 mb-2"></div>
        <div className="h-4 bg-surface-200 rounded w-full"></div>
      </Card>
    );
  }

  const saldos: SaldoItem[] = [
    {
      label: 'Disponible',
      value: saldo.saldoDisponible,
      icon: <ArrowUpCircle className="w-5 h-5 text-success-500" />,
      color: saldo.saldoDisponible >= 0 ? 'text-success-600' : 'text-danger-600',
    },
    {
      label: 'Retenido',
      value: saldo.saldoRetenido,
      icon: <Lock className="w-5 h-5 text-warning-500" />,
      color: 'text-warning-600',
    },
    {
      label: 'Contable',
      value: saldo.saldoContable,
      icon: <BookOpen className="w-5 h-5 text-primary-500" />,
      color: 'text-primary-600',
    },
    {
      label: 'Proyectado',
      value: saldo.saldoProyectado,
      icon: <TrendingUp className="w-5 h-5 text-surface-500" />,
      color: 'text-surface-600',
    },
  ];

  const formatAccountNumber = (num: string): string => {
    const padded = num.padStart(12, '0');
    return padded.replace(/(\d{4})(\d{4})(\d{4})/, '$1 $2 $3');
  };

  const estadoConfig = {
    ACTIVA: { label: 'Activa', variant: 'success' as const, icon: null },
    BLOQUEADA: { label: 'Bloqueada', variant: 'danger' as const, icon: <PauseCircle className="w-4 h-4" /> },
    INACTIVA: { label: 'Inactiva', variant: 'neutral' as const, icon: null },
    SUSPENDIDA: { label: 'Suspendida', variant: 'warning' as const, icon: null },
  };

  const estado = estadoConfig[saldo.estado] || estadoConfig.ACTIVA;

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      className="bg-gradient-to-br from-primary-600 to-primary-700 rounded-xl p-6 shadow-lg text-white"
    >
      <div className="flex items-center justify-between mb-4">
        <div>
          <div className="flex items-center space-x-2 mb-1">
            <CreditCard className="w-4 h-4 text-primary-200" />
            <span className="text-sm font-medium text-primary-200">Cuenta</span>
          </div>
          <div className="text-2xl font-mono font-bold text-white">
            {formatAccountNumber(saldo.numeroCuenta)}
          </div>
        </div>

        <div className="flex items-center space-x-2">
          {estado.icon && estado.icon}
          <Badge variant={estado.variant} size="sm" className="text-white border-primary-200">
            {estado.label}
          </Badge>
        </div>
      </div>

      <div className="mb-4">
        <p className="text-sm text-primary-200 mb-1">Saldo Disponible</p>
        <p className={clsx(
          'text-3xl font-bold',
          saldo.saldoDisponible >= 0 ? 'text-success-300' : 'text-danger-300'
        )}>
          {formatCurrency(saldo.saldoDisponible, saldo.moneda)}
        </p>
      </div>

      <div className="grid grid-cols-2 gap-3">
        {saldos.map((s) => (
          <div
            key={s.label}
            className="bg-primary-800/30 rounded-lg p-3 transition-colors hover:bg-primary-800/40"
          >
            <div className="flex items-center space-x-2 mb-1">
              {s.icon}
              <span className="text-xs text-primary-200">{s.label}</span>
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
