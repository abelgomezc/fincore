import apiClient from './axiosConfig';

export interface SolicitudPrestamo {
  id: number;
  idCliente: number;
  numeroSolicitud: string;
  tipoPrestamo: string;
  estado: string;
  montoSolicitado: number;
  plazoMeses: number;
  tasaInteresAnual?: number;
  montoAprobado?: number;
  scoreBuro?: number;
  scoreRiesgo?: number;
  evaluacionRiesgo?: string;
  motivoRechazo?: string;
  idDocumentoContrato?: number;
  idDocumentoPagare?: number;
  fechaSolicitud: string;
  fechaAprobacion?: string;
  fechaRechazo?: string;
  fechaDesembolso?: string;
}

export interface EvaluacionRiesgo {
  id: number;
  idSolicitud: number;
  estado: string;
  scoreBuro?: number;
  scoreInterno?: number;
  scoreFinal?: number;
  montoAprobado?: number;
  tasaInteresAprobada?: number;
  plazoAprobadoMeses?: number;
  detalle?: string;
  reglasAplicadas?: string;
  fechaEvaluacion?: string;
  evaluadoPor?: string;
}

export const loanApi = {
  crearSolicitud: async (data: { idCliente: number; tipoPrestamo: string; montoSolicitado: number; plazoMeses: number; tasaInteresAnual?: number; creadoPor?: string }): Promise<SolicitudPrestamo> => {
    const response = await apiClient.post<SolicitudPrestamo>('/api/prestamos/solicitudes', data);
    return response.data;
  },

  listarSolicitudes: async (): Promise<SolicitudPrestamo[]> => {
    const response = await apiClient.get<SolicitudPrestamo[]>('/api/prestamos/solicitudes');
    return response.data;
  },

  obtenerSolicitud: async (id: number): Promise<SolicitudPrestamo> => {
    const response = await apiClient.get<SolicitudPrestamo>(`/api/prestamos/solicitudes/${id}`);
    return response.data;
  },

  iniciarFlujo: async (id: number): Promise<SolicitudPrestamo> => {
    const response = await apiClient.post<SolicitudPrestamo>(`/api/prestamos/solicitudes/${id}/iniciar-flujo`);
    return response.data;
  },

  validarIdentidad: async (id: number): Promise<SolicitudPrestamo> => {
    const response = await apiClient.post<SolicitudPrestamo>(`/api/prestamos/solicitudes/${id}/validar-identidad`);
    return response.data;
  },

  consultarBuro: async (id: number): Promise<SolicitudPrestamo> => {
    const response = await apiClient.post<SolicitudPrestamo>(`/api/prestamos/solicitudes/${id}/consultar-buro`);
    return response.data;
  },

  evaluarRiesgo: async (data: { idSolicitud: number; scoreBuro?: number; scoreInterno?: number; detalle?: string }): Promise<EvaluacionRiesgo> => {
    const response = await apiClient.post<EvaluacionRiesgo>('/api/prestamos/evaluar-riesgo', data);
    return response.data;
  },

  aprobarSolicitud: async (id: number, motivo?: string): Promise<SolicitudPrestamo> => {
    const response = await apiClient.post<SolicitudPrestamo>(`/api/prestamos/solicitudes/${id}/aprobar`, { motivo });
    return response.data;
  },

  rechazarSolicitud: async (id: number, motivo: string): Promise<SolicitudPrestamo> => {
    const response = await apiClient.post<SolicitudPrestamo>(`/api/prestamos/solicitudes/${id}/rechazar`, { motivo });
    return response.data;
  },

  generarContrato: async (id: number): Promise<SolicitudPrestamo> => {
    const response = await apiClient.post<SolicitudPrestamo>(`/api/prestamos/solicitudes/${id}/generar-contrato`);
    return response.data;
  },

  enviarContratoAFirmar: async (id: number): Promise<SolicitudPrestamo> => {
    const response = await apiClient.post<SolicitudPrestamo>(`/api/prestamos/solicitudes/${id}/enviar-contrato`);
    return response.data;
  },

  desembolsar: async (id: number): Promise<SolicitudPrestamo> => {
    const response = await apiClient.post<SolicitudPrestamo>(`/api/prestamos/solicitudes/${id}/desembolsar`);
    return response.data;
  },
};
