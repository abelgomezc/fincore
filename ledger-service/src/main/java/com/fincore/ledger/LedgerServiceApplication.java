package com.fincore.ledger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ledger Service — Ledger de doble partida inmutable.
 *
 * El módulo más importante de FinCore. Gestiona:
 * - Plan de cuentas contables (códigos 1xxx-5xxx)
 * - Asientos contables (INMUTABLES — nunca UPDATE ni DELETE)
 * - Líneas de asiento (INMUTABLES)
 * - Validación de equilibrio (débitos = créditos)
 * - Reversión de asientos (crea nuevos, no modifica)
 * - Estado de cuentas, extractos, balance general
 * - gRPC server para transfer-service
 * - Consumo de eventos Kafka de transferencias
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@SpringBootApplication
public class LedgerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LedgerServiceApplication.class, args);
    }
}
