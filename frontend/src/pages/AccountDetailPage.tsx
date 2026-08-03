import React, { useEffect, useState } from 'react';
import { Header, Sidebar, Footer } from '@/components/layout';
import { AccountCard } from '@/components/account/AccountCard';
import { BalanceChart } from '@/components/account/BalanceChart';
import { MovimientosList } from '@/components/account/MovimientosList';
import { LedgerExtract } from '@/components/ledger/LedgerExtract';
import { useAuthStore } from '@/store/authStore';
import { useAccountStore } from '@/store/accountStore';
import { useNavigate, useParams } from 'react-router-dom';

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
    <div className="min-h-screen bg-surface-950 flex">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <Header />
        <main className="flex-1 overflow-y-auto p-6">
          <div className="max-w-7xl mx-auto">
            <h1 className="text-2xl font-bold text-surface-100 mb-6">
              Detalle de Cuenta
            </h1>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
              <div className="lg:col-span-1">
                <AccountCard saldo={saldoActual || undefined} isLoading={isLoading} />
              </div>
              <div className="lg:col-span-2">
                <BalanceChart movimientos={movimientos?.map(m => ({ fecha: m.fechaCreacion, saldoNuevo: m.saldoNuevo }))} isLoading={isLoading} />
              </div>
            </div>

            <div className="mb-6">
              <h3 className="text-lg font-semibold text-surface-100 mb-4">Movimientos</h3>
              <MovimientosList movimientos={movimientos} isLoading={isLoading} />
            </div>

            <div className="mb-6">
              <h3 className="text-lg font-semibold text-surface-100 mb-4">Extracto Contable (Ledger)</h3>
              <LedgerExtract asientos={[]} isLoading={false} />
            </div>
          </div>
        </main>
        <Footer />
      </div>
    </div>
  );
};
