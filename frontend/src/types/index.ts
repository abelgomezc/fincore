export interface Usuario {
  id: string;
  username: string;
  nombreCompleto: string;
  email: string;
  roles: string[];
  esActivo: boolean;
  ultimaConexion?: string;
}

export interface UsuarioBackoffice {
  id: number;
  username: string;
  nombreCompleto?: string;
  email?: string;
  roles?: string;
  estado: 'ACTIVO' | 'INACTIVO' | 'SUSPENDIDO' | 'ELIMINADO';
  fechaCreacion?: string;
  fechaActualizacion?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  primerNombre: string;
  segundoNombre?: string;
  primerApellido: string;
  segundoApellido?: string;
  rol: 'CLIENTE' | 'OPERADOR' | 'SUPERVISOR' | 'AUDITOR' | 'ADMIN';
  idCliente?: number;
  deviceId?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  userId: number;
  email: string;
  nombreCompleto: string;
  rol: string;
  estado: string;
  sessionId: string;
  deviceId: string;
}

export interface JwtPayload {
  sub: string;
  exp: number;
  iat: number;
  roles: string[];
  username: string;
}

export interface Cliente {
  id: number;
  tipoCliente: 'PERSONA_NATURAL' | 'PERSONA_JURIDICA';
  primerNombre: string;
  segundoNombre?: string;
  primerApellido: string;
  segundoApellido?: string;
  nombreCompleto?: string;
  fechaNacimiento?: string;
  genero?: string;
  email?: string;
  telefono?: string;
  estado: 'ACTIVO' | 'INACTIVO' | 'BLOQUEADO' | 'SUSPENDIDO' | 'ELIMINADO';
  fechaCreacion: string;
}
