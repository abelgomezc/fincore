import React, { useEffect } from 'react';
import { Header, Sidebar, Footer } from '@/components/layout';
import { AccountCard } from '@/components/account/AccountCard';
import { BalanceChart } from '@/components/account/BalanceChart';
import { MovimientosTable } from '@/components/account/MovimientosTable';
import { Card } from '@/components/ui';
import { useAuthStore } from '@/store/authStore';
import { useAccountStore } from '@/store/accountStore';
import { useNavigate, useParams } from 'react-router-dom';
import { CreditCard, TrendingUp, FileText } from 'lucide-react';
import { motion } from 'framer-motion';

export const AccountDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();
  const { selectedCuentaId, setSelectedCuenta, cuentas, saldoActual, movimientos, isLoading } = useAccountStore();

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    if (id) {
      setSelectedCuenta(parseInt(id, 10));
    }
  }, [isAuthenticated, id, setSelectedCuenta, navigate]);

  if (!isAuthenticated) return null;

  const cuenta = cuentas.find((c) => c.id === selectedCuentaId);

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
              className="flex items-center justify-between mb-6"
            >
              <h1 className="text-2xl font-bold text-dark-500 flex items-center">
                <CreditCard className="w-6 h-6 mr-3 text-primary-600" />
                Detalle de Cuenta
              </h1>
              {cuenta && (
                <div className="text-sm text-surface-500">
                  Cuenta: <span className="font-mono text-dark-500 font-medium">{cuenta.numeroCuenta}</span>
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
                <Card title="Evolución del Saldo" icon={<TrendingUp className="w-5 h-5 text-primary-500" />}>
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
              <Card title="Movimientos" icon={<FileText className="w-5 h-5 text-primary-500" />}>
                <MovimientosTable movimientos={movimientos || []} isLoading={isLoading} />
              </Card>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.3 }}
              className="mb-6"
            >
              <Card title="Extracto Contable (Ledger)" icon={<FileText className="w-5 h-5 text-primary-500" />}>
                <div className="text-center py-12 text-surface-400">
                  <FileText className="w-12 h-12 mx-auto mb-3 text-surface-300" />
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
