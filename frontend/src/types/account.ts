export type TipoCuenta = 'AHORROS' | 'CORRIENTE' | 'INVERSION';

export type EstadoCuenta = 'ACTIVA' | 'BLOQUEADA' | 'INACTIVA' | 'SUSPENDIDA';

export interface Beneficiario {
  id: number;
  idCuenta: number;
  idCliente: number;
  nombreBeneficiario: string;
  tipoBeneficiario: string;
  estado: 'ACTIVO' | 'INACTIVO';
  fechaCreacion: string;
}

export interface Saldo {
  idCuenta: number;
  numeroCuenta: string;
  saldoContable: number;
  saldoDisponible: number;
  saldoRetenido: number;
  saldoProyectado: number;
  estado: EstadoCuenta;
  moneda: string;
  ultimaActualizacion: string;
}

export interface Movimiento {
  id: number;
  idCuenta: number;
  tipoMovimiento: 'DEBITO' | 'CREDITO' | 'RETENCION' | 'LIBERACION' | 'COMISION';
  monto: number;
  saldoAnterior: number;
  saldoNuevo: number;
  descripcion: string;
  referencia: string;
  fechaCreacion: string;
}

export interface Cuenta {
  id: number;
  idCliente: number;
  numeroCuenta: string;
  tipo: TipoCuenta;
  estado: EstadoCuenta;
  saldoContable: number;
  saldoDisponible: number;
  saldoRetenido: number;
  saldoProyectado: number;
  moneda: string;
  fechaApertura: string;
  ultimaActualizacion: string;
  beneficiarios?: Beneficiario[];
  nombrePropietario?: string;
  identificacionPropietario?: string;
}
