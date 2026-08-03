package com.fincore.fraud.service.rules;

import org.springframework.stereotype.Component;

/**
 * Regla 2.3 — DISPOSITIVO_NUEVO (20 puntos).
 * La transferencia proviene de un dispositivo no reconocido.
 *
 * Nota: La verificación real contra Redis se delega al PerfilTransaccional.
 * Si el dispositivo no está en los dispositivos habituales del perfil → activa.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@Component
public class DispositivoNuevoRule implements FraudRule {

    @Override
    public RuleResult evaluate(RuleContext context) {
        String codigo = "DISPOSITIVO_NUEVO";
        int puntos = 20;

        if (context.getDispositivo() == null || context.getDispositivo().isBlank()) {
            return new RuleResult(codigo, puntos, true, "Sin deviceId en la petición");
        }

        if (context.getPerfilOpt().isEmpty()) {
            return new RuleResult(codigo, puntos, true,
                    "Sin perfil transaccional — primera evaluación del cliente");
        }

        String dispositivosHabituales = context.getPerfilOpt().get().getDispositivosHabituales();
        if (dispositivosHabituales == null || dispositivosHabituales.isBlank()) {
            return new RuleResult(codigo, puntos, true, "Sin dispositivos habituales registrados");
        }

        if (dispositivosHabituales.contains(context.getDispositivo())) {
            return new RuleResult(codigo, puntos, false, "Dispositivo conocido en perfil");
        }

        return new RuleResult(codigo, puntos, true,
                "Dispositivo '" + context.getDispositivo() + "' no reconocido en perfil");
    }

    @Override
    public String getCodigo() {
        return "DISPOSITIVO_NUEVO";
    }
}
