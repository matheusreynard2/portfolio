package com.prodify.produto_service.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    @Primary
    @Order(1) // Prioridade alta para sobrescrever outras configurações
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/**") // Especificar que este filtro se aplica a todos os requests
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()  // Permitir todas as requisições nos testes
            )
            .oauth2ResourceServer(AbstractHttpConfigurer::disable) // Desabilitar OAuth2
            .sessionManagement(AbstractHttpConfigurer::disable); // Desabilitar session management
        
        return http.build();
    }
} 