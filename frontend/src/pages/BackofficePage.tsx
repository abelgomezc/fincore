import React, { useEffect, useState } from 'react';
import { Header, Sidebar, Footer } from '@/components/layout';
import { TransferReviewList, FraudAlertList, ReportsPanel } from '@/components/backoffice';
import { useAuthStore } from '@/store/authStore';
import { backofficeApi } from '@/api/backofficeApi';
import { Card, Badge } from '@/components/ui';
import { Transferencia } from '@/types/transfer';
import { EvaluacionFraude } from '@/types/fraud';
import { useNavigate } from 'react-router-dom';
import {
  IconShieldCheck,
  IconAlertTriangle,
  IconReportMoney,
  IconRefresh,
  IconTrendingUp,
  IconTrendingDown,
  IconClock,
} from '@tabler/icons-react';
import { motion } from 'framer-motion';

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

  const fraudAlertCount = fraudAlerts.filter((f) => f.decision === 'RECHAZADO' || f.decision === 'EN_REVISION').length;
  const transferReviewCount = transferencias.length;

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
                <IconShieldCheck className="w-7 h-7 mr-3 text-blue-600" />
                Backoffice Administrativo
              </h1>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 }}
              className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8"
            >
              <Card className="text-center p-4">
                <div className="text-3xl font-bold text-amber-600">{fraudAlertCount}</div>
                <div className="text-sm text-slate-500 mt-1 flex items-center justify-center">
                  <IconAlertTriangle className="w-4 h-4 mr-1" />
                  Alertas de Fraude
                </div>
              </Card>
              <Card className="text-center p-4">
                <div className="text-3xl font-bold text-blue-600">{transferReviewCount}</div>
                <div className="text-sm text-slate-500 mt-1 flex items-center justify-center">
                  <IconClock className="w-4 h-4 mr-1" />
                  Transferencias en Revisión
                </div>
              </Card>
              <Card className="text-center p-4">
                <div className="text-3xl font-bold text-green-600">
                  {conciliacion ? '100%' : '--'}
                </div>
                <div className="text-sm text-slate-500 mt-1 flex items-center justify-center">
                  <IconTrendingUp className="w-4 h-4 mr-1" />
                  Conciliación
                </div>
              </Card>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.2 }}
              className="mb-8"
            >
              <Card title="Reporte de Conciliación" icon={<IconReportMoney className="w-5 h-5 text-blue-600" />}>
                <ReportsPanel conciliacion={conciliacion ?? undefined} isLoading={isLoading} />
              </Card>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.3 }}
              className="grid grid-cols-1 lg:grid-cols-2 gap-6"
            >
              <Card title="Transferencias en Revisión" icon={<IconClock className="w-5 h-5 text-blue-600" />}>
                <TransferReviewList transferencias={transferencias} isLoading={isLoading} />
              </Card>
              <Card title="Alertas de Fraude" icon={<IconAlertTriangle className="w-5 h-5 text-amber-600" />}>
                <FraudAlertList evaluaciones={fraudAlerts} isLoading={isLoading} />
              </Card>
            </motion.div>
          </div>
        </main>
        <Footer />
      </div>
    </div>
  );
};
