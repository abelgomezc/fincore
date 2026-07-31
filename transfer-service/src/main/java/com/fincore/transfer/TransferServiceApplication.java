package com.fincore.transfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Clase principal de Transfer Service.
 *
 * Transfer Service — Saga orchestrador para transferencias bancarias.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class TransferServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransferServiceApplication.class, args);
    }
}
