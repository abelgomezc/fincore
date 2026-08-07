import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { transferApi } from '@/api/transferApi';
import { useAuthStore } from '@/store/authStore';
import { useAccountStore } from '@/store/accountStore';
import { clsx } from '@/lib/utils';
import { formatCurrency } from '@/lib/utils';
import { Button, Badge } from '@/components/ui';
import { useToast } from '@/hooks/useToast';
import {
  IconSend,
  IconCreditCard,
  IconBuildingBank,
  IconInfoCircle,
  IconAlertCircle,
  IconWallet,
  IconSearch,
} from '@tabler/icons-react';
import { motion, AnimatePresence } from 'framer-motion';

interface TransferFormProps {
  cuentas: Array<{ id: number; numeroCuenta: string; saldoDisponible: number; moneda: string }>;
  onSubmitSuccess?: (id: string, traceId: string) => void;
}

const schema = yup.object().shape({
  cuentaDestino: yup.number().required('Selecciona una cuenta destino'),
  monto: yup
    .number()
    .typeError('El monto debe ser un número')
    .positive('El monto debe ser mayor a 0')
    .required('El monto es obligatorio'),
  concepto: yup.string().required('El concepto es obligatorio'),
});

export const TransferForm: React.FC<TransferFormProps> = ({ cuentas, onSubmitSuccess }) => {
  const { user } = useAuthStore();
  const { toastSuccess, toastError } = useToast();
  const { selectedCuentaId, cuentas: misCuentas } = useAccountStore();
  const cuentasConSaldo = cuentas.filter((c) => c.saldoDisponible > 0);
  const cuentaOrigen = misCuentas.find((c) => c.id === selectedCuentaId) || cuentasConSaldo[0];

  const [busquedaDestino, setBusquedaDestino] = useState('');
  const [cuentaDestinoPreview, setCuentaDestinoPreview] = useState<any>(null);
  const [isSearchingDestino, setIsSearchingDestino] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    watch,
    setValue,
  } = useForm({
    resolver: yupResolver(schema),
  });

  const watchedMonto = watch('monto', 0);

  const buscarCuentaDestino = async (numeroCuenta: string) => {
    if (!numeroCuenta || numeroCuenta.length < 5) {
      setCuentaDestinoPreview(null);
      return;
    }

    setIsSearchingDestino(true);
    try {
      const cuenta = await useAccountStore.getState().getCuentaPorNumero(numeroCuenta);
      if (cuenta) {
        const cliente = await useAccountStore.getState().getCliente(cuenta.idCliente);
        setCuentaDestinoPreview({ ...cuenta, cliente });
        setValue('cuentaDestino', cuenta.id);
      } else {
        setCuentaDestinoPreview(null);
        setValue('cuentaDestino', undefined as any);
      }
    } catch {
      setCuentaDestinoPreview(null);
    } finally {
      setIsSearchingDestino(false);
    }
  };

  const handleBusquedaDestinoKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      buscarCuentaDestino(busquedaDestino);
    }
  };

  useEffect(() => {
    if (cuentaDestinoPreview) {
      setValue('cuentaDestino', cuentaDestinoPreview.id);
    }
  }, [cuentaDestinoPreview, setValue]);

  const onSubmit = async (data: any) => {
    try {
      if (!cuentaOrigen) {
        toastError('Error', 'No hay cuenta origen seleccionada');
        return;
      }

      await transferApi.crearTransferencia({
        idCuentaOrigen: cuentaOrigen.id,
        idCuentaDestino: data.cuentaDestino,
        monto: data.monto,
        moneda: cuentaOrigen.moneda || 'USD',
        concepto: data.concepto,
        idUsuarioOrigen: user?.id || '',
      });

      toastSuccess('Transferencia creada', 'Se ha iniciado el proceso de transferencia');
      setBusquedaDestino('');
      setCuentaDestinoPreview(null);
      onSubmitSuccess?.(cuentaOrigen.id.toString(), 'trace-' + Date.now());
    } catch (error: any) {
      toastError('Error al crear transferencia', error?.message || 'Inténtalo nuevamente');
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">Cuenta Origen</label>
          <div className="relative">
            <div className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500 pointer-events-none">
              <IconWallet className="w-5 h-5" />
            </div>
            <input
              type="text"
              readOnly
              value={cuentaOrigen ? `${cuentaOrigen.numeroCuenta} — ${formatCurrency(cuentaOrigen.saldoDisponible, cuentaOrigen.moneda)}` : ''}
              className={clsx(
                'w-full pl-10 pr-3 py-2.5 bg-slate-100 border rounded-xl text-slate-800',
                'border-slate-200'
              )}
            />
          </div>
          <p className="text-xs text-slate-500 mt-1">
            Usando cuenta seleccionada en el panel
          </p>
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">Cuenta Destino</label>
          <div className="relative">
            <div className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500 pointer-events-none">
              <IconBuildingBank className="w-5 h-5" />
            </div>
            <input
              type="text"
              value={busquedaDestino}
              onChange={(e) => {
                setBusquedaDestino(e.target.value);
                if (!e.target.value) {
                  setCuentaDestinoPreview(null);
                  setValue('cuentaDestino', undefined as any);
                }
              }}
              onKeyDown={handleBusquedaDestinoKeyDown}
              className={clsx(
                'w-full pl-10 pr-10 py-2.5 bg-white border rounded-xl text-slate-800 placeholder-slate-400',
                'focus:border-blue-500 focus:outline-none transition-colors',
                errors.cuentaDestino ? 'border-red-300' : 'border-slate-200'
              )}
              placeholder="Ingrese número de cuenta y presione Enter"
            />
            <button
              type="button"
              onClick={() => buscarCuentaDestino(busquedaDestino)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-blue-600 transition-colors"
            >
              {isSearchingDestino ? (
                <div className="animate-spin rounded-full h-4 w-4 border-2 border-slate-400 border-t-transparent" />
              ) : (
                <IconSearch className="w-4 h-4" />
              )}
            </button>
          </div>
          {errors.cuentaDestino && (
            <p className="text-red-600 text-xs mt-1 flex items-center">
              <IconAlertCircle className="w-3 h-3 mr-1" />
              {errors.cuentaDestino.message?.toString()}
            </p>
          )}

          <AnimatePresence>
            {cuentaDestinoPreview && (
              <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                className="mt-2 p-3 bg-blue-50 rounded-lg border border-blue-200"
              >
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-medium text-slate-800">
                      {cuentaDestinoPreview.cliente?.nombreCompleto || 'Cliente'}
                    </p>
                    <p className="text-xs text-slate-500">
                      {cuentaDestinoPreview.cliente?.email || ''}
                    </p>
                    <p className="text-xs text-slate-500">
                      {cuentaDestinoPreview.tipoCuenta} · {cuentaDestinoPreview.moneda}
                    </p>
                  </div>
                  <Badge variant={cuentaDestinoPreview.estado === 'ACTIVA' ? 'success' : 'error'} size="sm">
                    {cuentaDestinoPreview.estado}
                  </Badge>
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium text-slate-700 mb-1">Monto</label>
        <div className="relative">
          <span className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500">$</span>
          <input
            type="number"
            step="0.01"
            {...register('monto')}
            className={clsx(
              'w-full pl-8 pr-3 py-2.5 bg-white border rounded-xl text-slate-800 placeholder-slate-400',
              'focus:border-blue-500 focus:outline-none transition-colors',
              errors.monto ? 'border-red-300' : 'border-slate-200'
            )}
            placeholder="0.00"
          />
        </div>
        {errors.monto && (
          <p className="text-red-600 text-xs mt-1 flex items-center">
            <IconAlertCircle className="w-3 h-3 mr-1" />
            {errors.monto.message?.toString()}
          </p>
        )}
        {watchedMonto > 0 && (
          <div className="mt-1 flex items-center space-x-2">
            <Badge variant="info" size="sm">
              {formatCurrency(watchedMonto)}
            </Badge>
          </div>
        )}
      </div>

      <div>
        <label className="block text-sm font-medium text-slate-700 mb-1">Concepto</label>
        <div className="relative">
          <div className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500 pointer-events-none">
            <IconInfoCircle className="w-5 h-5" />
          </div>
          <input
            type="text"
            {...register('concepto')}
            className={clsx(
              'w-full pl-10 pr-3 py-2.5 bg-white border rounded-xl text-slate-800 placeholder-slate-400',
              'focus:border-blue-500 focus:outline-none transition-colors',
              errors.concepto ? 'border-red-300' : 'border-slate-200'
            )}
            placeholder="Ej: Pago de factura, transferencia, etc."
          />
        </div>
        {errors.concepto && (
          <p className="text-red-600 text-xs mt-1 flex items-center">
            <IconAlertCircle className="w-3 h-3 mr-1" />
            {errors.concepto.message?.toString()}
          </p>
        )}
      </div>

      <div className="pt-4 border-t border-slate-200">
        <div className="flex items-center justify-between mb-4 text-sm">
          <span className="text-slate-500">Comisión estimada</span>
          <Badge variant="neutral" size="sm">
            $0.00 USD
          </Badge>
        </div>
        <Button
          type="submit"
          disabled={isSubmitting}
          variant="primary"
          className="w-full"
          icon={
            isSubmitting ? (
              <div className="animate-spin rounded-full h-5 w-5 border-2 border-white border-t-transparent"></div>
            ) : (
              <IconSend className="w-5 h-5" />
            )
          }
        >
          {isSubmitting ? 'Procesando...' : 'Enviar Transferencia'}
        </Button>
      </div>
    </form>
  );
};
