import { create } from 'zustand';
import { Cuenta, Saldo, Movimiento } from '@/types/account';
import { accountApi } from '@/api/accountApi';

interface AccountState {
  cuentas: Cuenta[];
  saldoActual: Saldo | null;
  movimientos: Movimiento[];
  isLoading: boolean;
  error: string | null;
  selectedCuentaId: number | null;
}

interface AccountActions {
  fetchCuentas: (idUsuario: string) => Promise<void>;
  fetchSaldo: (idCuenta: number) => Promise<void>;
  fetchMovimientos: (idCuenta: number, page?: number) => Promise<void>;
  setSelectedCuenta: (idCuenta: number | null) => void;
  clear: () => void;
}

export const useAccountStore = create<AccountState & AccountActions>((set, get) => ({
  cuentas: [],
  saldoActual: null,
  movimientos: [],
  isLoading: false,
  error: null,
  selectedCuentaId: null,

  fetchCuentas: async (idUsuario) => {
    set({ isLoading: true, error: null });
    try {
      const cuentas = await accountApi.getCuentas(idUsuario);
      set({ cuentas, isLoading: false });
    } catch (error: any) {
      set({ error: error.message || 'Error cargando cuentas', isLoading: false });
    }
  },

  fetchSaldo: async (idCuenta) => {
    set({ isLoading: true });
    try {
      const saldo = await accountApi.getSaldo(idCuenta);
      set({ saldoActual: saldo, isLoading: false });
    } catch (error: any) {
      set({ error: error.message || 'Error cargando saldo', isLoading: false });
    }
  },

  fetchMovimientos: async (idCuenta, page = 0) => {
    set({ isLoading: true });
    try {
      const movimientos = await accountApi.getMovimientos(idCuenta, page);
      if (page === 0) {
        set({ movimientos, isLoading: false });
      } else {
        set({ movimientos: [...get().movimientos, ...movimientos], isLoading: false });
      }
    } catch (error: any) {
      set({ error: error.message || 'Error cargando movimientos', isLoading: false });
    }
  },

  setSelectedCuenta: (idCuenta) => {
    set({ selectedCuentaId: idCuenta });
    if (idCuenta) {
      get().fetchSaldo(idCuenta);
      get().fetchMovimientos(idCuenta);
    }
  },

  clear: () => set({
    cuentas: [],
    saldoActual: null,
    movimientos: [],
    error: null,
    selectedCuentaId: null,
  }),
}));
