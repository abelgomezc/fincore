import { useQuery, useQueryClient } from '@tanstack/react-query';
import { Transferencia } from '@/types/transfer';
import { transferApi } from '@/api/transferApi';

export const useTransfers = (idUsuario: string) => {
  const queryClient = useQueryClient();

  const { data: historial, isLoading, error } = useQuery({
    queryKey: ['transfers', idUsuario],
    queryFn: () => transferApi.getHistorial(idUsuario, 0),
    enabled: !!idUsuario,
    staleTime: 60000,
    refetchOnWindowFocus: false,
  });

  const fetchTransfer = async (id: string): Promise<Transferencia> => {
    return transferApi.getTransferencia(id);
  };

  const invalidate = () => {
    queryClient.invalidateQueries({ queryKey: ['transfers', idUsuario] });
  };

  const prefetchTransfer = (id: string) => {
    queryClient.prefetchQuery({
      queryKey: ['transfer', id],
      queryFn: () => transferApi.getTransferencia(id),
    });
  };

  return {
    historial: historial ?? [],
    isLoading,
    error,
    fetchTransfer,
    invalidate,
    prefetchTransfer,
  };
};
