package com.prodify.react.config;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração necessária para o tratamento de exceções.
 * Fornece beans utilizados pelo Spring WebFlux para gerenciar erros.
 */
@Configuration
public class ExceptionHandlerConfig {
    
    /**
     * WebProperties.Resources é necessário para o tratamento de exceções no WebFlux.
     */
    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }
}