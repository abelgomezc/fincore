import React from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { transferApi } from '@/api/transferApi';
import { useAuthStore } from '@/store/authStore';
import { clsx } from '@/lib/utils';
import { formatCurrency } from '@/lib/utils';
import { Button, Badge } from '@/components/ui';
import { useToast } from '@/hooks/useToast';
import {
  Send,
  CreditCard,
  Building,
  Info,
  AlertCircle,
  Wallet,
} from 'lucide-react';

interface TransferFormProps {
  cuentas: Array<{ id: number; numeroCuenta: string; saldoDisponible: number; moneda: string }>;
  onSubmitSuccess?: (id: string, traceId: string) => void;
}

const schema = yup.object().shape({
  cuentaOrigen: yup.number().required('Selecciona una cuenta origen'),
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
  const cuentasConSaldo = cuentas.filter((c) => c.saldoDisponible > 0);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    watch,
  } = useForm({
    resolver: yupResolver(schema),
  });

  const watchedMonto = watch('monto', 0);

  const onSubmit = async (data: any) => {
    try {
      const cuentaOrigen = cuentasConSaldo.find((c) => c.id === data.cuentaOrigen);
      await transferApi.crearTransferencia({
        idCuentaOrigen: data.cuentaOrigen,
        idCuentaDestino: data.cuentaDestino,
        monto: data.monto,
        moneda: cuentaOrigen?.moneda || 'USD',
        concepto: data.concepto,
        idUsuarioOrigen: user?.id || '',
      });

      toastSuccess('Transferencia creada', 'Se ha iniciado el proceso de transferencia');
      onSubmitSuccess?.(data.cuentaOrigen.toString(), 'trace-' + Date.now());
    } catch (error: any) {
      toastError('Error al crear transferencia', error?.message || 'Inténtalo nuevamente');
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label className="block text-sm font-medium text-dark-500 mb-2">Cuenta Origen</label>
          <div className="relative">
            <div className="absolute left-3 top-1/2 -translate-y-1/2 text-surface-500 pointer-events-none">
              <Wallet className="w-5 h-5" />
            </div>
            <select
              {...register('cuentaOrigen')}
              className={clsx(
                'w-full pl-10 pr-3 py-2.5 bg-card-50 border rounded-lg text-dark-500',
                'focus:border-primary-500 focus:outline-none transition-colors',
                errors.cuentaOrigen ? 'border-danger-300' : 'border-surface-300'
              )}
            >
              <option value="">Seleccionar cuenta</option>
              {cuentasConSaldo.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.numeroCuenta} — {formatCurrency(c.saldoDisponible, c.moneda)}
                </option>
              ))}
            </select>
          </div>
          {errors.cuentaOrigen && (
            <p className="text-danger-600 text-xs mt-1 flex items-center">
              <AlertCircle className="w-3 h-3 mr-1" />
              {errors.cuentaOrigen.message?.toString()}
            </p>
          )}
        </div>

        <div>
          <label className="block text-sm font-medium text-dark-500 mb-2">Cuenta Destino</label>
          <div className="relative">
            <div className="absolute left-3 top-1/2 -translate-y-1/2 text-surface-500 pointer-events-none">
              <Building className="w-5 h-5" />
            </div>
            <select
              {...register('cuentaDestino')}
              className={clsx(
                'w-full pl-10 pr-3 py-2.5 bg-card-50 border rounded-lg text-dark-500',
                'focus:border-primary-500 focus:outline-none transition-colors',
                errors.cuentaDestino ? 'border-danger-300' : 'border-surface-300'
              )}
            >
              <option value="">Seleccionar cuenta</option>
              {cuentas.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.numeroCuenta}
                </option>
              ))}
            </select>
          </div>
          {errors.cuentaDestino && (
            <p className="text-danger-600 text-xs mt-1 flex items-center">
              <AlertCircle className="w-3 h-3 mr-1" />
              {errors.cuentaDestino.message?.toString()}
            </p>
          )}
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium text-dark-500 mb-2">Monto</label>
        <div className="relative">
          <span className="absolute left-3 top-1/2 -translate-y-1/2 text-surface-500">$</span>
          <input
            type="number"
            step="0.01"
            {...register('monto')}
            className={clsx(
              'w-full pl-8 pr-3 py-2.5 bg-card-50 border rounded-lg text-dark-500 placeholder-surface-400',
              'focus:border-primary-500 focus:outline-none transition-colors',
              errors.monto ? 'border-danger-300' : 'border-surface-300'
            )}
            placeholder="0.00"
          />
        </div>
        {errors.monto && (
          <p className="text-danger-600 text-xs mt-1 flex items-center">
            <AlertCircle className="w-3 h-3 mr-1" />
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
        <label className="block text-sm font-medium text-dark-500 mb-2">Concepto</label>
        <div className="relative">
          <div className="absolute left-3 top-1/2 -translate-y-1/2 text-surface-500 pointer-events-none">
            <Info className="w-5 h-5" />
          </div>
          <input
            type="text"
            {...register('concepto')}
            className={clsx(
              'w-full pl-10 pr-3 py-2.5 bg-card-50 border rounded-lg text-dark-500 placeholder-surface-400',
              'focus:border-primary-500 focus:outline-none transition-colors',
              errors.concepto ? 'border-danger-300' : 'border-surface-300'
            )}
            placeholder="Ej: Pago de factura, transferencia, etc."
          />
        </div>
        {errors.concepto && (
          <p className="text-danger-600 text-xs mt-1 flex items-center">
            <AlertCircle className="w-3 h-3 mr-1" />
            {errors.concepto.message?.toString()}
          </p>
        )}
      </div>

      <div className="pt-4 border-t border-surface-200">
        <div className="flex items-center justify-between mb-4 text-sm">
          <span className="text-surface-500">Comisión estimada</span>
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
              <Send className="w-5 h-5" />
            )
          }
        >
          {isSubmitting ? 'Procesando...' : 'Enviar Transferencia'}
        </Button>
      </div>
    </form>
  );
};
