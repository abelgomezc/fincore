package com.fincore.customer.service;

/**
 * Interfaz del servicio de KYC.
 *
 * Gestiona el proceso de verificación KYC (Know Your Customer)
 * y AML (Anti-Money Laundering).
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public interface KycService {

    void iniciarVerificacion(Long idCliente);

    void aprobarKyc(Long idCliente, String verificadoPor, String observaciones);

    void rechazarKyc(Long idCliente, String verificadoPor, String observaciones);

    void verificarDocumentos(Long idCliente);

    boolean estaKycAprobado(Long idCliente);
}
