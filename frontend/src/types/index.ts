export interface Usuario {
  id: string;
  username: string;
  nombreCompleto: string;
  email: string;
  roles: string[];
  esActivo: boolean;
  ultimaConexion?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  usuario: Usuario;
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
  fechaNacimiento?: string;
  genero?: string;
  email?: string;
  telefono?: string;
  estado: 'ACTIVO' | 'INACTIVO' | 'BLOQUEADO';
  fechaCreacion: string;
}
