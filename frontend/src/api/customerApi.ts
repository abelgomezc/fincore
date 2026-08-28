import apiClient from './axiosConfig';

export interface ValidacionIdentidadResponse {
  id: number;
  idCliente: number;
  tipoValidacion: string;
  estado: string;
  proveedor?: string;
  puntajeConfianza?: number;
  detalle?: string;
  fechaValidacion?: string;
  idTransaccion?: string;
}

export interface SesionBiometricaResponse {
  id: number;
  idCliente: number;
  tipoBiometria: string;
  estado: string;
  proveedor?: string;
  puntajeConfianza?: number;
  fechaInicio: string;
  fechaFin?: string;
  idSesionProveedor?: string;
  detalle?: string;
}

export const customerApi = {
  validarIdentidad: async (data: { idCliente: number; tipoValidacion: string; proveedor?: string; detalle?: string }): Promise<ValidacionIdentidadResponse> => {
    const response = await apiClient.post<ValidacionIdentidadResponse>(`/api/clientes/${data.idCliente}/identidad/validar`, {
      tipoValidacion: data.tipoValidacion,
      proveedor: data.proveedor,
      detalle: data.detalle,
    });
    return response.data;
  },

  consultarValidaciones: async (idCliente: number): Promise<ValidacionIdentidadResponse[]> => {
    const response = await apiClient.get<ValidacionIdentidadResponse[]>(`/api/clientes/${idCliente}/identidad/validaciones`);
    return response.data;
  },

  iniciarBiometria: async (data: { idCliente: number; tipoBiometria: string; proveedor?: string; idSesionProveedor?: string }): Promise<SesionBiometricaResponse> => {
    const response = await apiClient.post<SesionBiometricaResponse>(`/api/clientes/${data.idCliente}/biometria/iniciar`, {
      tipoBiometria: data.tipoBiometria,
      proveedor: data.proveedor,
      idSesionProveedor: data.idSesionProveedor,
    });
    return response.data;
  },

  consultarBiometrias: async (idCliente: number): Promise<SesionBiometricaResponse[]> => {
    const response = await apiClient.get<SesionBiometricaResponse[]>(`/api/clientes/${idCliente}/biometria/sesiones`);
    return response.data;
  },

  finalizarBiometria: async (idSesion: number, estado: string, puntajeConfianza?: number, detalle?: string): Promise<SesionBiometricaResponse> => {
    const response = await apiClient.put<SesionBiometricaResponse>(`/api/clientes/biometria/${idSesion}/finalizar`, null, {
      params: { estado, puntajeConfianza, detalle },
    });
    return response.data;
  },
};
