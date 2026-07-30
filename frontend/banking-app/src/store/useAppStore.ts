import { create } from 'zustand'
import type { Account, Transaction, FraudScore, LedgerEntry, KafkaEvent, AuditEntry } from '../types'

/**
 * Store global de la aplicación FinCore Banking
 * Gestiona el estado compartido de cuentas, transacciones y datos en tiempo real
 */
interface AppState {
  // Cuentas
  accounts: Account[]
  setAccounts: (accounts: Account[]) => void
  updateAccountBalance: (accountId: string, updates: Partial<Account>) => void

  // Transacciones
  transactions: Transaction[]
  addTransaction: (transaction: Transaction) => void
  updateTransactionStatus: (transactionId: string, status: Transaction['status']) => void

  // Score de fraude
  currentFraudScore: FraudScore | null
  setCurrentFraudScore: (score: FraudScore) => void

  // Asientos contables
  ledgerEntries: LedgerEntry[]
  addLedgerEntries: (entries: LedgerEntry[]) => void

  // Eventos Kafka
  kafkaEvents: KafkaEvent[]
  addKafkaEvent: (event: KafkaEvent) => void

  // Audit trail
  auditTrail: AuditEntry[]
  addAuditEntry: (entry: AuditEntry) => void

  // UI
  isTransferPending: boolean
  setIsTransferPending: (pending: boolean) => void
}

export const useAppStore = create<AppState>((set) => ({
  // Cuentas iniciales (demo)
  accounts: [
    {
      id: 'ACC-001',
      owner: 'Abel Gomez',
      accountNumber: '****4521',
      availableBalance: 125000.00,
      heldBalance: 5000.00,
      bookBalance: 130500.00,
      projectedBalance: 127500.00,
      currency: 'USD',
      status: 'active',
      lastUpdate: new Date().toISOString()
    },
    {
      id: 'ACC-002',
      owner: 'María López',
      accountNumber: '****8832',
      availableBalance: 89000.00,
      heldBalance: 2000.00,
      bookBalance: 91200.00,
      projectedBalance: 89500.00,
      currency: 'USD',
      status: 'active',
      lastUpdate: new Date().toISOString()
    }
  ],

  setAccounts: (accounts) => set({ accounts }),

  updateAccountBalance: (accountId, updates) =>
    set((state) => ({
      accounts: state.accounts.map((acc) =>
        acc.id === accountId ? { ...acc, ...updates, lastUpdate: new Date().toISOString() } : acc
      )
    })),

  // Transacciones
  transactions: [],
  addTransaction: (transaction) =>
    set((state) => ({
      transactions: [transaction, ...state.transactions]
    })),
  updateTransactionStatus: (transactionId, status) =>
    set((state) => ({
      transactions: state.transactions.map((tx) =>
        tx.id === transactionId
          ? {
              ...tx,
              status,
              ...(status === 'SETTLED' || status === 'FAILED' ? { completedAt: new Date().toISOString() } : {})
            }
          : tx
      )
    })),

  // Score de fraude
  currentFraudScore: null,
  setCurrentFraudScore: (currentFraudScore) => set({ currentFraudScore }),

  // Asientos contables
  ledgerEntries: [],
  addLedgerEntries: (ledgerEntries) =>
    set((state) => ({
      ledgerEntries: [...state.ledgerEntries, ...ledgerEntries]
    })),

  // Eventos Kafka
  kafkaEvents: [],
  addKafkaEvent: (kafkaEvents) =>
    set((state) => ({
      kafkaEvents: [kafkaEvents, ...state.kafkaEvents]
    })),

  // Audit trail
  auditTrail: [],
  addAuditEntry: (auditTrail) =>
    set((state) => ({
      auditTrail: [auditTrail, ...state.auditTrail]
    })),

  // UI
  isTransferPending: false,
  setIsTransferPending: (isTransferPending) => set({ isTransferPending })
}))
