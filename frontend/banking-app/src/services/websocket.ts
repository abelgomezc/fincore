/**
 * Servicio WebSocket para demo en tiempo real de FinCore Banking
 * Simula el flujo completo de transferencias con estados y eventos
 */
type MessageHandler = (data: unknown) => void

class WebSocketService {
  private ws: WebSocket | null = null
  private handlers: MessageHandler[] = []
  private reconnectAttempts = 0
  private maxReconnectAttempts = 5
  private reconnectInterval = 3000
  private simulationInterval: number | null = null

  /**
   * Establece la conexión WebSocket al endpoint de tiempo real
   */
  connect(url: string = 'ws://localhost:8080/ws'): void {
    try {
      this.ws = new WebSocket(url)

      this.ws.onopen = () => {
        console.log('[WebSocket] Conexión establecida')
        this.reconnectAttempts = 0
        this.startDemoSimulation()
      }

      this.ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data)
          this.handlers.forEach((handler) => handler(data))
        } catch {
          console.error('[WebSocket] Error parseando mensaje:', event.data)
        }
      }

      this.ws.onclose = () => {
        console.log('[WebSocket] Conexión cerrada')
        this.stopDemoSimulation()
        this.attemptReconnect(url)
      }

      this.ws.onerror = (error) => {
        console.error('[WebSocket] Error:', error)
      }
    } catch (error) {
      console.error('[WebSocket] Error al conectar:', error)
    }
  }

  /**
   * Intenta reconectar automáticamente
   */
  private attemptReconnect(url: string): void {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++
      console.log(`[WebSocket] Reintento ${this.reconnectAttempts}/${this.maxReconnectAttempts}`)
      setTimeout(() => this.connect(url), this.reconnectInterval)
    } else {
      console.log('[WebSocket] Máximo de reintentos alcanzado')
    }
  }

  /**
   * Registra un manejador para mensajes entrantes
   */
  onMessage(handler: MessageHandler): void {
    this.handlers.push(handler)
  }

  /**
   * Elimina un manejador
   */
  offMessage(handler: MessageHandler): void {
    this.handlers = this.handlers.filter((h) => h !== handler)
  }

  /**
   * Envía un mensaje por WebSocket
   */
  send(data: unknown): void {
    if (this.ws?.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(data))
    }
  }

  /**
   * Cierra la conexión
   */
  disconnect(): void {
    this.stopDemoSimulation()
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
  }

  /**
   * Inicia la simulación de demo para el dashboard
   * Genera datos en tiempo real sin necesidad de backend real
   */
  private startDemoSimulation(): void {

    // Simula actualizaciones de saldo cada 3 segundos
    this.simulationInterval = window.setInterval(() => {
      const accountId = Math.random() > 0.5 ? 'ACC-001' : 'ACC-002'
      const fluctuation = (Math.random() - 0.5) * 500
      const message = {
        type: 'balance_update',
        accountId,
        delta: fluctuation,
        timestamp: new Date().toISOString()
      }
      this.handlers.forEach((handler) => handler(message))
    }, 3000)
  }

  /**
   * Detiene la simulación de demo
   */
  private stopDemoSimulation(): void {
    if (this.simulationInterval) {
      clearInterval(this.simulationInterval)
      this.simulationInterval = null
    }
  }
}

/**
 * Instancia singleton del servicio WebSocket
 */
export const wsService = new WebSocketService()

export default WebSocketService
