package com.fincore.fraud.service.rules;

import com.fincore.fraud.entity.PerfilTransaccional;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MontoInusualRuleTest {

    private final MontoInusualRule rule = new MontoInusualRule();

    @Test
    void debeActivarse_cuandoMontoEsTripleDelPromedio() {
        rule.setUmbral(3.0);

        PerfilTransaccional perfil = new PerfilTransaccional();
        perfil.setPromedioMonto30d(BigDecimal.valueOf(100));

        RuleContext context = new RuleContext(
                1L, 1L, 1L,
                BigDecimal.valueOf(500),
                "127.0.0.1", "device1",
                "12345678",
                Optional.of(perfil));

        RuleResult result = rule.evaluate(context);
        assertTrue(result.isActivada());
        assertEquals(15, result.getPuntos());
    }

    @Test
    void noDebeActivarse_cuandoMontoEstaDentroDelRango() {
        rule.setUmbral(3.0);

        PerfilTransaccional perfil = new PerfilTransaccional();
        perfil.setPromedioMonto30d(BigDecimal.valueOf(100));

        RuleContext context = new RuleContext(
                1L, 1L, 1L,
                BigDecimal.valueOf(200),
                "127.0.0.1", "device1",
                "12345678",
                Optional.of(perfil));

        RuleResult result = rule.evaluate(context);
        assertFalse(result.isActivada());
    }

    @Test
    void noDebeActivarse_cuandoNoHayPerfil() {
        rule.setUmbral(3.0);

        RuleContext context = new RuleContext(
                1L, 1L, 1L,
                BigDecimal.valueOf(500),
                "127.0.0.1", "device1",
                "12345678",
                Optional.empty());

        RuleResult result = rule.evaluate(context);
        assertFalse(result.isActivada());
    }
}
