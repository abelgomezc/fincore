package com.fincore.backoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class BackofficeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackofficeServiceApplication.class, args);
    }

    @Bean
    RestClient restClient(org.springframework.core.env.Environment env) {
        String clienteUrl = env.getProperty("services.cliente.url", "http://localhost:8082");
        return RestClient.builder()
                .baseUrl(clienteUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
