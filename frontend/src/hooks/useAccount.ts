import { useEffect } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { Cuenta } from '@/types/account';
import { accountApi } from '@/api/accountApi';
import { useAccountStore } from '@/store/accountStore';

export const useAccount = (idUsuario: string) => {
  const queryClient = useQueryClient();
  const { selectedCuentaId, setSelectedCuenta, clear } = useAccountStore();

  const { data: cuentas, isLoading, error } = useQuery({
    queryKey: ['cuentas', idUsuario],
    queryFn: () => accountApi.getCuentas(idUsuario),
    enabled: !!idUsuario,
    staleTime: 30000,
    refetchOnWindowFocus: true,
  });

  const selectedCuenta = cuentas?.find((c) => c.id === selectedCuentaId) ?? cuentas?.[0];

  useEffect(() => {
    const firstCuenta = cuentas?.[0];
    if (firstCuenta && !selectedCuentaId) {
      setSelectedCuenta(firstCuenta.id);
    }
  }, [cuentas, selectedCuentaId, setSelectedCuenta]);

  const fetchCuenta = async (idCuenta: number): Promise<Cuenta> => {
    return accountApi.getCuenta(idCuenta);
  };

  const invalidate = () => {
    queryClient.invalidateQueries({ queryKey: ['cuentas', idUsuario] });
    if (selectedCuentaId) {
      queryClient.invalidateQueries({ queryKey: ['saldo', selectedCuentaId] });
      queryClient.invalidateQueries({ queryKey: ['movimientos', selectedCuentaId] });
    }
  };

  return {
    cuentas: cuentas ?? [],
    selectedCuenta,
    selectedCuentaId,
    isLoading,
    error,
    setSelectedCuenta,
    fetchCuenta,
    clear,
    invalidate,
  };
};
