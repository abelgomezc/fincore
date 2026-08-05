import React, { useState, useEffect } from 'react';
import { Transferencia } from '@/types/transfer';
import { useTransferDemo } from '@/hooks/useTransferDemo';
import { useAuthStore } from '@/store/authStore';
import { TransferForm } from '@/components/transfer/TransferForm';
import { TransferTimeline } from '@/components/transfer/TransferTimeline';
import { AccountCard } from '@/components/account/AccountCard';
import { Card, Button, Badge } from '@/components/ui';
import { formatCurrency } from '@/lib/utils';
import { clsx } from '@/lib/utils';
import toast, { ToastOptions } from 'react-hot-toast';
import confetti from 'canvas-confetti';
import {
  IconSend,
  IconPlayerPause,
  IconPlayerPlay,
  IconWifi,
  IconWifiOff,
  IconClock,
  IconWallet,
  IconBook,
  IconRefresh,
  IconBuildingBank,
  IconCreditCard,
} from '@tabler/icons-react';
import { motion, AnimatePresence } from 'framer-motion';

interface CuentaResumen {
  id: number;
  numeroCuenta: string;
  saldoDisponible: number;
  saldoContable: number;
  saldoRetenido: number;
  saldoProyectado: number;
  moneda: string;
  estado: string;
}

const estadoBadgeConfig: Record<string, string> = {
  COMPLETADA: 'bg-green-100 text-green-800 border border-green-200',
  FALLIDA: 'bg-red-100 text-red-800 border border-red-200',
  REVERTIDA: 'bg-orange-100 text-orange-800 border border-orange-200',
  PENDIENTE: 'bg-slate-200 text-slate-700 border border-slate-300',
  EN_REVISION: 'bg-amber-100 text-amber-800 border border-amber-200 animate-pulse',
  VALIDANDO: 'bg-blue-100 text-blue-800 border border-blue-200',
};

const fireConfetti = () => {
  const count = 200;
  const defaults = { origin: { y: 0.7 } };
  const colors = ['#1B4F8A', '#F39C12', '#2ECC71', '#E74C3C'];

  confetti({
    ...defaults,
    particleCount: count,
    spread: 70,
    colors,
    startVelocity: 60,
  });
};

