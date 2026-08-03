import React, { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { useAuthStore } from '@/store/authStore';
import { useNavigate } from 'react-router-dom';
import { Copyright } from '@/components/ui/Copyright';
import { clsx } from '@/lib/utils';

const schema = yup.object().shape({
  username: yup.string().required('El usuario es obligatorio'),
  password: yup.string().required('La contraseña es obligatoria'),
});

export const LoginPage: React.FC = () => {
  const { login, error, isLoading } = useAuthStore();
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(schema),
  });

  const onSubmit = async (data: { username: string; password: string }) => {
    await login(data.username, data.password);
    const { isAuthenticated } = useAuthStore.getState();
    if (isAuthenticated) {
      navigate('/');
    }
  };

  return (
    <div className="min-h-screen bg-surface-900 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-primary-400 mb-2">FinCore Banking</h1>
          <p className="text-surface-400">Accede a tu sistema financiero</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-surface-300 mb-1">Usuario</label>
            <input
              type="text"
              {...register('username')}
              className={clsx(
                'w-full px-3 py-2 bg-surface-800 border rounded-lg text-surface-100',
                errors.username ? 'border-banking-error' : 'border-surface-600'
              )}
              placeholder="Nombre de usuario"
            />
            {errors.username && (
              <p className="text-banking-error text-xs mt-1">{errors.username.message}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-surface-300 mb-1">Contraseña</label>
            <input
              type="password"
              {...register('password')}
              className={clsx(
                'w-full px-3 py-2 bg-surface-800 border rounded-lg text-surface-100',
                errors.password ? 'border-banking-error' : 'border-surface-600'
              )}
              placeholder="••••••••"
            />
            {errors.password && (
              <p className="text-banking-error text-xs mt-1">{errors.password.message}</p>
            )}
          </div>

          {error && (
            <div className="bg-banking-error/10 border border-banking-error/30 text-banking-error px-3 py-2 rounded-lg text-sm">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={isLoading}
            className="w-full py-3 bg-primary-600 hover:bg-primary-700 disabled:opacity-50 rounded-lg font-medium text-white transition-colors"
          >
            {isLoading ? 'Cargando...' : 'Iniciar Sesión'}
          </button>
        </form>

        <div className="mt-6 text-center">
          <button
            onClick={() => alert('Contacta al administrador para crear una cuenta')}
            className="text-sm text-surface-400 hover:text-surface-200"
          >
            ¿No tienes una cuenta? Contacta al administrador
          </button>
        </div>

        <Copyright />
      </div>
    </div>
  );
};
