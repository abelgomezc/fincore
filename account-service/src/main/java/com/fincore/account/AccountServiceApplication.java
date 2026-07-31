package com.fincore.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Account Service — Gestión de cuentas, saldos y CQRS.
 *
 * Funciones:
 * - CRUD de cuentas bancarias
 * - Los 4 tipos de saldo: contable, disponible, retenido, proyectado
 * - CQRS: separación de comandos y consultas
 * - Optimistic locking (@Version) en todas las entidades
 * - gRPC server para comunicación interna (transfer-service)
 * - Caché Redis para consultas de saldo (TTL 300s)
 * - Pessimistic write lock en operaciones críticas
 * - Publicación de eventos Kafka
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@SpringBootApplication
@EnableCaching
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}
