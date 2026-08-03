import React, { useState } from 'react';
import { Transferencia } from '@/types/transfer';
import { useTransferDemo } from '@/hooks/useTransferDemo';
import { useAuthStore } from '@/store/authStore';
import { TransferForm } from '@/components/transfer/TransferForm';
import { TransferTimeline } from '@/components/transfer/TransferTimeline';
import { AccountCard } from '@/components/account/AccountCard';
import { formatCurrency } from '@/lib/utils';
import { clsx } from '@/lib/utils';

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

export const TransferDemoLive: React.FC = () => {
  const [cuentas, setCuentas] = useState<CuentaResumen[]>([
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

  const handleTransferCreated = () => {
    setShowForm(false);
    startDemo();
  };

  const saldoOrigen = demoTransfer?.estado === 'COMPLETADA'
    ? cuentas.find(c => c.id === 1)!
    : cuentas.find(c => c.id === 1)!;

  return (
    <div className="p-6 max-w-7xl mx-auto">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-surface-100 mb-2">Transferencia en Vivo</h1>
        <p className="text-surface-400">
          Demostración en tiempo real del Saga Pattern orquestado (12 pasos)
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        <div>
          <h3 className="text-sm font-medium text-surface-400 mb-2">Cuenta Origen</h3>
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
          <h3 className="text-sm font-medium text-surface-400 mb-2">Cuenta Destino</h3>
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
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
        <div className="lg:col-span-1">
          <button
            onClick={() => setShowForm(!showForm)}
            className={clsx(
              'w-full py-3 px-4 rounded-lg font-medium transition-colors',
              showForm
                ? 'bg-surface-700 text-surface-200'
                : 'bg-primary-600 hover:bg-primary-700 text-white'
            )}
          >
            {showForm ? 'Ocultar Formulario' : 'Nueva Transferencia'}
          </button>

          <button
            onClick={demoTransfer ? stopDemo : startDemo}
            className={clsx(
              'w-full py-3 px-4 rounded-lg font-medium mt-2 transition-colors',
              demoTransfer
                ? 'bg-banking-error hover:bg-banking-error/80 text-white'
                : 'bg-banking-success hover:bg-banking-success/80 text-white'
            )}
          >
            {demoTransfer ? 'Detener Demo' : 'Iniciar Demo Automática'}
          </button>
        </div>

        <div className="lg:col-span-2">
          <div className={clsx(
            'inline-flex items-center space-x-2 px-3 py-1 rounded-full text-sm',
            isConnected ? 'bg-banking-success/20 text-banking-success' : 'bg-banking-error/20 text-banking-error'
          )}>
            <span className="w-2 h-2 rounded-full animate-pulse"></span>
            <span>{isConnected ? 'WebSocket conectado' : 'WebSocket desconectado'}</span>
          </div>
        </div>
      </div>

      {showForm && (
        <div className="mb-6 bg-surface-800 rounded-xl p-6 border border-surface-700">
          <h3 className="text-lg font-semibold text-surface-100 mb-4">Crear Transferencia</h3>
          <TransferForm
            cuentas={cuentas}
            onSubmitSuccess={handleTransferCreated}
          />
        </div>
      )}

      {demoTransfer && (
        <div className="mb-6">
          <div className="bg-surface-800 rounded-xl p-6 border border-surface-700 mb-4">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-surface-100">Estado de la Transferencia</h3>
              <div className="flex items-center space-x-4 text-sm">
                <span className={clsx(
                  'px-3 py-1 rounded-full font-medium',
                  demoTransfer.estado === 'COMPLETADA' ? 'bg-banking-success/20 text-banking-success' :
                  demoTransfer.estado === 'FALLIDA' || demoTransfer.estado === 'REVERTIDA' ? 'bg-banking-error/20 text-banking-error' :
                  'bg-banking-warning/20 text-banking-warning'
                )}>
                  {demoTransfer.estado}
                </span>
                <span className="text-surface-400">
                  Trace: {demoTransfer.traceId}
                </span>
              </div>
            </div>

            <div className="mb-4">
              <span className="text-sm text-surface-400">Monto:</span>
              <span className="text-xl font-bold text-surface-100 ml-2">
                {formatCurrency(demoTransfer.monto, demoTransfer.moneda)}
              </span>
            </div>

            <TransferTimeline
              estados={demoTransfer.estadoDetalle}
              currentEstado={demoTransfer.estado}
            />
          </div>

          <div className="bg-surface-800 rounded-xl p-6 border border-surface-700">
            <h3 className="text-lg font-semibold text-surface-100 mb-4">Asientos Contables Generados</h3>
            <div className="text-center py-8 text-surface-500">
              Los asientos se mostrarán aquí en tiempo real vía WebSocket
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
