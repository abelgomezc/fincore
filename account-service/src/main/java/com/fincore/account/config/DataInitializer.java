package com.fincore.account.config;

import com.fincore.account.entity.Cuenta;
import com.fincore.account.entity.TipoCuenta;
import com.fincore.account.enums.EstadoCuenta;
import com.fincore.account.repository.CuentaRepository;
import com.fincore.account.repository.TipoCuentaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(CuentaRepository cuentaRepository,
                                      TipoCuentaRepository tipoCuentaRepository) {
        return args -> {
            if (cuentaRepository.findByNumeroCuenta("202600000001").isPresent()
                    && cuentaRepository.findByNumeroCuenta("202600000002").isPresent()) {
                log.info("Datos de cuenta ya inicializados, omitiendo seed");
                return;
            }

            TipoCuenta tipoAhorros = tipoCuentaRepository.findByCodigo("CC")
                    .orElseThrow(() -> new IllegalStateException("Tipo de cuenta CC no encontrado"));

            Cuenta cuenta1 = new Cuenta();
            cuenta1.setNumeroCuenta("202600000001");
            cuenta1.setIdCliente(1L);
            cuenta1.setTipoCuenta(tipoAhorros);
            cuenta1.setEstado(EstadoCuenta.ACTIVA);
            cuenta1.setMoneda("USD");
            cuenta1.setSaldoContable(new BigDecimal("1000.00"));
            cuenta1.setSaldoDisponible(new BigDecimal("1000.00"));
            cuenta1.setSaldoRetenido(BigDecimal.ZERO);
            cuenta1.setSaldoProyectado(new BigDecimal("1000.00"));
            cuenta1.setFechaApertura(LocalDate.of(2024, 1, 15));

            Cuenta cuenta2 = new Cuenta();
            cuenta2.setNumeroCuenta("202600000002");
            cuenta2.setIdCliente(2L);
            cuenta2.setTipoCuenta(tipoAhorros);
            cuenta2.setEstado(EstadoCuenta.ACTIVA);
            cuenta2.setMoneda("USD");
            cuenta2.setSaldoContable(new BigDecimal("500.00"));
            cuenta2.setSaldoDisponible(new BigDecimal("500.00"));
            cuenta2.setSaldoRetenido(BigDecimal.ZERO);
            cuenta2.setSaldoProyectado(new BigDecimal("500.00"));
            cuenta2.setFechaApertura(LocalDate.of(2024, 1, 15));

            cuentaRepository.save(cuenta1);
            cuentaRepository.save(cuenta2);

            log.info("Datos semilla de cuentas cargados: 2 cuentas");
        };
    }
}
