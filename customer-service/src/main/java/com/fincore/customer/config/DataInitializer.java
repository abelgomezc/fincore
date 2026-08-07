package com.fincore.customer.config;

import com.fincore.customer.entity.Cliente;
import com.fincore.customer.enums.EstadoCliente;
import com.fincore.customer.enums.TipoCliente;
import com.fincore.customer.repository.ClienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(ClienteRepository clienteRepository) {
        return args -> {
            if (clienteRepository.findByEmail("abel.gomez@fincore.com").isPresent()
                    && clienteRepository.findByEmail("maria.lopez@fincore.com").isPresent()) {
                log.info("Datos de cliente ya inicializados, omitiendo seed");
                return;
            }

            Cliente cliente1 = new Cliente();
            cliente1.setTipoCliente(TipoCliente.NATURAL);
            cliente1.setEstado(EstadoCliente.ACTIVO);
            cliente1.setPrimerNombre("Abel");
            cliente1.setSegundoNombre("Alejandro");
            cliente1.setPrimerApellido("Gomez");
            cliente1.setSegundoApellido("Salazar");
            cliente1.setFechaNacimiento(LocalDate.of(1990, 5, 15));
            cliente1.setGenero("MASCULINO");
            cliente1.setEmail("abel.gomez@fincore.com");
            cliente1.setTelefono("+593991234567");
            cliente1.setDireccion("Av. Amazonas N34-567, Quito");
            cliente1.setCiudad("Quito");
            cliente1.setPais("EC");
            cliente1.setFechaRegistro(LocalDate.of(2024, 1, 15));

            Cliente cliente2 = new Cliente();
            cliente2.setTipoCliente(TipoCliente.NATURAL);
            cliente2.setEstado(EstadoCliente.ACTIVO);
            cliente2.setPrimerNombre("María");
            cliente2.setSegundoNombre("Fernanda");
            cliente2.setPrimerApellido("López");
            cliente2.setSegundoApellido("García");
            cliente2.setFechaNacimiento(LocalDate.of(1992, 8, 22));
            cliente2.setGenero("FEMENINO");
            cliente2.setEmail("maria.lopez@fincore.com");
            cliente2.setTelefono("+593987654321");
            cliente2.setDireccion("Calle 10 de Agosto N12-34, Guayaquil");
            cliente2.setCiudad("Guayaquil");
            cliente2.setPais("EC");
            cliente2.setFechaRegistro(LocalDate.of(2024, 1, 15));

            clienteRepository.save(cliente1);
            clienteRepository.save(cliente2);

            log.info("Datos semilla de clientes cargados: 2 clientes");
        };
    }
}
