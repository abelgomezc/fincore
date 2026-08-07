import React, { useEffect } from 'react';
import { Header, Sidebar, Footer } from '@/components/layout';
import { AccountCard } from '@/components/account/AccountCard';
import { BalanceChart } from '@/components/account/BalanceChart';
import { Card } from '@/components/ui';
import { useAuthStore } from '@/store/authStore';
import { useAccountStore } from '@/store/accountStore';
import { useNavigate } from 'react-router-dom';
import { IconCreditCard, IconTrendingUp, IconFileText } from '@tabler/icons-react';
import { motion } from 'framer-motion';

export const AccountDetailPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();
  const { selectedCuentaId, setSelectedCuenta, cuentas, saldoActual, movimientos, isLoading, fetchCuentas, fetchSaldo, fetchMovimientos } = useAccountStore();

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
  }, [isAuthenticated, navigate]);

  useEffect(() => {
    if (cuentas.length === 0 && isAuthenticated) {
      const user = useAuthStore.getState().user;
      if (user?.id) {
        fetchCuentas(user.id);
      }
    }
  }, [cuentas.length, isAuthenticated, fetchCuentas]);

  useEffect(() => {
    if (!selectedCuentaId && cuentas.length > 0) {
      setSelectedCuenta(cuentas[0].id);
    }
  }, [cuentas, selectedCuentaId, setSelectedCuenta]);

  useEffect(() => {
    if (selectedCuentaId) {
      fetchSaldo(selectedCuentaId);
      fetchMovimientos(selectedCuentaId);
    }
  }, [selectedCuentaId, fetchSaldo, fetchMovimientos]);

  if (!isAuthenticated) return null;

  const cuenta = cuentas.find((c) => c.id === selectedCuentaId);

  return (
    <div className="min-h-screen bg-slate-50 flex">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden ml-64">
        <Header />
        <main className="flex-1 overflow-y-auto p-6">
          <div className="max-w-7xl mx-auto">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="flex items-center justify-between mb-6"
            >
              <h1 className="text-3xl font-bold text-slate-800 flex items-center">
                <IconCreditCard className="w-7 h-7 mr-3 text-blue-600" />
                Detalle de Cuenta
              </h1>
              {cuenta && (
                <div className="text-sm text-slate-500">
                  Cuenta: <span className="font-mono text-slate-800 font-medium">{cuenta.numeroCuenta}</span>
                </div>
              )}
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 }}
              className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6"
            >
              <div className="lg:col-span-1">
                <AccountCard saldo={saldoActual || undefined} isLoading={isLoading} />
              </div>
              <div className="lg:col-span-2">
                <Card title="Evolución del Saldo" icon={<IconTrendingUp className="w-5 h-5 text-blue-600" />}>
                  <BalanceChart
                    movimientos={movimientos?.map(m => ({ fecha: m.fechaCreacion, saldoNuevo: m.saldoNuevo }))}
                    isLoading={isLoading}
                  />
                </Card>
              </div>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.2 }}
              className="mb-6"
            >
              <Card title="Movimientos" icon={<IconFileText className="w-5 h-5 text-blue-600" />}>
                <div className="text-center py-12 text-slate-400">
                  <IconFileText className="w-12 h-12 mx-auto mb-3 text-slate-300" />
                  <p>Cargando movimientos...</p>
                </div>
              </Card>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.3 }}
              className="mb-6"
            >
              <Card title="Extracto Contable (Ledger)" icon={<IconFileText className="w-5 h-5 text-blue-600" />}>
                <div className="text-center py-12 text-slate-400">
                  <IconFileText className="w-12 h-12 mx-auto mb-3 text-slate-300" />
                  Los asientos contables se mostrarán aquí
                </div>
              </Card>
            </motion.div>
          </div>
        </main>
        <Footer />
      </div>
    </div>
  );
};
