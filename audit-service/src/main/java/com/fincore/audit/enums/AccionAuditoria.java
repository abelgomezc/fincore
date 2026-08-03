package com.fincore.audit.enums;

/**
 * Acciones auditables del sistema FinCore.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public enum AccionAuditoria {
    CREAR_CLIENTE("CREAR_CLIENTE"),
    ACTUALIZAR_CLIENTE("ACTUALIZAR_CLIENTE"),
    CREAR_CUENTA("CREAR_CUENTA"),
    ACTUALIZAR_SALDO("ACTUALIZAR_SALDO"),
    CREAR_ASIENTO("CREAR_ASIENTO"),
    INICIAR_TRANSFERENCIA("INICIAR_TRANSFERENCIA"),
    COMPLETAR_TRANSFERENCIA("COMPLETAR_TRANSFERENCIA"),
    REVERTIR_TRANSFERENCIA("REVERTIR_TRANSFERENCIA"),
    EVALUAR_FRAUDE("EVALUAR_FRAUDE"),
    REGISTRAR_SAGA("REGISTRAR_SAGA"),
    ENVIAR_NOTIFICACION("ENVIAR_NOTIFICACION");

    private final String codigo;

    AccionAuditoria(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
