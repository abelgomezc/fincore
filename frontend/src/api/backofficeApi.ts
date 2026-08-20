import apiClient from './axiosConfig';
import { Usuario, UsuarioBackoffice, Cliente } from '@/types';
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

  getUsuariosSistema: async (): Promise<UsuarioBackoffice[]> => {
    const response = await apiClient.get<UsuarioBackoffice[]>('/api/backoffice/usuarios');
    return response.data;
  },

  getClientes: async (page: number = 0): Promise<Cliente[]> => {
    const response = await apiClient.get<Cliente[]>(`/api/backoffice/clientes?page=${page}`);
    return response.data;
  },

  actualizarUsuarioSistema: async (id: number, data: { nombreCompleto?: string; email?: string; roles?: string }): Promise<UsuarioBackoffice> => {
    const response = await apiClient.put<UsuarioBackoffice>(`/api/backoffice/usuarios/${id}`, data);
    return response.data;
  },

  cambiarEstadoUsuarioSistema: async (id: number, estado: string): Promise<void> => {
    await apiClient.put(`/api/backoffice/usuarios/${id}/estado`, null, { params: { estado } });
  },

  consultarAuditoriaUsuario: async (id: number): Promise<AuditoriaCambioBackoffice[]> => {
    const response = await apiClient.get<AuditoriaCambioBackoffice[]>(`/api/backoffice/auditoria/usuario/${id}`);
    return response.data;
  },
};

export interface AuditoriaCambioBackoffice {
  id: number;
  idUsuarioSistema: number;
  entidad: string;
  idEntidad: string;
  accion: string;
  valoresAnteriores?: string;
  valoresNuevos?: string;
  comentario?: string;
  ipOrigen?: string;
  userAgent?: string;
  dispositivo?: string;
  fechaCambio?: string;
  creadoPor?: string;
  actualizadoPor?: string;
  version: number;
}
