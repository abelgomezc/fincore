import apiClient from './axiosConfig';
import { Transferencia, CrearTransferenciaRequest } from '@/types/transfer';

export const transferApi = {
  crearTransferencia: async (
    request: Omit<CrearTransferenciaRequest, 'id'>
  ): Promise<{ id: string; traceId: string }> => {
    const response = await apiClient.post('/api/transferencias', request);
    return response.data;
  },

  getTransferencia: async (id: string): Promise<Transferencia> => {
    const response = await apiClient.get<Transferencia>(`/api/transferencias/${id}`);
    return response.data;
  },

  getTransferenciaByTraceId: async (traceId: string): Promise<Transferencia> => {
    const response = await apiClient.get<Transferencia>(`/api/transferencias?traceId=${traceId}`);
    return response.data;
  },

  getHistorial: async (
    idUsuario: string,
    page: number = 0,
    size: number = 20
  ): Promise<Transferencia[]> => {
    const response = await apiClient.get<Transferencia[]>(
      `/api/transferencias?userId=${idUsuario}&page=${page}&size=${size}`
    );
    return response.data;
  },

  cancelarTransferencia: async (id: string): Promise<void> => {
    await apiClient.post(`/api/transferencias/${id}/cancelar`);
  },
};
