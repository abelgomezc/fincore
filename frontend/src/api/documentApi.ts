import apiClient from './axiosConfig';

export interface PlantillaDocumento {
  id: number;
  tipoDocumento: string;
  nombre: string;
  descripcion?: string;
  contenidoHtml?: string;
  version: number;
  activo: boolean;
  fechaCreacion: string;
  fechaActualizacion: string;
}

export interface DocumentoGenerado {
  id: number;
  entidad: string;
  idEntidad: string;
  tipoDocumento: string;
  estado: string;
  nombreArchivo?: string;
  urlArchivo?: string;
  hashArchivo?: string;
  idPlantilla?: number;
  fechaGeneracion: string;
  fechaFirma?: string;
  fechaVencimiento?: string;
}

export interface FirmaElectronica {
  id: number;
  idDocumento: number;
  idFirmante: string;
  nombreFirmante: string;
  emailFirmante?: string;
  estado: string;
  proveedor?: string;
  idTransaccionProveedor?: string;
  fechaEnvio?: string;
  fechaFirma?: string;
  ipFirmante?: string;
  huellaDigital?: string;
  certificadoSerial?: string;
  motivoRechazo?: string;
}

export const documentApi = {
  crearPlantilla: async (data: { tipoDocumento: string; nombre: string; descripcion?: string; contenidoHtml: string }): Promise<PlantillaDocumento> => {
    const response = await apiClient.post<PlantillaDocumento>('/api/documentos/plantillas', data);
    return response.data;
  },

  listarPlantillas: async (): Promise<PlantillaDocumento[]> => {
    const response = await apiClient.get<PlantillaDocumento[]>('/api/documentos/plantillas');
    return response.data;
  },

  obtenerPlantilla: async (id: number): Promise<PlantillaDocumento> => {
    const response = await apiClient.get<PlantillaDocumento>(`/api/documentos/plantillas/${id}`);
    return response.data;
  },

  generarDocumento: async (data: { idPlantilla: number; tipoDocumento: string; entidad: string; idEntidad: string; nombreArchivo: string; variables?: Record<string, unknown>; creadoPor?: string }): Promise<DocumentoGenerado> => {
    const response = await apiClient.post<DocumentoGenerado>('/api/documentos/generar', data);
    return response.data;
  },

  obtenerDocumento: async (id: number): Promise<DocumentoGenerado> => {
    const response = await apiClient.get<DocumentoGenerado>(`/api/documentos/${id}`);
    return response.data;
  },

  consultarDocumentos: async (entidad: string, idEntidad: string): Promise<DocumentoGenerado[]> => {
    const response = await apiClient.get<DocumentoGenerado[]>(`/api/documentos?entidad=${encodeURIComponent(entidad)}&idEntidad=${encodeURIComponent(idEntidad)}`);
    return response.data;
  },

  enviarAFirmar: async (data: { idDocumento: number; idFirmante: string; nombreFirmante: string; emailFirmante?: string; proveedor?: string; ipFirmante?: string }): Promise<FirmaElectronica> => {
    const response = await apiClient.post<FirmaElectronica>(`/api/documentos/${data.idDocumento}/enviar-firmar`, data);
    return response.data;
  },

  finalizarFirma: async (idFirma: number, estado: string, motivoRechazo?: string, ipFirmante?: string): Promise<FirmaElectronica> => {
    const response = await apiClient.put<FirmaElectronica>(`/api/documentos/firmas/${idFirma}/finalizar`, { estado, motivoRechazo, ipFirmante });
    return response.data;
  },
};
