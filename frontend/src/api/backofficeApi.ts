import apiClient from './axiosConfig';
import { Usuario, Cliente } from '@/types';
import { Transferencia } from '@/types/transfer';
import { EvaluacionFraude } from '@/types/fraud';

export const backofficeApi = {
  getTransferenciasEnRevision: async (page: number = 0): Promise<Transferencia[]> => {
    const response = await apiClient.get<Transferencia[]>(
      `/api/backoffice/transferencias/en-revision?page=${page}`
    );
    return response.data;
  },

  getFraudAlerts: async (fechaDesde?: string): Promise<EvaluacionFraude[]> => {
    const url = fechaDesde
      ? `/api/backoffice/fraude/alertas?fechaDesde=${fechaDesde}`
      : '/api/backoffice/fraude/alertas';
    const response = await apiClient.get<EvaluacionFraude[]>(url);
    return response.data;
  },

  getReporteConciliacion: async (fecha: string): Promise<unknown> => {
    const response = await apiClient.get(`/api/backoffice/reportes/conciliacion?fecha=${fecha}`);
    return response.data;
  },

  getReporteFraude: async (fechaDesde: string, fechaHasta: string): Promise<unknown> => {
    const response = await apiClient.get(
      `/api/backoffice/reportes/fraude?fechaDesde=${fechaDesde}&fechaHasta=${fechaHasta}`
    );
    return response.data;
  },

  getUsuariosSistema: async (): Promise<Usuario[]> => {
    const response = await apiClient.get<Usuario[]>('/api/backoffice/usuarios');
    return response.data;
  },

  getClientes: async (page: number = 0): Promise<Cliente[]> => {
    const response = await apiClient.get<Cliente[]>(`/api/backoffice/clientes?page=${page}`);
    return response.data;
  },
};
