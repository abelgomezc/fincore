import apiClient from './axiosConfig';
import { AuthResponse, LoginRequest } from '@/types';

export const authApi = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>('/api/auth/login', credentials);
    return response.data;
  },

  register: async (data: LoginRequest & { nombreCompleto: string; email: string }): Promise<AuthResponse> => {
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
};
