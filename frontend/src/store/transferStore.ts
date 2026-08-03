import { create } from 'zustand';
import { Transferencia, EstadoTransferencia, TransferenciaEstadoDetalle } from '@/types/transfer';
import { transferApi } from '@/api/transferApi';

interface TransferState {
  historial: Transferencia[];
  currentTransfer: Transferencia | null;
  isLoading: boolean;
  error: string | null;
  demoTransfer: Transferencia | null;
}

interface TransferActions {
  fetchHistorial: (idUsuario: string) => Promise<void>;
  fetchTransferencia: (id: string) => Promise<void>;
  setCurrentTransfer: (transfer: Transferencia | null) => void;
  setDemoTransfer: (transfer: Transferencia | null) => void;
  updateDemoEstado: (estado: EstadoTransferencia, paso?: string) => void;
  updateDemoDetalle: (detalle: TransferenciaEstadoDetalle) => void;
  clear: () => void;
}

export const useTransferStore = create<TransferState & TransferActions>((set, get) => ({
  historial: [],
  currentTransfer: null,
  isLoading: false,
  error: null,
  demoTransfer: null,

  fetchHistorial: async (idUsuario) => {
    set({ isLoading: true, error: null });
    // This will be replaced with actual API call
    set({ isLoading: false });
  },

  fetchTransferencia: async (id) => {
    set({ isLoading: true, error: null });
      try {
      const transferencia = await transferApi.getTransferencia(id);
      set({ currentTransfer: transferencia, isLoading: false });
    } catch (error: any) {
      set({ error: error.message || 'Error cargando transferencia', isLoading: false });
    }
  },

  setCurrentTransfer: (transfer) => set({ currentTransfer: transfer }),

  setDemoTransfer: (transfer) => set({ demoTransfer: transfer }),

  updateDemoEstado: (estado, paso) => {
    const current = get().demoTransfer;
    if (!current) {
      const newTransfer: Transferencia = {
        id: 'demo-' + Date.now(),
        numeroTransferencia: '',
        traceId: 'demo-trace-' + Date.now(),
        idCuentaOrigen: 0,
        numeroCuentaOrigen: '',
        idCuentaDestino: 0,
        numeroCuentaDestino: '',
        monto: 0,
        moneda: 'USD',
        concepto: '',
        estado,
        estadoDetalle: [],
        idUsuarioOrigen: '',
        idUsuarioDestino: '',
        fechaCreacion: new Date().toISOString(),
        fechaActualizacion: new Date().toISOString(),
        esDemo: true,
      };
      set({ demoTransfer: newTransfer });
      return;
    }

    set({
      demoTransfer: { ...current, estado, fechaActualizacion: new Date().toISOString() },
    });
  },

  updateDemoDetalle: (detalle) => {
    const current = get().demoTransfer;
    if (!current) return;
    const existing = current.estadoDetalle.find(d => d.paso === detalle.paso);
    let nuevaLista: TransferenciaEstadoDetalle[];
    if (existing) {
      nuevaLista = current.estadoDetalle.map(d =>
        d.paso === detalle.paso ? detalle : d
      );
    } else {
      nuevaLista = [...current.estadoDetalle, detalle];
    }
    set({
      demoTransfer: {
        ...current,
        estadoDetalle: nuevaLista,
        fechaActualizacion: new Date().toISOString(),
      },
    });
  },

  clear: () => set({
    historial: [],
    currentTransfer: null,
    error: null,
    demoTransfer: null,
  }),
}));
