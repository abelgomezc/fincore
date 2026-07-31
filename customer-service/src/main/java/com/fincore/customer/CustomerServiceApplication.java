package com.fincore.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Customer Service — Gestión de clientes, KYC y AML.
 *
 * Funciones:
 * - CRUD de clientes (personas naturales y jurídicas)
 * - Validación de cédula ecuatoriana (algoritmo módulo 10)
 * - Gestión de documentos de identidad
 * - Proceso KYC simulado con estados (PENDIENTE, EN_REVISION, APROBADO, RECHAZADO)
 * - Verificación AML básica
 * - Publicación de eventos Kafka
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
