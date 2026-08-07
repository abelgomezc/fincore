import React, { useEffect, useState } from 'react';
import { Header, Sidebar, Footer } from '@/components/layout';
import { AccountCard } from '@/components/account/AccountCard';
import { BalanceChart } from '@/components/account/BalanceChart';
import { Card, Button } from '@/components/ui';
import CountUp from 'react-countup';
import { useAuthStore } from '@/store/authStore';
import { useAccountStore } from '@/store/accountStore';
import { useNavigate } from 'react-router-dom';
import {
  AreaChart,
  DonutChart,
  ProgressBar,
  Card as TremorCard,
  Text,
  Title,
  Metric,
  Flex,
  Badge as TremorBadge,
} from '@tremor/react';
import {
  IconArrowsRightLeft,
  IconTrendingUp,
  IconCoin,
  IconCreditCard,
  IconWallet,
  IconActivity,
} from '@tabler/icons-react';
import { motion } from 'framer-motion';
import { formatCurrency } from '@/lib/utils';

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
      }
    };
    cargarDatos();
  }, [isAuthenticated, user?.id, fetchCuentas, navigate]);

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

  const chartData = movimientos?.map(m => ({
    fecha: new Date(m.fechaCreacion).toLocaleDateString('es-EC', { day: '2-digit', month: 'short' }),
    saldo: m.saldoNuevo,
  })) || [];

  const tipoMovimientoData = [
    { name: 'Débitos', value: movimientos?.filter(m => m.tipoMovimiento === 'DEBITO').length || 0 },
    { name: 'Créditos', value: movimientos?.filter(m => m.tipoMovimiento === 'CREDITO').length || 0 },
    { name: 'Retenciones', value: movimientos?.filter(m => m.tipoMovimiento === 'RETENCION').length || 0 },
    { name: 'Comisiones', value: movimientos?.filter(m => m.tipoMovimiento === 'COMISION').length || 0 },
  ].filter(d => d.value > 0);

  const dailyLimit = 5000;
  const dailyUsed = movimientos?.reduce((sum, m) => {
    const today = new Date().toISOString().split('T')[0];
    const movDate = new Date(m.fechaCreacion).toISOString().split('T')[0];
    return movDate === today ? sum + m.monto : sum;
  }, 0) || 0;
  const dailyProgress = Math.min((dailyUsed / dailyLimit) * 100, 100);

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
              transition={{ duration: 0.4 }}
              className="flex items-center justify-between mb-6"
            >
              <div>
                <h1 className="text-3xl font-bold text-slate-800">
                  Bienvenido, {user?.nombreCompleto || 'Usuario'}
                </h1>
                <p className="text-sm text-slate-500 mt-1">
                  Panel de control financiero
                </p>
              </div>
              <Button
                variant="primary"
                size="sm"
                icon={<IconArrowsRightLeft className="w-4 h-4" />}
                onClick={() => navigate('/transfers')}
              >
                Nueva Transferencia
              </Button>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: 0.1 }}
              className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8"
            >
              <TremorCard className="rounded-xl">
                <Text>Saldo Disponible</Text>
                <Metric>
                  {saldoActual ? (
                    <CountUp
                      start={0}
                      end={saldoActual.saldoDisponible}
                      duration={1.5}
                      separator=","
                      prefix="$"
                      decimals={2}
                    />
                  ) : (
                    '$0.00'
                  )}
                </Metric>
                <Flex justifyContent="start" className="mt-2">
                  <TremorBadge color="green" icon={IconTrendingUp}>+12.5%</TremorBadge>
                  <Text className="text-slate-500">vs mes anterior</Text>
                </Flex>
              </TremorCard>

              <TremorCard className="rounded-xl">
                <Text>Cuentas Activas</Text>
                <Metric>
                  <CountUp start={0} end={cuentas.length} duration={1} />
                </Metric>
                <Flex justifyContent="start" className="mt-2">
                  <TremorBadge color="blue" icon={IconCreditCard}>
                    {cuentas.filter(c => c.estado === 'ACTIVA').length} activas
                  </TremorBadge>
                </Flex>
              </TremorCard>

              <TremorCard className="rounded-xl">
                <Text>Movimientos (30d)</Text>
                <Metric>
                  <CountUp start={0} end={movimientos?.length || 0} duration={1} />
                </Metric>
                <Flex justifyContent="start" className="mt-2">
                  <TremorBadge color="yellow" icon={IconActivity}>
                    {movimientos?.filter(m => m.tipoMovimiento === 'DEBITO').length || 0} débitos
                  </TremorBadge>
                </Flex>
              </TremorCard>

              <TremorCard className="rounded-xl">
                <Text>Límite Diario</Text>
                <Metric>{formatCurrency(dailyUsed)}</Metric>
                <div className="mt-2">
                  <ProgressBar
                    value={dailyProgress}
                    color={dailyProgress > 80 ? 'red' : dailyProgress > 50 ? 'yellow' : 'blue'}
                    className="mt-2"
                  />
                  <Text className="text-xs text-slate-500 mt-1">
                    {dailyProgress.toFixed(1)}% de {formatCurrency(dailyLimit)}
                  </Text>
                </div>
              </TremorCard>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: 0.2 }}
              className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6"
            >
              <div className="lg:col-span-2">
                <TremorCard className="rounded-xl">
                  <Title>Evolución del Saldo</Title>
                  <Text>Últimos 30 días</Text>
                  <AreaChart
                    className="h-72 mt-4"
                    data={chartData}
                    index="fecha"
                    categories={['saldo']}
                    colors={['blue']}
                    valueFormatter={(value) => formatCurrency(value)}
                    showLegend={false}
                    showGridLines={false}
                    showYAxis={true}
                    showXAxis={true}
                  />
                </TremorCard>
              </div>

              <div>
                <TremorCard className="rounded-xl">
                  <Title>Distribución de Movimientos</Title>
                  <DonutChart
                    className="h-64 mt-4"
                    data={tipoMovimientoData}
                    category="value"
                    index="name"
                    colors={['blue', 'green', 'yellow', 'red']}
                    valueFormatter={(value) => `${value} movimientos`}
                  />
                </TremorCard>
              </div>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: 0.3 }}
            >
              <Card title="Actividad Reciente" icon={<IconActivity className="w-5 h-5 text-blue-600" />}>
                <div className="text-center py-12 text-slate-400">
                  <IconActivity className="w-12 h-12 mx-auto mb-3 text-slate-300" />
                  <p>Cargando movimientos...</p>
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
