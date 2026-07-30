/**
 * Página principal del dashboard
 * Muestra demo en tiempo real con transferencias, balances, score de fraude,
 * asientos contables, eventos Kafka y audit trail
 */
import { useEffect, useState, useCallback } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useAppStore } from '../store/useAppStore'
import { wsService } from '../services/websocket'
import AccountCard from '../components/AccountCard'
import TransferForm from '../components/TransferForm'
import TransferTimeline from '../components/TransferTimeline'
import FraudScore from '../components/FraudScore'
import LedgerPanel from '../components/LedgerPanel'
import type { Transaction, FraudScore as FraudScoreType, LedgerEntry, KafkaEvent, AuditEntry } from '../types'

// Datos iniciales de demo
const INITIAL_FRAUD_SCORE: FraudScoreType = {
  score: 15,
  riskLevel: 'low',
  factors: [
    'Monto dentro de límites normales',
    'Cuentas registradas previamente',
    'Patrón de transacción conocido',
    'Sin coincidencias en lista de sanciones'
  ],
  explanation: 'La transferencia presenta un riesgo bajo. Se apega a patrones de comportamiento normales del cliente.'
}

function Dashboard() {
  const { accounts, transactions, addTransaction, updateTransactionStatus, currentFraudScore, setCurrentFraudScore, ledgerEntries, addLedgerEntries, addKafkaEvent, addAuditEntry } = useAppStore()
  const [logs, setLogs] = useState<string[]>([])

  const addLog = useCallback((message: string) => {
    setLogs((prev) => [`[${new Date().toLocaleTimeString('es-ES')}] ${message}`, ...prev.slice(0, 49)])
  }, [])

  // Conexión WebSocket para demo en tiempo real
  useEffect(() => {
    // Establecer score inicial de fraude demo
    setCurrentFraudScore(INITIAL_FRAUD_SCORE)

    // Inicializar servicio WebSocket en modo demo
    wsService.connect('ws://localhost:8080/ws')

    wsService.onMessage((data) => {
      const message = data as { type: string; [key: string]: unknown }
      addLog(`WS: ${JSON.stringify(message)}`)

      // Actualizar saldos según mensajes del WebSocket
      if (message.type === 'balance_update' && typeof message.accountId === 'string') {
        const delta = Number(message.delta) || 0
        const account = accounts.find((acc) => acc.id === message.accountId)
        if (account) {
          useAppStore.getState().updateAccountBalance(account.id, {
            availableBalance: account.availableBalance + delta,
            projectedBalance: account.projectedBalance + delta
          })
        }
      }
    })

    return () => {
      wsService.disconnect()
    }
  }, [setCurrentFraudScore, addLog, accounts])

  // Simula una nueva transferencia con todos sus estados
  const handleTransfer = (transferData: { fromAccountId: string; toAccountId: string; amount: number; currency: string }) => {
    if (transferData.amount <= 0) return

    addLog(`Iniciando transferencia: ${transferData.amount} ${transferData.currency}`)

    // Crear transacción inicial
    const newTransaction: Transaction = {
      id: `TXN-${Date.now().toString(36).toUpperCase()}`,
      fromAccountId: transferData.fromAccountId,
      toAccountId: transferData.toAccountId,
      amount: transferData.amount,
      currency: transferData.currency,
      status: 'INITIATED',
      createdAt: new Date().toISOString()
    }

    addTransaction(newTransaction)

    // Audit trail
    const auditEntry: AuditEntry = {
      id: `AUD-${Date.now().toString(36).toUpperCase()}`,
      timestamp: new Date().toISOString(),
      actor: 'system',
      action: 'TRANSFER_CREATED',
      resource: 'transaction',
      resourceId: newTransaction.id,
      details: { amount: transferData.amount, from: transferData.fromAccountId, to: transferData.toAccountId },
      ipAddress: '127.0.0.1'
    }
    addAuditEntry(auditEntry)

    // Simulación del flujo de estados en tiempo real
    const statuses: Transaction['status'][] = [
      'INITIATED', 'VALIDATING', 'DEBIT_PENDING', 'DEBITED',
      'CREDIT_PENDING', 'CREDITED', 'FRAUD_CHECK', 'RECONCILING',
      'LEDGER_POSTED', 'KAFKA_PUBLISHED', 'SETTLED'
    ]

    let currentStatusIndex = 0

    const interval = setInterval(() => {
      if (currentStatusIndex >= statuses.length) {
        clearInterval(interval)
        return
      }

      const status = statuses[currentStatusIndex]
      updateTransactionStatus(newTransaction.id, status)

      addLog(`Estado: ${status}`)

      // Generar asientos contables cuando se publican en ledger
      if (status === 'LEDGER_POSTED') {
        const ledgerEntries: LedgerEntry[] = [
          {
            id: `LEDGER-${Date.now().toString(36).toUpperCase()}`,
            transactionId: newTransaction.id,
            accountCode: '1.1.001',
            debit: transferData.amount,
            credit: 0,
            currency: transferData.currency,
            description: `Transferencia ${newTransaction.id} - Origen`,
            postedAt: new Date().toISOString(),
            posterId: 'SYSTEM'
          },
          {
            id: `LEDGER-${Date.now().toString(36).toUpperCase()}-2`,
            transactionId: newTransaction.id,
            accountCode: '2.1.001',
            debit: 0,
            credit: transferData.amount,
            currency: transferData.currency,
            description: `Transferencia ${newTransaction.id} - Destino`,
            postedAt: new Date().toISOString(),
            posterId: 'SYSTEM'
          }
        ]
        addLedgerEntries(ledgerEntries)
        addLog(`Asientos contables generados: ${ledgerEntries.length}`)
      }

      // Generar evento Kafka
      if (status === 'KAFKA_PUBLISHED') {
        const kafkaEvent: KafkaEvent = {
          id: `KAFKA-${Date.now().toString(36).toUpperCase()}`,
          topic: 'banking.transfers.completed',
          key: newTransaction.id,
          payload: {
            transactionId: newTransaction.id,
            amount: transferData.amount,
            currency: transferData.currency,
            fromAccount: transferData.fromAccountId,
            toAccount: transferData.toAccountId,
            status: 'SETTLED'
          },
          publishedAt: new Date().toISOString(),
          partition: Math.floor(Math.random() * 3),
          offset: Math.floor(Math.random() * 1000)
        }
        addKafkaEvent(kafkaEvent)
        addLog(`Evento Kafka publicado: ${kafkaEvent.topic}`)

        // Actualizar score de fraude para demo
        const newScore = Math.floor(Math.random() * 30) + 10
        setCurrentFraudScore({
          score: newScore,
          riskLevel: newScore < 30 ? 'low' : newScore < 60 ? 'medium' : 'high',
          factors: [
            'Patrón de comportamiento analizado',
            'Montos dentro de parámetros',
            'Historial transaccional positivo'
          ],
          explanation: 'Análisis de fraude completado. Bajo riesgo.'
        })
      }

      currentStatusIndex++
    }, 800)

    return () => clearInterval(interval)
  }

  return (
    <div className="space-y-6">
      {/* Título principal */}
      <div>
        <h2 className="text-2xl font-bold text-slate-900">Dashboard FinCore</h2>
        <p className="text-slate-500 mt-1">Demo en tiempo real del sistema de pagos y contabilidad</p>
      </div>

      {/* Cuentas lado a lado */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {accounts.map((account) => (
          <AccountCard key={account.id} account={account} />
        ))}
      </div>

      {/* Formulario de transferencia y Score de fraude */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <TransferForm accounts={accounts.map((a) => ({ id: a.id, owner: a.owner }))} onTransfer={handleTransfer} />
        </div>
        {currentFraudScore && (
          <FraudScore score={currentFraudScore} />
        )}
      </div>

      {/* Timeline de transferencias */}
      <TransferTimeline transactions={transactions} />

      {/* Asientos contables y Kafka */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <LedgerPanel entries={ledgerEntries} />

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="card-banking p-6"
        >
          <h3 className="text-lg font-semibold text-slate-900 mb-4">Eventos Kafka</h3>

          {useAppStore((state) => state.kafkaEvents).length === 0 ? (
            <p className="text-sm text-slate-500 text-center py-8">
              No hay eventos publicados aún.
            </p>
          ) : (
            <div className="space-y-3">
              {useAppStore((state) => state.kafkaEvents).map((event: KafkaEvent, idx: number) => (
                <motion.div
                  key={event.id}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: idx * 0.1 }}
                  className="bg-slate-50 rounded-lg p-4 border border-slate-100"
                >
                  <div className="flex items-start justify-between">
                    <div>
                      <p className="text-sm font-medium text-slate-900">{event.topic}</p>
                      <p className="text-xs text-slate-500 mt-1">Key: {event.key}</p>
                      <p className="text-xs text-slate-500">
                        Partition: {event.partition} | Offset: {event.offset}
                      </p>
                    </div>
                    <span className="text-xs text-slate-400">
                      {new Date(event.publishedAt).toLocaleTimeString('es-ES')}
                    </span>
                  </div>
                </motion.div>
              ))}
            </div>
          )}
        </motion.div>
      </div>

      {/* Audit trail */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="card-banking p-6"
      >
        <h3 className="text-lg font-semibold text-slate-900 mb-4">Audit Trail</h3>

        <div className="bg-slate-900 rounded-lg p-4 font-mono text-xs max-h-64 overflow-y-auto">
          {logs.length === 0 ? (
            <p className="text-slate-400">Esperando actividad...</p>
          ) : (
            <AnimatePresence>
              {logs.map((log, index) => (
                <motion.div
                  key={`${log}-${index}`}
                  initial={{ opacity: 0, y: -10 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="text-emerald-400 py-1 border-b border-slate-800 last:border-b-0"
                >
                  {log}
                </motion.div>
              ))}
            </AnimatePresence>
          )}
        </div>
      </motion.div>
    </div>
  )
}

export default Dashboard
