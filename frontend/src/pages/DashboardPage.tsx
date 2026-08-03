import React, { useEffect } from 'react';
import { Header, Sidebar, Footer } from '@/components/layout';
import { AccountCard } from '@/components/account/AccountCard';
import { MovimientosList } from '@/components/account/MovimientosList';
import { useAuthStore } from '@/store/authStore';
import { useAccountStore } from '@/store/accountStore';
import { useNavigate } from 'react-router-dom';
import { clsx } from '@/lib/utils';

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

  return (
    <div className="min-h-screen bg-surface-950 flex">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <Header />
        <main className="flex-1 overflow-y-auto p-6">
          <div className="max-w-7xl mx-auto">
            <h1 className="text-2xl font-bold text-surface-100 mb-6">
              Bienvenido, {user?.nombreCompleto || 'Usuario'}
            </h1>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
              <AccountCard saldo={saldoActual || undefined} isLoading={isLoading} />

              <div className="bg-surface-800 rounded-xl p-6 border border-surface-700">
                <h3 className="text-lg font-semibold text-surface-100 mb-4">Transferencias Recientes</h3>
                <div className="text-center py-8 text-surface-500">
                  <p>No hay transferencias recientes</p>
                  <button
                    onClick={() => navigate('/transfers')}
                    className="text-primary-400 hover:text-primary-300 text-sm mt-2"
                  >
                    Ver todas las transferencias →
                  </button>
                </div>
              </div>
            </div>

            <div className="mb-6">
              <h3 className="text-lg font-semibold text-surface-100 mb-4">Movimientos Recientes</h3>
              <MovimientosList movimientos={movimientos} isLoading={isLoading} />
            </div>
          </div>
        </main>
        <Footer />
      </div>
    </div>
  );
};
