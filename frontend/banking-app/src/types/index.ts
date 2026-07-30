/**
 * Tipos TypeScript para la aplicación FinCore Banking
 */

/**
 * Cuenta bancaria con diferentes tipos de saldos
 */
export interface Account {
  id: string
  owner: string
  accountNumber: string
  availableBalance: number
  heldBalance: number
  bookBalance: number
  projectedBalance: number
  currency: string
  status: 'active' | 'frozen' | 'closed'
  lastUpdate: string
}

/**
 * Transacción entre cuentas
 */
export interface Transaction {
  id: string
  fromAccountId: string
  toAccountId: string
  amount: number
  currency: string
  status: TransactionStatus
  createdAt: string
  completedAt?: string
}

/**
 * Estados posibles de una transacción en el flujo FinCore
 */
export type TransactionStatus =
  | 'INITIATED'
  | 'VALIDATING'
  | 'DEBIT_PENDING'
  | 'DEBITED'
  | 'CREDIT_PENDING'
  | 'CREDITED'
  | 'FRAUD_CHECK'
  | 'RECONCILING'
  | 'LEDGER_POSTED'
  | 'KAFKA_PUBLISHED'
  | 'SETTLED'
  | 'FAILED'

/**
 * Score de fraude con detalle
 */
export interface FraudScore {
  score: number
  riskLevel: 'low' | 'medium' | 'high' | 'critical'
  factors: string[]
  explanation: string
}

/**
 * Asiento contable generado por una transacción
 */
export interface LedgerEntry {
  id: string
  transactionId: string
  accountCode: string
  debit: number
  credit: number
  currency: string
  description: string
  postedAt: string
  posterId: string
}

/**
 * Evento publicado en Kafka
 */
export interface KafkaEvent {
  id: string
  topic: string
  key: string
  payload: Record<string, unknown>
  publishedAt: string
  partition: number
  offset: number
}

/**
 * Entrada de audit trail
 */
export interface AuditEntry {
  id: string
  timestamp: string
  actor: string
  action: string
  resource: string
  resourceId: string
  details: Record<string, unknown>
  ipAddress: string
}

/**
 * Formulario de transferencia
 */
export interface TransferFormData {
  fromAccountId: string
  toAccountId: string
  amount: number
  currency: string
  concept: string
}