export const TransferDemoLive: React.FC = () => {
  const [cuentas] = useState<CuentaResumen[]>([
    {
      id: 1,
      numeroCuenta: '2026 0001 0001',
      saldoDisponible: 15000.0,
      saldoContable: 15000.0,
      saldoRetenido: 0,
      saldoProyectado: 15000.0,
      moneda: 'USD',
      estado: 'ACTIVA',
    },
    {
      id: 2,
      numeroCuenta: '2026 0002 0002',
      saldoDisponible: 8500.0,
      saldoContable: 8500.0,
      saldoRetenido: 0,
      saldoProyectado: 8500.0,
      moneda: 'USD',
      estado: 'ACTIVA',
    },
  ]);

  const [showForm, setShowForm] = useState(false);
  const { user } = useAuthStore();
  const { demoTransfer, startDemo, stopDemo, isConnected } = useTransferDemo(true);
  const [lastNotifiedEstado, setLastNotifiedEstado] = useState<string | null>(null);

  useEffect(() => {
    if (!demoTransfer) return;
    const estado = demoTransfer.estado;
    if (estado === lastNotifiedEstado) return;
    setLastNotifiedEstado(estado);

    switch (estado) {
      case 'PENDIENTE':
        toast('Transferencia iniciada', { icon: '⏳', duration: 3000 });
        break;
      case 'VALIDANDO':
        toast.loading('Validando información...');
        break;
      case 'FALLIDA':
        toast.error('Transferencia rechazada por el sistema antifraude');
        break;
      case 'REVERTIDA':
        toast.error('Transferencia revertida');
        break;
      case 'COMPLETADA':
        fireConfetti();
        toast.success('Transferencia completada', {
          duration: 4000,
          icon: '✅',
        });
        break;
        default:
          toast(`Estado: ${estado}`);
    }
  }, [demoTransfer, lastNotifiedEstado]);

  const handleTransferCreated = () => {
    setShowForm(false);
    startDemo();
  };

  return (
    <div className="space-y-6">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="mb-6"
      >
        <h1 className="text-3xl font-bold text-slate-800 mb-2 flex items-center">
          <IconClock className="w-7 h-7 mr-3 text-blue-600" />
          Transferencia en Vivo
        </h1>
        <p className="text-slate-500">
          Demostración en tiempo real del Saga Pattern orquestado (12 pasos)
        </p>
      </motion.div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.1 }}
        className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6"
      >
        <div>
          <h3 className="text-sm font-medium text-slate-500 mb-2 flex items-center">
            <IconWallet className="w-4 h-4 mr-1" />
            Cuenta Origen
          </h3>
          <AccountCard
            saldo={{
              idCuenta: cuentas[0].id,
              numeroCuenta: cuentas[0].numeroCuenta,
              saldoContable: cuentas[0].saldoContable,
              saldoDisponible: cuentas[0].saldoDisponible,
              saldoRetenido: cuentas[0].saldoRetenido,
              saldoProyectado: cuentas[0].saldoProyectado,
              estado: cuentas[0].estado as any,
              moneda: cuentas[0].moneda,
              ultimaActualizacion: new Date().toISOString(),
            }}
          />
        </div>

        <div>
          <h3 className="text-sm font-medium text-slate-500 mb-2 flex items-center">
            <IconWallet className="w-4 h-4 mr-1" />
            Cuenta Destino
          </h3>
          <AccountCard
            saldo={{
              idCuenta: cuentas[1].id,
              numeroCuenta: cuentas[1].numeroCuenta,
              saldoContable: cuentas[1].saldoContable,
              saldoDisponible: cuentas[1].saldoDisponible,
              saldoRetenido: cuentas[1].saldoRetenido,
              saldoProyectado: cuentas[1].saldoProyectado,
              estado: cuentas[1].estado as any,
              moneda: cuentas[1].moneda,
              ultimaActualizacion: new Date().toISOString(),
            }}
          />
        </div>
      </motion.div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
        className="grid grid-cols-1 lg:grid-cols-3 gap-6"
      >
        <div className="lg:col-span-1 flex flex-col space-y-3">
          <Button
            variant={showForm ? 'secondary' : 'primary'}
            icon={showForm ? <IconPlayerPause className="w-4 h-4" /> : <IconSend className="w-4 h-4" />}
            onClick={() => setShowForm(!showForm)}
          >
            {showForm ? 'Ocultar Formulario' : 'Nueva Transferencia'}
          </Button>

          <Button
            variant={demoTransfer ? 'danger' : 'success'}
            icon={demoTransfer ? <IconPlayerPause className="w-4 h-4" /> : <IconPlayerPlay className="w-4 h-4" />}
            onClick={demoTransfer ? stopDemo : startDemo}
          >
            {demoTransfer ? 'Detener Demo' : 'Iniciar Demo Automática'}
          </Button>
        </div>

        <div className="lg:col-span-2 flex items-center">
          <Badge variant={isConnected ? 'success' : 'danger'} size="md">
            {isConnected ? (
              <span className="flex items-center">
                <IconWifi className="w-4 h-4 mr-1" />
                WebSocket conectado
              </span>
            ) : (
              <span className="flex items-center">
                <IconWifiOff className="w-4 h-4 mr-1" />
                WebSocket desconectado
              </span>
            )}
          </Badge>
        </div>
      </motion.div>

      <AnimatePresence>
        {showForm && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            transition={{ duration: 0.3 }}
          >
            <Card title="Crear Transferencia" icon={<IconSend className="w-5 h-5 text-blue-600" />}>
              <TransferForm
                cuentas={cuentas}
                onSubmitSuccess={handleTransferCreated}
              />
            </Card>
          </motion.div>
        )}
      </AnimatePresence>

      <AnimatePresence>
        {demoTransfer && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            transition={{ duration: 0.4 }}
            className="space-y-6"
          >
            <motion.div
              animate={
                demoTransfer.estado === 'COMPLETADA'
                  ? { scale: [1, 1.02, 1], transition: { duration: 2 } }
                  : demoTransfer.estado === 'FALLIDA'
                  ? { x: [0, -5, 5, -5, 5, 0], transition: { duration: 0.5 } }
                  : {}
              }
            >
              <Card
                title="Estado de la Transferencia"
                icon={<IconClock className="w-5 h-5 text-blue-600" />}
                footer={
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-slate-500">
                      Trace: <code className="font-mono bg-slate-100 px-2 py-1 rounded text-xs">{demoTransfer.traceId}</code>
                    </span>
                  </div>
                }
              >
                <div className="mb-4">
                  <span className="text-sm text-slate-500">Monto:</span>
                  <span className="text-2xl font-bold text-slate-800 ml-2">
                    {formatCurrency(demoTransfer.monto, demoTransfer.moneda)}
                  </span>
                </div>

                <div className="mb-4">
                  <Badge className={clsx(estadoBadgeConfig[demoTransfer.estado] || estadoBadgeConfig.PENDIENTE)}>
                    {demoTransfer.estado}
                  </Badge>
                </div>

                <TransferTimeline
                  estados={demoTransfer.estadoDetalle}
                  currentEstado={demoTransfer.estado}
                  onComplete={() => toast.success('Transferencia completada', { duration: 4000, icon: '✅' })}
                  onReject={() => toast.error('Transferencia rechazada por el sistema antifraude')}
                />
              </Card>
            </motion.div>

            <Card title="Asientos Contables Generados" icon={<IconBook className="w-5 h-5 text-blue-600" />}>
              <div className="text-center py-8 text-slate-400">
                <IconBook className="w-12 h-12 mx-auto mb-3 text-slate-300" />
                Los asientos se mostrarán aquí en tiempo real vía WebSocket
              </div>
            </Card>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};
