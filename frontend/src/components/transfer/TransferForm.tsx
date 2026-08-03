import React from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { transferApi } from '@/api/transferApi';
import { useAuthStore } from '@/store/authStore';
import { clsx } from '@/lib/utils';
import { formatCurrency } from '@/lib/utils';

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
  const cuentasConSaldo = cuentas.filter((c) => c.saldoDisponible > 0);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    watch,
  } = useForm({
    resolver: yupResolver(schema),
  });

  const monto = watch('monto', 0);

  const onSubmit = async (data: any) => {
    try {
      const cuentaOrigen = cuentasConSaldo.find((c) => c.id === data.cuentaOrigen);
      const cuentaDestino = cuentas.find((c) => c.id === data.cuentaDestino);

      const result = await transferApi.crearTransferencia({
        idCuentaOrigen: data.cuentaOrigen,
        idCuentaDestino: data.cuentaDestino,
        monto: data.monto,
        moneda: cuentaOrigen?.moneda || 'USD',
        concepto: data.concepto,
        idUsuarioOrigen: user?.id || '',
      });

      onSubmitSuccess?.(result.id, result.traceId);
    } catch (error: any) {
      console.error('Error creando transferencia:', error);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-surface-300 mb-1">Cuenta Origen</label>
          <select
            {...register('cuentaOrigen')}
            className={clsx(
              'w-full px-3 py-2 bg-surface-800 border rounded-lg text-surface-100',
              errors.cuentaOrigen ? 'border-banking-error' : 'border-surface-600'
            )}
          >
            <option value="">Seleccionar</option>
            {cuentasConSaldo.map((c) => (
              <option key={c.id} value={c.id}>
                {c.numeroCuenta} — {formatCurrency(c.saldoDisponible, c.moneda)}
              </option>
            ))}
          </select>
          {errors.cuentaOrigen && (
            <p className="text-banking-error text-xs mt-1">{errors.cuentaOrigen.message?.toString()}</p>
          )}
        </div>

        <div>
          <label className="block text-sm font-medium text-surface-300 mb-1">Cuenta Destino</label>
          <select
            {...register('cuentaDestino')}
            className={clsx(
              'w-full px-3 py-2 bg-surface-800 border rounded-lg text-surface-100',
              errors.cuentaDestino ? 'border-banking-error' : 'border-surface-600'
            )}
          >
            <option value="">Seleccionar</option>
            {cuentas.map((c) => (
              <option key={c.id} value={c.id}>
                {c.numeroCuenta}
              </option>
            ))}
          </select>
          {errors.cuentaDestino && (
            <p className="text-banking-error text-xs mt-1">{errors.cuentaDestino.message?.toString()}</p>
          )}
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium text-surface-300 mb-1">Monto</label>
        <div className="relative">
          <span className="absolute left-3 top-1/2 -translate-y-1/2 text-surface-500">$</span>
          <input
            type="number"
            step="0.01"
            {...register('monto')}
            className={clsx(
              'w-full pl-8 pr-3 py-2 bg-surface-800 border rounded-lg text-surface-100',
              errors.monto ? 'border-banking-error' : 'border-surface-600'
            )}
            placeholder="0.00"
          />
        </div>
        {errors.monto && (
          <p className="text-banking-error text-xs mt-1">{errors.monto.message?.toString()}</p>
        )}
        {monto > 0 && (
          <p className="text-xs text-surface-500 mt-1">
            {formatCurrency(monto)} USD
          </p>
        )}
      </div>

      <div>
        <label className="block text-sm font-medium text-surface-300 mb-1">Concepto</label>
        <input
          type="text"
          {...register('concepto')}
          className={clsx(
            'w-full px-3 py-2 bg-surface-800 border rounded-lg text-surface-100',
            errors.concepto ? 'border-banking-error' : 'border-surface-600'
          )}
          placeholder="Pago, transferencia, etc."
        />
        {errors.concepto && (
          <p className="text-banking-error text-xs mt-1">{errors.concepto.message?.toString()}</p>
        )}
      </div>

      <button
        type="submit"
        disabled={isSubmitting}
        className="w-full py-3 bg-primary-600 hover:bg-primary-700 disabled:opacity-50 rounded-lg font-medium text-white transition-colors flex items-center justify-center space-x-2"
      >
        {isSubmitting ? (
          <>
            <svg className="animate-spin h-5 w-5" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
            </svg>
            <span>Procesando...</span>
          </>
        ) : (
          <span>Enviar Transferencia</span>
        )}
      </button>
    </form>
  );
};
