export type EstadoTransferencia =
  | 'PENDIENTE'
  | 'VALIDANDO'
  | 'VALIDADA'
  | 'RESERVANDO_FONDOS'
  | 'FONDOS_RESERVADOS'
  | 'EJECUTANDO_DEBITO'
  | 'DEBITO_APLICADO'
  | 'EJECUTANDO_CREDITO'
  | 'CREDITO_APLICADO'
  | 'COMPLETADA'
  | 'REVERTIDA'
  | 'FALLIDA'
  | 'CANCELADA';

export interface CrearTransferenciaRequest {
  idCuentaOrigen: number;
  idCuentaDestino: number;
  monto: number;
  moneda: string;
  concepto: string;
  idUsuarioOrigen: string;
}

export type PasoTransferencia =
  | 'VALIDAR_DATOS'
  | 'VERIFICAR_KYC'
  | 'VALIDAR_LIMITES'
  | 'EVALUAR_FRAUDE'
  | 'RESERVAR_FONDOS'
  | 'CREAR_ASIENTO_DEBITO'
  | 'APLICAR_DEBITO'
  | 'APLICAR_CREDITO'
  | 'LIBERAR_RESERVA'
  | 'REGISTRAR_AUDITORIA'
  | 'COBRAR_COMISION'
  | 'NOTIFICAR';

export interface TransferenciaEstadoDetalle {
  paso: PasoTransferencia;
  estado: EstadoTransferencia;
  timestamp: string;
  exito: boolean;
  mensaje?: string;
  duracionMs?: number;
}

export interface Transferencia {
  id: string;
  numeroTransferencia: string;
  traceId: string;
  idCuentaOrigen: number;
  numeroCuentaOrigen: string;
  idCuentaDestino: number;
  numeroCuentaDestino: string;
  monto: number;
  moneda: string;
  concepto: string;
  estado: EstadoTransferencia;
  estadoDetalle: TransferenciaEstadoDetalle[];
  idUsuarioOrigen: string;
  idUsuarioDestino: string;
  fechaCreacion: string;
  fechaActualizacion: string;
  fechaCompletada?: string;
  motivoFallo?: string;
  esDemo?: boolean;
}
