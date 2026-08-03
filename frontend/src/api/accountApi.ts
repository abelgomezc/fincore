import apiClient from './axiosConfig';
import { Cuenta, Saldo, Movimiento } from '@/types/account';

export const accountApi = {
  getCuentas: async (idUsuario: string): Promise<Cuenta[]> => {
    const response = await apiClient.get<Cuenta[]>(`/api/cuentas?userId=${idUsuario}`);
    return response.data;
  },

  getCuenta: async (idCuenta: number): Promise<Cuenta> => {
    const response = await apiClient.get<Cuenta>(`/api/cuentas/${idCuenta}`);
    return response.data;
  },

  getSaldo: async (idCuenta: number): Promise<Saldo> => {
    const response = await apiClient.get<Saldo>(`/api/saldos/${idCuenta}`);
    return response.data;
  },

  getMovimientos: async (idCuenta: number, page: number = 0): Promise<Movimiento[]> => {
    const response = await apiClient.get<Movimiento[]>(
      `/api/cuentas/${idCuenta}/movimientos?page=${page}`
    );
    return response.data;
  },

  getBeneficiarios: async (idCuenta: number) => {
    const response = await apiClient.get(`/api/beneficiarios?cuentaId=${idCuenta}`);
    return response.data;
  },
};
