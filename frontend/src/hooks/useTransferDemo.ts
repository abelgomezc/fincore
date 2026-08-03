import { useEffect, useRef, useCallback } from 'react';
import { PasoTransferencia, EstadoTransferencia } from '@/types/transfer';
import { useTransferStore } from '@/store/transferStore';

const PASOS_ORDEN: PasoTransferencia[] = [
  'VALIDAR_DATOS',
  'VERIFICAR_KYC',
  'VALIDAR_LIMITES',
  'EVALUAR_FRAUDE',
  'RESERVAR_FONDOS',
  'CREAR_ASIENTO_DEBITO',
  'APLICAR_DEBITO',
  'APLICAR_CREDITO',
  'LIBERAR_RESERVA',
  'REGISTRAR_AUDITORIA',
  'COBRAR_COMISION',
  'NOTIFICAR',
];

interface DemoEvent {
  type: 'estado' | 'detalle';
  estado?: EstadoTransferencia;
  paso?: PasoTransferencia;
  timestamp: string;
  exito?: boolean;
  mensaje?: string;
  duracionMs?: number;
}

export const useTransferDemo = (autoStart: boolean = false) => {
  const ws = useRef<WebSocket | null>(null);
  const reconnectTimeout = useRef<number | null>(null);
  const { demoTransfer, setDemoTransfer, updateDemoEstado, updateDemoDetalle } = useTransferStore();

  const connect = useCallback(() => {
    if (ws.current && ws.current.readyState === WebSocket.OPEN) return;

    const wsUrl = `${import.meta.env.VITE_WS_URL || ''}/ws/transferencias?idUsuario=demo`;
    ws.current = new WebSocket(wsUrl);

    ws.current.onopen = () => {
      console.log('[WebSocket] Conexión establecida para demo');
    };

    ws.current.onmessage = (event) => {
      const data: DemoEvent = JSON.parse(event.data);

      if (data.type === 'estado') {
        updateDemoEstado(data.estado as EstadoTransferencia, data.paso);
      } else if (data.type === 'detalle') {
        updateDemoDetalle({
          paso: data.paso as PasoTransferencia,
          estado: data.estado as EstadoTransferencia,
          timestamp: data.timestamp,
          exito: data.exito ?? true,
          mensaje: data.mensaje,
          duracionMs: data.duracionMs,
        });
      }
    };

    ws.current.onclose = () => {
      reconnectTimeout.current = window.setTimeout(() => {
        connect();
      }, 3000);
    };

    ws.current.onerror = (error) => {
      console.error('[WebSocket] Error:', error);
    };
  }, [updateDemoEstado, updateDemoDetalle]);

  const disconnect = useCallback(() => {
    if (reconnectTimeout.current) {
      clearTimeout(reconnectTimeout.current);
      reconnectTimeout.current = null;
    }
    if (ws.current) {
      ws.current.close();
      ws.current = null;
    }
  }, []);

  const startDemo = useCallback(() => {
    setDemoTransfer(null);
    if (!ws.current || ws.current.readyState !== WebSocket.OPEN) {
      connect();
    }
  }, [connect, setDemoTransfer]);

  const stopDemo = useCallback(() => {
    disconnect();
    setDemoTransfer(null);
  }, [disconnect, setDemoTransfer]);

  useEffect(() => {
    if (autoStart) {
      startDemo();
    }

    return () => {
      stopDemo();
    };
  }, [autoStart, startDemo, stopDemo]);

  return {
    demoTransfer,
    isConnected: ws.current?.readyState === WebSocket.OPEN,
    startDemo,
    stopDemo,
    reconnect: connect,
  };
};
