import React, { useEffect, useState } from 'react';
import { Header, Sidebar, Footer } from '@/components/layout';
import { TransferReviewList, FraudAlertList, ReportsPanel } from '@/components/backoffice';
import { useAuthStore } from '@/store/authStore';
import { backofficeApi } from '@/api/backofficeApi';
import { Transferencia } from '@/types/transfer';
import { EvaluacionFraude } from '@/types/fraud';
import { useNavigate } from 'react-router-dom';

export const BackofficePage: React.FC = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuthStore();
  const isAdmin = user?.roles?.some((r) => r === 'ADMIN' || r === 'SUPER_ADMIN' || r === 'AFRICANO');

  const [transferencias, setTransferencias] = useState<Transferencia[]>([]);
  const [fraudAlerts, setFraudAlerts] = useState<EvaluacionFraude[]>([]);
  const [conciliacion, setConciliacion] = useState<Record<string, unknown> | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!isAuthenticated || !isAdmin) {
      navigate('/');
      return;
    }
    cargarDatos();
  }, [isAuthenticated, isAdmin, navigate]);

  const cargarDatos = async () => {
    setIsLoading(true);
    try {
      const [transfers, frauds, reporte] = await Promise.all([
        backofficeApi.getTransferenciasEnRevision(),
        backofficeApi.getFraudAlerts(),
        backofficeApi.getReporteConciliacion(new Date().toISOString().split('T')[0]),
      ]);
      setTransferencias(transfers);
      setFraudAlerts(frauds);
      setConciliacion(reporte as Record<string, unknown>);
    } catch (error) {
      console.error('Error cargando datos de backoffice:', error);
    } finally {
      setIsLoading(false);
    }
  };

  if (!isAuthenticated || !isAdmin) return null;

  return (
    <div className="min-h-screen bg-surface-950 flex">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <Header />
        <main className="flex-1 overflow-y-auto p-6">
          <div className="max-w-7xl mx-auto">
            <h1 className="text-2xl font-bold text-surface-100 mb-6">Backoffice Administrativo</h1>

            <div className="mb-8">
              <h3 className="text-lg font-semibold text-surface-100 mb-4">Reporte de Conciliación</h3>
              <ReportsPanel conciliacion={conciliacion ?? undefined} isLoading={isLoading} />
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
              <div>
                <h3 className="text-lg font-semibold text-surface-100 mb-4">Transferencias en Revisión</h3>
                <TransferReviewList
                  transferencias={transferencias}
                  isLoading={isLoading}
                />
              </div>
              <div>
                <h3 className="text-lg font-semibold text-surface-100 mb-4">Alertas de Fraude</h3>
                <FraudAlertList evaluaciones={fraudAlerts} isLoading={isLoading} />
              </div>
            </div>
          </div>
        </main>
        <Footer />
      </div>
    </div>
  );
};
