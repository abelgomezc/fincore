import apiClient from './axiosConfig';
import { AuthResponse, LoginRequest, RegisterRequest, UsuarioBackoffice } from '@/types';

export const authApi = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>('/api/auth/login', credentials);
    return response.data;
  },

  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>('/api/auth/register', data);
    return response.data;
  },

  refresh: async (refreshToken: string): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>('/api/auth/refresh', { refreshToken });
    return response.data;
  },

  logout: async (): Promise<void> => {
    await apiClient.post('/api/auth/logout');
  },

  getUsuarioActual: async () => {
    const response = await apiClient.get('/api/auth/me');
    return response.data;
  },

  listarUsuarios: async (): Promise<UsuarioBackoffice[]> => {
    const response = await apiClient.get<UsuarioBackoffice[]>('/api/auth/usuarios');
    return response.data;
  },

  suspenderUsuario: async (id: number, motivo: string): Promise<void> => {
    await apiClient.put(`/api/auth/usuario/${id}/suspender`, null, { params: { motivo } });
  },

  reactivarUsuario: async (id: number): Promise<void> => {
    await apiClient.put(`/api/auth/usuario/${id}/reactivar`);
  },

  eliminarUsuario: async (id: number): Promise<void> => {
    await apiClient.put(`/api/auth/usuario/${id}/eliminar`);
  },
};

export const clienteApi = {
  suspenderCliente: async (id: number, motivo: string): Promise<void> => {
    await apiClient.put(`/api/clientes/${id}/suspender`, null, { params: { motivo } });
  },

  reactivarCliente: async (id: number): Promise<void> => {
    await apiClient.put(`/api/clientes/${id}/reactivar`);
  },

  eliminarCliente: async (id: number): Promise<void> => {
    await apiClient.delete(`/api/clientes/${id}`);
  },
};
