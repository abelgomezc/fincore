import React, { useState, useEffect } from 'react';
import { Transferencia } from '@/types/transfer';
import { useTransferDemo } from '@/hooks/useTransferDemo';
import { useAuthStore } from '@/store/authStore';
import { TransferForm } from '@/components/transfer/TransferForm';
import { TransferTimeline } from '@/components/transfer/TransferTimeline';
import { AccountCard } from '@/components/account/AccountCard';
import { Card, Button, Badge } from '@/components/ui';
import { useToast } from '@/hooks/useToast';
import { formatCurrency } from '@/lib/utils';
import { clsx } from '@/lib/utils';
import {
  Send,
  PauseCircle,
  Play,
  Wifi,
  WifiOff,
  Clock,
  Wallet,
  BookOpen,
  RefreshCw,
} from 'lucide-react';
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
  COMPLETADA: 'bg-success-100 text-success-700 border-success-200',
  FALLIDA: 'bg-danger-100 text-danger-700 border-danger-200',
  REVERTIDA: 'bg-warning-100 text-warning-700 border-warning-200',
  PENDIENTE: 'bg-surface-200 text-surface-600 border-surface-300',
};

export const TransferDemoLive: React.FC = () => {
  const [cuentas] = useState<CuentaResumen[]>([
    {
      id: 1,
      numeroCuenta: '0123456789',
      saldoDisponible: 15000.0,
      saldoContable: 15000.0,
      saldoRetenido: 0,
      saldoProyectado: 15000.0,
      moneda: 'USD',
      estado: 'ACTIVA',
    },
    {
      id: 2,
      numeroCuenta: '9876543210',
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
  const { toastSuccess, toastError, toastLoading, toastInfo } = useToast();
  const [lastNotifiedEstado, setLastNotifiedEstado] = useState<string | null>(null);

  useEffect(() => {
    if (!demoTransfer) return;
    const estado = demoTransfer.estado;
    if (estado === lastNotifiedEstado) return;

    setLastNotifiedEstado(estado);

    switch (estado) {
      case 'PENDIENTE':
        toastInfo('Transferencia iniciada', `Trace: ${demoTransfer.traceId}`);
        break;
      case 'VALIDANDO':
        toastLoading('Validando información de la transferencia...');
        break;
      case 'FALLIDA':
        toastError('Transferencia rechazada', demoTransfer.motivoFallo || 'Revisa los datos e intenta nuevamente');
        break;
      case 'REVERTIDA':
        toastError('Transferencia revertida', 'Se ha revertido la operación');
        break;
      case 'COMPLETADA':
        toastSuccess(
          'Transferencia completada',
          `$${demoTransfer.monto.toFixed(2)} ${demoTransfer.moneda} acreditados`
        );
        break;
      default:
        toastInfo('Estado actual', estado);
    }
  }, [demoTransfer, lastNotifiedEstado, toastSuccess, toastError, toastLoading, toastInfo]);

  const handleTransferCreated = () => {
    setShowForm(false);
    startDemo();
  };

  const saldoOrigen = demoTransfer?.estado === 'COMPLETADA'
    ? cuentas.find(c => c.id === 1)!
    : cuentas.find(c => c.id === 1)!;

  return (
    <div className="space-y-6">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="mb-6"
      >
        <h1 className="text-3xl font-bold text-dark-500 mb-2 flex items-center">
          <Clock className="w-7 h-7 mr-3 text-primary-500" />
          Transferencia en Vivo
        </h1>
        <p className="text-surface-500">
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
          <h3 className="text-sm font-medium text-surface-500 mb-2 flex items-center">
            <Wallet className="w-4 h-4 mr-1" />
            Cuenta Origen
          </h3>
          <AccountCard
            saldo={{
              idCuenta: saldoOrigen.id,
              numeroCuenta: saldoOrigen.numeroCuenta,
              saldoContable: saldoOrigen.saldoContable,
              saldoDisponible: saldoOrigen.saldoDisponible,
              saldoRetenido: saldoOrigen.saldoRetenido,
              saldoProyectado: saldoOrigen.saldoProyectado,
              estado: saldoOrigen.estado as any,
              moneda: saldoOrigen.moneda,
              ultimaActualizacion: new Date().toISOString(),
            }}
          />
        </div>

        <div>
          <h3 className="text-sm font-medium text-surface-500 mb-2 flex items-center">
            <Wallet className="w-4 h-4 mr-1" />
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
            icon={showForm ? <PauseCircle className="w-4 h-4" /> : <Send className="w-4 h-4" />}
            onClick={() => setShowForm(!showForm)}
          >
            {showForm ? 'Ocultar Formulario' : 'Nueva Transferencia'}
          </Button>

          <Button
            variant={demoTransfer ? 'danger' : 'success'}
            icon={demoTransfer ? <PauseCircle className="w-4 h-4" /> : <Play className="w-4 h-4" />}
            onClick={demoTransfer ? stopDemo : startDemo}
          >
            {demoTransfer ? 'Detener Demo' : 'Iniciar Demo Automática'}
          </Button>
        </div>

        <div className="lg:col-span-2 flex items-center">
          <Badge variant={isConnected ? 'success' : 'danger'} size="md">
            {isConnected ? (
              <span className="flex items-center">
                <Wifi className="w-4 h-4 mr-1" />
                WebSocket conectado
              </span>
            ) : (
              <span className="flex items-center">
                <WifiOff className="w-4 h-4 mr-1" />
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
            <Card title="Crear Transferencia" icon={<Send className="w-5 h-5 text-primary-500" />}>
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
            <Card
              title="Estado de la Transferencia"
              icon={<Clock className="w-5 h-5 text-primary-500" />}
              footer={
                <div className="flex items-center justify-between text-sm">
                  <span className="text-surface-500">
                    Trace: <code className="font-mono bg-surface-100 px-2 py-1 rounded text-xs">{demoTransfer.traceId}</code>
                  </span>
                </div>
              }
            >
              <div className="mb-4">
                <span className="text-sm text-surface-500">Monto:</span>
                <span className="text-2xl font-bold text-dark-500 ml-2">
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
              />

              {demoTransfer.estado === 'COMPLETADA' && (
                <motion.div
                  initial={{ scale: 0.8, opacity: 0 }}
                  animate={{ scale: 1, opacity: 1 }}
                  transition={{ type: 'spring', damping: 10 }}
                  className="mt-4 p-4 bg-success-50 rounded-lg border border-success-200 text-center"
                >
                  <div className="flex items-center justify-center space-x-2 text-success-700">
                    <div>🎉</div>
                    <span className="font-bold">¡Transferencia completada exitosamente!</span>
                  </div>
                </motion.div>
              )}
            </Card>

            <Card title="Asientos Contables Generados" icon={<BookOpen className="w-5 h-5 text-primary-500" />}>
              <div className="text-center py-8 text-surface-400">
                <BookOpen className="w-12 h-12 mx-auto mb-3 text-surface-300" />
                Los asientos se mostrarán aquí en tiempo real vía WebSocket
              </div>
            </Card>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};
