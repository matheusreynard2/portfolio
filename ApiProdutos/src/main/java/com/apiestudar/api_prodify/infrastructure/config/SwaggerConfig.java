package com.apiestudar.api_prodify.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.customizers.OpenApiCustomizer;

/*
 * 
 * http://localhost:8080/swagger-ui.html
ou
http://localhost:8080/swagger-ui/index.html <-- ESSE É O QUE FUNCIONA
 * 
 */

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("PRODIFY API")
                .version("1.0.0")
                .description("API do sistema de gerenciamento de produtos Prodify")
            );
    }

    @Bean
    public OpenApiCustomizer removeSchemasCustomizer() {
        return openApi -> {
            Components components = openApi.getComponents();
            if (components != null) {
                components.setSchemas(null);
            }
        };
    }
} 