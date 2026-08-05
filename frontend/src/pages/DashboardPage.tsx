import React, { useEffect } from 'react';
import { Header, Sidebar, Footer } from '@/components/layout';
import { AccountCard } from '@/components/account/AccountCard';
import { MovimientosTable } from '@/components/account/MovimientosTable';
import { BalanceChart } from '@/components/account/BalanceChart';
import { StatsGrid, Card, Button } from '@/components/ui';
import { useAuthStore } from '@/store/authStore';
import { useAccountStore } from '@/store/accountStore';
import { useNavigate } from 'react-router-dom';
import {
  ArrowRightLeft,
  TrendingUp,
  PiggyBank,
  CreditCard,
  RefreshCw,
} from 'lucide-react';
import { motion } from 'framer-motion';

export const DashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuthStore();
  const { cuentas, saldoActual, movimientos, selectedCuentaId, setSelectedCuenta, fetchCuentas, fetchSaldo, fetchMovimientos, isLoading } = useAccountStore();

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    const cargarDatos = async () => {
      if (user?.id) {
        await fetchCuentas(user.id);
        if (!selectedCuentaId && cuentas.length > 0) {
          setSelectedCuenta(cuentas[0].id);
        }
      }
    };

    cargarDatos();
  }, [isAuthenticated, user?.id, fetchCuentas, fetchSaldo, fetchMovimientos, setSelectedCuenta, navigate]);

  useEffect(() => {
    if (selectedCuentaId) {
      fetchSaldo(selectedCuentaId);
      fetchMovimientos(selectedCuentaId);
    }
  }, [selectedCuentaId, fetchSaldo, fetchMovimientos]);

  if (!isAuthenticated) return null;

  const dashboardStats = [
    {
      label: 'Saldo Total',
      value: saldoActual ? `$${saldoActual.saldoDisponible.toFixed(2)}` : '--',
      icon: <PiggyBank className="w-6 h-6 text-success-500" />,
      color: 'success' as const,
    },
    {
      label: 'Cuentas Activas',
      value: cuentas.length,
      icon: <CreditCard className="w-6 h-6 text-primary-500" />,
      color: 'primary' as const,
    },
    {
      label: 'Movimientos (30d)',
      value: movimientos?.length || 0,
      icon: <ArrowRightLeft className="w-6 h-6 text-warning-500" />,
      color: 'warning' as const,
    },
    {
      label: 'Transferencias Pendientes',
      value: 0,
      icon: <RefreshCw className="w-6 h-6 text-surface-400" />,
      color: 'neutral' as const,
    },
  ];

  return (
    <div className="min-h-screen bg-surface-50 flex">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden ml-64">
        <Header />
        <main className="flex-1 overflow-y-auto p-6">
          <div className="max-w-7xl mx-auto">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4 }}
              className="flex items-center justify-between mb-6"
            >
              <h1 className="text-2xl font-bold text-dark-500">
                Bienvenido, {user?.nombreCompleto || 'Usuario'}
              </h1>
              <Button
                variant="primary"
                size="sm"
                icon={<ArrowRightLeft className="w-4 h-4" />}
                onClick={() => navigate('/transfers')}
              >
                Nueva Transferencia
              </Button>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: 0.1 }}
              className="mb-8"
            >
              <StatsGrid items={dashboardStats} cols={4} />
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: 0.2 }}
              className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6"
            >
              <div>
                <AccountCard saldo={saldoActual || undefined} isLoading={isLoading} />
              </div>

              <Card title="Evolución del Saldo" icon={<TrendingUp className="w-5 h-5 text-primary-500" />}>
                <BalanceChart
                  movimientos={movimientos?.map(m => ({ fecha: m.fechaCreacion, saldoNuevo: m.saldoNuevo }))}
                  isLoading={isLoading}
                />
              </Card>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: 0.3 }}
              className="mb-6"
            >
              <Card title="Movimientos Recientes" icon={<ArrowRightLeft className="w-5 h-5 text-primary-500" />}>
                <MovimientosTable movimientos={movimientos || []} isLoading={isLoading} itemsPerPage={5} />
              </Card>
            </motion.div>
          </div>
        </main>
        <Footer />
      </div>
    </div>
  );
};
