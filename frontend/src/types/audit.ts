export type AccionAuditoria =
  | 'CREACION'
  | 'ACTUALIZACION'
  | 'BLOQUEO'
  | 'DESBLOQUEO'
  | 'VALIDACION'
  | 'RESERVA_FONDOS'
  | 'APLICACION_DEBITO'
  | 'APLICACION_CREDITO'
  | 'REVERSO_ASIENTO'
  | 'EVALUACION_FRAUDE'
  | 'GENERACION_NOTIFICACION'
  | 'EJECUCION_SAGA';

export type ResultadoAuditoria = 'EXITOSO' | 'FALLIDO';

export interface EventoSaga {
  id: number;
  traceId: string;
  paso: string;
  estadoAnterior: string;
  estadoNuevo: string;
  timestamp: string;
  exito: boolean;
  detalle?: string;
}

export interface RegistroAuditoria {
  id: number;
  traceId: string;
  idUsuario: string;
  servicio: string;
  accion: AccionAuditoria;
  entidad: string;
  idEntidad: string;
  resultado: ResultadoAuditoria;
  detalle: string;
  ipOrigen: string;
  userAgent: string;
  duracionMs?: number;
  datosAntes?: Record<string, unknown>;
  datosDespues?: Record<string, unknown>;
  fechaCreacion: string;
  eventosSaga?: EventoSaga[];
}

export interface AuditTrailFilter {
  traceId?: string;
  idUsuario?: string;
  servicio?: string;
  accion?: AccionAuditoria;
  fechaDesde?: string;
  fechaHasta?: string;
  resultado?: ResultadoAuditoria;
}
