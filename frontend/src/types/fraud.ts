export type DecisionFraude = 'APROBADO' | 'EN_REVISION' | 'RECHAZADO';

export type TipoEntradaListaNegra = 'IP' | 'DISPOSITIVO' | 'USUARIO';

export type ReglaFraudeCodigo =
  | 'MONTO_INUSUAL'
  | 'HORARIO_INUSUAL'
  | 'DISPOSITIVO_NUEVO'
  | 'BENEFICIARIO_NUEVO'
  | 'PAIS_DIFERENTE'
  | 'VELOCIDAD_ALTA'
  | 'LISTA_NEGRA'
  | 'IP_SOSPECHOSA'
  | 'PATRON_FRACCIONADO'
  | 'PRIMER_TRANSFER_GRANDE';

export interface ReglaFraudeInfo {
  codigo: ReglaFraudeCodigo;
  descripcion: string;
  peso: number;
  activa: boolean;
}

export interface ReglaFraudeDetalle {
  codigo: ReglaFraudeCodigo;
  descripcion: string;
  pesoAsignado: number;
  activada: boolean;
  detalle: string;
}

export interface EvaluacionFraude {
  id: number;
  traceId: string;
  numeroTransferencia: string;
  idCuentaOrigen: number;
  monto: number;
  score: number;
  decision: DecisionFraude;
  reglasEvaluadas: ReglaFraudeDetalle[];
  timestamp: string;
  ipOrigen?: string;
  userAgent?: string;
  dispositivoId?: string;
}

export interface ListaNegra {
  id: number;
  tipo: TipoEntradaListaNegra;
  valor: string;
  motivo: string;
  activa: boolean;
  fechaCreacion: string;
  fechaExpiracion?: string;
}

export interface PerfilTransaccional {
  id: number;
  idUsuario: string;
  montoPromedio: number;
  montoMaximoDiario: number;
  montoMaximoMensual: number;
  frecuenciaPromedio: number;
  paisesFrecuentes: string[];
  ultimaActualizacion: string;
}
