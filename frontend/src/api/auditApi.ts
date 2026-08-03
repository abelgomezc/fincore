import apiClient from './axiosConfig';
import { RegistroAuditoria, AuditTrailFilter } from '@/types/audit';

export const auditApi = {
  getTrail: async (filter: AuditTrailFilter): Promise<RegistroAuditoria[]> => {
    const params = new URLSearchParams();
    if (filter.traceId) params.set('traceId', filter.traceId);
    if (filter.idUsuario) params.set('idUsuario', filter.idUsuario);
    if (filter.servicio) params.set('servicio', filter.servicio);
    if (filter.accion) params.set('accion', filter.accion);
    if (filter.fechaDesde) params.set('fechaDesde', filter.fechaDesde);
    if (filter.fechaHasta) params.set('fechaHasta', filter.fechaHasta);
    if (filter.resultado) params.set('resultado', filter.resultado);

    const response = await apiClient.get<RegistroAuditoria[]>(
      `/api/audit/trail?${params.toString()}`
    );
    return response.data;
  },

  getByTraceId: async (traceId: string): Promise<RegistroAuditoria[]> => {
    const response = await apiClient.get<RegistroAuditoria[]>(
      `/api/audit/trail?traceId=${traceId}`
    );
    return response.data;
  },

  getByTransferencia: async (idTransferencia: string): Promise<RegistroAuditoria> => {
    const response = await apiClient.get<RegistroAuditoria>(
      `/api/audit/transferencia/${idTransferencia}`
    );
    return response.data;
  },
};
